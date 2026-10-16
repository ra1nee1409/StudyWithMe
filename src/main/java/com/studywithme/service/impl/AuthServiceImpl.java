package com.studywithme.service.impl;

import com.studywithme.dto.request.DangKyRequest;
import com.studywithme.dto.request.DangNhapRequest;
import com.studywithme.dto.response.JwtAuthResponse;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.TrangThaiGiangVien;
import com.studywithme.entity.enums.TrangThaiNguoiDung;
import com.studywithme.entity.enums.VaiTroName;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import com.studywithme.security.JwtTokenProvider;
import com.studywithme.security.UserPrincipal;
import com.studywithme.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           NguoiDungRepository nguoiDungRepository,
                           VaiTroRepository vaiTroRepository,
                           HoSoGiangVienRepository hoSoGiangVienRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.nguoiDungRepository = nguoiDungRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public JwtAuthResponse dangNhap(DangNhapRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMatKhau())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JwtAuthResponse.builder()
                .accessToken(jwt)
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .hoTen(userPrincipal.getHoTen())
                .danhSachVaiTro(roles)
                .build();
    }

    @Override
    @Transactional
    public String dangKy(DangKyRequest request) {
        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được sử dụng trên hệ thống: " + request.getEmail());
        }

        Set<VaiTro> danhSachVaiTro = new HashSet<>();

        // Mặc định luôn có vai trò Học viên
        VaiTro roleHocVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseThrow(() -> new IllegalStateException("Chưa thiết lập vai trò ROLE_HOC_VIEN trong hệ thống"));
        danhSachVaiTro.add(roleHocVien);

        // Nếu đăng ký làm giảng viên
        if (request.isDangKyLamGiangVien()) {
            VaiTro roleGiangVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                    .orElseThrow(() -> new IllegalStateException("Chưa thiết lập vai trò ROLE_GIANG_VIEN trong hệ thống"));
            danhSachVaiTro.add(roleGiangVien);
        }

        NguoiDung nguoiDung = NguoiDung.builder()
                .hoTen(request.getHoTen())
                .email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .soDienThoai(request.getSoDienThoai())
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(danhSachVaiTro)
                .build();

        NguoiDung savedUser = nguoiDungRepository.save(nguoiDung);

        // Tạo hồ sơ giảng viên chờ duyệt nếu đăng ký làm gia sư
        if (request.isDangKyLamGiangVien()) {
            HoSoGiangVien hoSo = HoSoGiangVien.builder()
                    .nguoiDung(savedUser)
                    .tieuDeChuyenMon("Gia sư / Giảng viên mới đăng ký")
                    .soDuVi(BigDecimal.ZERO)
                    .trangThaiDuyet(TrangThaiGiangVien.CHO_DUYET)
                    .build();
            hoSoGiangVienRepository.save(hoSo);
        }

        return "Đăng ký tài khoản thành công!";
    }
}

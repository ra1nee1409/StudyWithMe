package com.studywithme.service.impl;

import com.studywithme.dto.request.DangKyRequest;
import com.studywithme.dto.request.DangNhapRequest;
import com.studywithme.dto.response.JwtAuthResponse;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
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
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getMatKhau())
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
    @Transactional(readOnly = true)
    public boolean kiemTraEmailTonTai(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return nguoiDungRepository.existsByEmailIgnoreCase(email.trim().toLowerCase());
    }

    @Override
    @Transactional
    public String dangKy(DangKyRequest request) {
        String normalizedEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Địa chỉ email không được để trống.");
        }

        // Kiểm tra chặt chẽ tính duy nhất của email (không phân biệt hoa thường)
        if (nguoiDungRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email '" + normalizedEmail + "' đã được sử dụng trên hệ thống. Mỗi tài khoản chỉ được dùng 1 email duy nhất!");
        }

        Set<VaiTro> danhSachVaiTro = new HashSet<>();

        // Mặc định mọi tài khoản mới đăng ký đều là người dùng/học viên
        VaiTro roleHocVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseThrow(() -> new IllegalStateException("Chưa thiết lập vai trò ROLE_HOC_VIEN trong hệ thống"));
        danhSachVaiTro.add(roleHocVien);

        NguoiDung nguoiDung = NguoiDung.builder()
                .hoTen(request.getHoTen() != null ? request.getHoTen().trim() : "")
                .email(normalizedEmail)
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .soDienThoai(request.getSoDienThoai() != null ? request.getSoDienThoai().trim() : null)
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(danhSachVaiTro)
                .build();

        nguoiDungRepository.save(nguoiDung);

        return "Đăng ký tài khoản thành công!";
    }
}

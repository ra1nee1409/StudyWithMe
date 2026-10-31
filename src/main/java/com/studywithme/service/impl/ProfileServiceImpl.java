package com.studywithme.service.impl;

import com.studywithme.dto.user.CapNhatHoSoRequest;
import com.studywithme.dto.user.DoiMatKhauRequest;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public NguoiDung layThongTinNguoiDung(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email xác thực không hợp lệ.");
        }
        return nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản với email: " + email));
    }

    @Override
    @Transactional
    public NguoiDung capNhatThongTin(String email, CapNhatHoSoRequest request) {
        NguoiDung user = layThongTinNguoiDung(email);

        // 1. Cập nhật thông tin cơ bản của Người Dùng (Tuyệt đối không đổi email)
        if (request.getHoTen() != null && !request.getHoTen().isBlank()) {
            user.setHoTen(request.getHoTen().trim());
        }
        if (request.getSoDienThoai() != null) {
            user.setSoDienThoai(request.getSoDienThoai().trim());
        }
        if (request.getAnhDaiDien() != null && !request.getAnhDaiDien().isBlank()) {
            user.setAnhDaiDien(request.getAnhDaiDien().trim());
        }
        user = nguoiDungRepository.save(user);

        // 2. Nếu là Giảng viên / Gia sư, đồng thời cập nhật Hồ sơ giảng dạy
        hoSoGiangVienRepository.findByNguoiDungId(user.getId()).ifPresent(gv -> {
            if (request.getTieuDeChuyenMon() != null) {
                gv.setTieuDeChuyenMon(request.getTieuDeChuyenMon().trim());
            }
            if (request.getGioiThieuBanThan() != null) {
                gv.setGioiThieuBanThan(request.getGioiThieuBanThan().trim());
            }
            hoSoGiangVienRepository.save(gv);
        });

        log.info("Cập nhật thông tin cá nhân thành công cho tài khoản: {}", email);
        return user;
    }

    @Override
    @Transactional
    public void doiMatKhau(String email, DoiMatKhauRequest request) {
        NguoiDung user = layThongTinNguoiDung(email);

        // 1. Xác thực mật khẩu cũ
        if (!passwordEncoder.matches(request.getMatKhauHienTai(), user.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác.");
        }

        // 2. Kiểm tra mật khẩu mới
        if (!request.getMatKhauMoi().equals(request.getXacNhanMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không trùng khớp.");
        }

        if (passwordEncoder.matches(request.getMatKhauMoi(), user.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu hiện tại.");
        }

        // 3. Mã hóa và lưu mật khẩu mới
        user.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
        nguoiDungRepository.save(user);
        log.info("Đổi mật khẩu thành công cho tài khoản: {}", email);
    }
}


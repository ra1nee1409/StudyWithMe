package com.studywithme;

import com.studywithme.dto.user.CapNhatHoSoRequest;
import com.studywithme.dto.user.DoiMatKhauRequest;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import com.studywithme.service.AdminNguoiDungService;
import com.studywithme.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ProfileAndUserManagementIntegrationTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AdminNguoiDungService adminNguoiDungService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("1. Học viên cập nhật thông tin cá nhân thành công - Email giữ nguyên")
    void testCapNhatThongTinHocVienKhongDoiEmail() {
        String testEmail = "student_profile_" + UUID.randomUUID() + "@studywithme.vn";
        NguoiDung user = nguoiDungRepository.save(NguoiDung.builder()
                .email(testEmail)
                .matKhau(passwordEncoder.encode("dummy_test_pass"))
                .hoTen("Tên Cũ")
                .soDienThoai("0900000001")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        CapNhatHoSoRequest req = CapNhatHoSoRequest.builder()
                .hoTen("Tên Mới Sau Cập Nhật")
                .soDienThoai("0988889999")
                .anhDaiDien("https://example.com/new_avatar.jpg")
                .build();

        NguoiDung updated = profileService.capNhatThongTin(testEmail, req);

        assertThat(updated.getHoTen()).isEqualTo("Tên Mới Sau Cập Nhật");
        assertThat(updated.getSoDienThoai()).isEqualTo("0988889999");
        assertThat(updated.getAnhDaiDien()).isEqualTo("https://example.com/new_avatar.jpg");
        // Đảm bảo email tuyệt đối không đổi
        assertThat(updated.getEmail()).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("2. Đổi mật khẩu thành công khi nhập đúng mật khẩu cũ")
    void testDoiMatKhauThanhCong() {
        String testEmail = "student_pass_" + UUID.randomUUID() + "@studywithme.vn";
        NguoiDung user = nguoiDungRepository.save(NguoiDung.builder()
                .email(testEmail)
                .matKhau(passwordEncoder.encode("old_test_password_123"))
                .hoTen("Học viên Đổi Mật Khẩu")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        DoiMatKhauRequest req = DoiMatKhauRequest.builder()
                .matKhauHienTai("old_test_password_123")
                .matKhauMoi("new_test_password_456")
                .xacNhanMatKhau("new_test_password_456")
                .build();

        profileService.doiMatKhau(testEmail, req);

        NguoiDung updated = nguoiDungRepository.findByEmail(testEmail).orElseThrow();
        assertThat(passwordEncoder.matches("new_test_password_456", updated.getMatKhau())).isTrue();
    }

    @Test
    @DisplayName("3. Đổi mật khẩu thất bại khi mật khẩu cũ không đúng hoặc xác nhận không khớp")
    void testDoiMatKhauSaiMatKhauCu() {
        String testEmail = "student_wrongpass_" + UUID.randomUUID() + "@studywithme.vn";
        nguoiDungRepository.save(NguoiDung.builder()
                .email(testEmail)
                .matKhau(passwordEncoder.encode("correct_test_password_123"))
                .hoTen("Học viên")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        // Sai mật khẩu cũ
        DoiMatKhauRequest reqSaiCu = DoiMatKhauRequest.builder()
                .matKhauHienTai("wrong_test_password_123")
                .matKhauMoi("new_test_password_456")
                .xacNhanMatKhau("new_test_password_456")
                .build();

        assertThrows(IllegalArgumentException.class, () -> profileService.doiMatKhau(testEmail, reqSaiCu));

        // Xác nhận mật khẩu không khớp
        DoiMatKhauRequest reqKhongKhop = DoiMatKhauRequest.builder()
                .matKhauHienTai("correct_test_password_123")
                .matKhauMoi("new_test_password_456")
                .xacNhanMatKhau("different_test_password_789")
                .build();

        assertThrows(IllegalArgumentException.class, () -> profileService.doiMatKhau(testEmail, reqKhongKhop));
    }

    @Test
    @DisplayName("4. Admin xóa vĩnh viễn tài khoản người dùng khỏi CSDL (Hard Delete)")
    void testAdminXoaNguoiDungVinhVien() {
        String testEmail = "user_to_delete_" + UUID.randomUUID() + "@studywithme.vn";
        NguoiDung user = nguoiDungRepository.save(NguoiDung.builder()
                .email(testEmail)
                .matKhau(passwordEncoder.encode("dummy_test_pass"))
                .hoTen("Người dùng cần xóa")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        Long userId = user.getId();
        assertThat(nguoiDungRepository.existsById(userId)).isTrue();

        adminNguoiDungService.xoaNguoiDungVinhVien(userId, "admin@studywithme.vn");

        assertThat(nguoiDungRepository.existsById(userId)).isFalse();
    }

    @Test
    @DisplayName("5. Chặn tuyệt đối xóa tài khoản Quản trị viên gốc admin@studywithme.vn")
    void testChanXoaAdminGoc() {
        NguoiDung admin = nguoiDungRepository.findByEmail("admin@studywithme.vn").orElseGet(() ->
                nguoiDungRepository.save(NguoiDung.builder()
                        .email("admin@studywithme.vn")
                        .matKhau(passwordEncoder.encode("Admin@123456"))
                        .hoTen("Quản trị viên")
                        .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                        .build())
        );

        assertThrows(IllegalStateException.class, () ->
                adminNguoiDungService.xoaNguoiDungVinhVien(admin.getId(), "another_admin@studywithme.vn")
        );
    }

    @Test
    @DisplayName("6. Chặn admin tự xóa tài khoản của chính mình khi đang đăng nhập")
    void testChanAdminTuXoaChinhMinh() {
        String selfEmail = "active_admin_" + UUID.randomUUID() + "@studywithme.vn";
        NguoiDung admin = nguoiDungRepository.save(NguoiDung.builder()
                .email(selfEmail)
                .matKhau(passwordEncoder.encode("Admin@123456"))
                .hoTen("Admin Đang Đăng Nhập")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        assertThrows(IllegalStateException.class, () ->
                adminNguoiDungService.xoaNguoiDungVinhVien(admin.getId(), selfEmail)
        );
    }
}


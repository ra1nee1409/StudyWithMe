package com.studywithme.config;

import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.TrangThaiNguoiDung;
import com.studywithme.entity.enums.VaiTroName;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final VaiTroRepository vaiTroRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(VaiTroRepository vaiTroRepository,
                      NguoiDungRepository nguoiDungRepository,
                      HoSoGiangVienRepository hoSoGiangVienRepository,
                      PasswordEncoder passwordEncoder) {
        this.vaiTroRepository = vaiTroRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Khởi tạo 3 vai trò cơ bản nếu chưa tồn tại
        VaiTro roleQuanTri = initRole(VaiTroName.ROLE_QUAN_TRI, "Quản trị viên hệ thống");
        VaiTro roleGiangVien = initRole(VaiTroName.ROLE_GIANG_VIEN, "Giảng viên / Gia sư trực tuyến");
        VaiTro roleHocVien = initRole(VaiTroName.ROLE_HOC_VIEN, "Học viên nền tảng");

        // 2. Khởi tạo tài khoản Admin mặc định nếu chưa tồn tại
        String adminEmail = "admin@studywithme.vn";
        if (!nguoiDungRepository.existsByEmail(adminEmail)) {
            NguoiDung admin = NguoiDung.builder()
                    .email(adminEmail)
                    .matKhau(passwordEncoder.encode("Admin@123456"))
                    .hoTen("Quản Trị Viên Hệ Thống")
                    .soDienThoai("0987654321")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(Set.of(roleQuanTri))
                    .build();

            nguoiDungRepository.save(admin);
            logger.info(">>> Đã khởi tạo tài khoản Quản trị viên mặc định: {} / Admin@123456", adminEmail);
        }

        // 3. Khởi tạo tài khoản Giảng viên mẫu
        String tutorEmail = "tutor@studywithme.vn";
        if (!nguoiDungRepository.existsByEmail(tutorEmail)) {
            NguoiDung tutor = NguoiDung.builder()
                    .email(tutorEmail)
                    .matKhau(passwordEncoder.encode("Tutor@123456"))
                    .hoTen("Thầy Hoàng Văn Nam")
                    .soDienThoai("0912345678")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(Set.of(roleGiangVien))
                    .build();

            NguoiDung savedTutor = nguoiDungRepository.save(tutor);

            com.studywithme.entity.HoSoGiangVien hoSo = com.studywithme.entity.HoSoGiangVien.builder()
                    .nguoiDung(savedTutor)
                    .tieuDeChuyenMon("Thạc sĩ Sư Phạm Toán - 7 năm KN Luyện Thi")
                    .gioiThieuBanThan("Tốt nghiệp thủ khoa ĐH Sư Phạm Hà Nội. Phương pháp giảng dạy trực quan, tư duy Casio và sơ đồ Mindmap.")
                    .trangThaiDuyet(com.studywithme.entity.enums.TrangThaiGiangVien.DA_DUYET)
                    .soDuVi(java.math.BigDecimal.ZERO)
                    .build();

            hoSoGiangVienRepository.save(hoSo);
            logger.info(">>> Đã khởi tạo tài khoản Giảng viên mẫu: {} / Tutor@123456", tutorEmail);
        }

        // 4. Khởi tạo tài khoản Học sinh mẫu
        String studentEmail = "student@studywithme.vn";
        if (!nguoiDungRepository.existsByEmail(studentEmail)) {
            NguoiDung student = NguoiDung.builder()
                    .email(studentEmail)
                    .matKhau(passwordEncoder.encode("Student@123456"))
                    .hoTen("Nguyễn Văn Be")
                    .soDienThoai("0988776655")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(Set.of(roleHocVien))
                    .build();

            nguoiDungRepository.save(student);
            logger.info(">>> Đã khởi tạo tài khoản Học viên mẫu: {} / Student@123456", studentEmail);
        }
    }

    private VaiTro initRole(VaiTroName vaiTroName, String moTa) {
        return vaiTroRepository.findByTenVaiTro(vaiTroName).orElseGet(() -> {
            VaiTro vaiTro = VaiTro.builder()
                    .tenVaiTro(vaiTroName)
                    .moTa(moTa)
                    .build();
            logger.info(">>> Đã khởi tạo vai trò: {}", vaiTroName);
            return vaiTroRepository.save(vaiTro);
        });
    }
}

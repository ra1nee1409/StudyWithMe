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
    private final com.studywithme.repository.BannerQuangCaoRepository bannerQuangCaoRepository;
    private final com.studywithme.repository.DonHangRepository donHangRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(VaiTroRepository vaiTroRepository,
                      NguoiDungRepository nguoiDungRepository,
                      HoSoGiangVienRepository hoSoGiangVienRepository,
                      com.studywithme.repository.BannerQuangCaoRepository bannerQuangCaoRepository,
                      com.studywithme.repository.DonHangRepository donHangRepository,
                      PasswordEncoder passwordEncoder) {
        this.vaiTroRepository = vaiTroRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.bannerQuangCaoRepository = bannerQuangCaoRepository;
        this.donHangRepository = donHangRepository;
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

        // 5. Khởi tạo Banner quảng cáo mẫu nếu chưa có banner nào
        if (bannerQuangCaoRepository.count() == 0) {
            com.studywithme.entity.BannerQuangCao b1 = com.studywithme.entity.BannerQuangCao.builder()
                    .tieuDe("Khai Giảng Khóa Học Luyện Thi Vào Lớp 10 Chuyên Toàn Diện")
                    .linkAnhBanner("https://images.unsplash.com/photo-1509062522246-3755977927d7?w=1200&auto=format&fit=crop&q=80")
                    .linkChuyenHuong("/#lop-1-kem-1")
                    .thuTuHienThi(1)
                    .kichHoat(true)
                    .build();

            com.studywithme.entity.BannerQuangCao b2 = com.studywithme.entity.BannerQuangCao.builder()
                    .tieuDe("Bứt Phá Điểm Số Toán & Khoa Học Tự Nhiên Cùng Thủ Khoa")
                    .linkAnhBanner("https://images.unsplash.com/photo-1427504494785-3a9ca7044f45?w=1200&auto=format&fit=crop&q=80")
                    .linkChuyenHuong("/#tim-gia-su")
                    .thuTuHienThi(2)
                    .kichHoat(true)
                    .build();

            com.studywithme.entity.BannerQuangCao b3 = com.studywithme.entity.BannerQuangCao.builder()
                    .tieuDe("Luyện Thi Chứng Chỉ IELTS Academic 7.0+ 1-Kèm-1 Linh Hoạt")
                    .linkAnhBanner("https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=1200&auto=format&fit=crop&q=80")
                    .linkChuyenHuong("/#tim-gia-su")
                    .thuTuHienThi(3)
                    .kichHoat(true)
                    .build();

            bannerQuangCaoRepository.saveAll(java.util.List.of(b1, b2, b3));
            logger.info(">>> Đã khởi tạo 3 banner mẫu thành công.");
        }

        // 6. Khởi tạo đơn hàng mẫu hoàn thành nếu chưa có đơn hàng nào (cho biểu đồ doanh thu)
        if (donHangRepository.count() == 0) {
            NguoiDung student = nguoiDungRepository.findByEmail(studentEmail).orElse(null);
            if (student != null) {
                int currentYear = 2026;
                long[][] samples = {
                    {1, 15000000}, {2, 18500000}, {3, 24000000}, {4, 21000000},
                    {5, 29500000}, {6, 35000000}, {7, 42000000}, {8, 38000000},
                    {9, 48000000}, {10, 52000000}
                };

                for (long[] s : samples) {
                    int month = (int) s[0];
                    java.math.BigDecimal amount = java.math.BigDecimal.valueOf(s[1]);
                    java.time.LocalDateTime createdDate = java.time.LocalDateTime.of(currentYear, month, 15, 10, 30);

                    com.studywithme.entity.DonHang dh = com.studywithme.entity.DonHang.builder()
                            .maDonHang("DH-" + currentYear + String.format("%02d", month) + "-001")
                            .nguoiDung(student)
                            .tongTienThanhToan(amount)
                            .phuongThucThanhToan(com.studywithme.entity.enums.PhuongThucThanhToan.VNPAY)
                            .trangThaiThanhToan(com.studywithme.entity.enums.TrangThaiThanhToan.DA_THANH_TOAN)
                            .ngayThanhToan(createdDate)
                            .ngayTaoDon(createdDate)
                            .build();

                    donHangRepository.save(dh);
                }
                logger.info(">>> Đã khởi tạo dữ liệu đơn hàng mẫu cho năm {}", currentYear);
            }
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

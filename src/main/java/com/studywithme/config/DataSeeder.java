package com.studywithme.config;

import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final VaiTroRepository vaiTroRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final com.studywithme.repository.BannerQuangCaoRepository bannerQuangCaoRepository;
    private final com.studywithme.repository.DonHangRepository donHangRepository;
    private final com.studywithme.repository.DanhMucRepository danhMucRepository;
    private final PasswordEncoder passwordEncoder;

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
                    .trangThaiDuyet(com.studywithme.entity.enums.admin.TrangThaiGiangVien.DA_DUYET)
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
                            .phuongThucThanhToan(com.studywithme.entity.enums.shared.PhuongThucThanhToan.VNPAY)
                            .trangThaiThanhToan(com.studywithme.entity.enums.shared.TrangThaiThanhToan.DA_THANH_TOAN)
                            .ngayThanhToan(createdDate)
                            .ngayTaoDon(createdDate)
                            .build();

                    donHangRepository.save(dh);
                }
                logger.info(">>> Đã khởi tạo dữ liệu đơn hàng mẫu cho năm {}", currentYear);
            }
        }

        // 7. Khởi tạo cây danh mục chuẩn (Tiểu học, THCS, THPT, Ngoại ngữ) nếu chưa có danh mục nào
        if (danhMucRepository.count() == 0) {
            initDanhMucChuan();
        }
    }

    private void initDanhMucChuan() {
        // Cấp 1: Tiểu học
        com.studywithme.entity.DanhMuc cap1 = danhMucRepository.save(com.studywithme.entity.DanhMuc.builder()
                .tenDanhMuc("Tiểu học (Cấp 1)")
                .duongDanSlug("tieu-hoc-cap-1")
                .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.CAP_HOC_K12)
                .linkIcon("child_care")
                .thuTuHienThi(1)
                .kichHoat(true)
                .build());

        danhMucRepository.saveAll(java.util.List.of(
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Toán Tiểu học")
                        .duongDanSlug("toan-tieu-hoc")
                        .danhMucCha(cap1)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("calculate")
                        .thuTuHienThi(1)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Tiếng Việt & Tập đọc")
                        .duongDanSlug("tieng-viet-tap-doc")
                        .danhMucCha(cap1)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("menu_book")
                        .thuTuHienThi(2)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Tiếng Anh Trẻ em")
                        .duongDanSlug("tieng-anh-tre-em")
                        .danhMucCha(cap1)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("translate")
                        .thuTuHienThi(3)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Luyện thi vào Lớp 6 CLC")
                        .duongDanSlug("luyen-thi-vao-lop-6-clc")
                        .danhMucCha(cap1)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("school")
                        .thuTuHienThi(4)
                        .kichHoat(true)
                        .build()
        ));

        // Cấp 2: THCS
        com.studywithme.entity.DanhMuc cap2 = danhMucRepository.save(com.studywithme.entity.DanhMuc.builder()
                .tenDanhMuc("THCS (Cấp 2)")
                .duongDanSlug("thcs-cap-2")
                .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.CAP_HOC_K12)
                .linkIcon("menu_book")
                .thuTuHienThi(2)
                .kichHoat(true)
                .build());

        danhMucRepository.saveAll(java.util.List.of(
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Toán THCS (Lớp 6-9)")
                        .duongDanSlug("toan-thcs")
                        .danhMucCha(cap2)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("calculate")
                        .thuTuHienThi(1)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Ngữ Văn THCS")
                        .duongDanSlug("ngu-van-thcs")
                        .danhMucCha(cap2)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("edit_note")
                        .thuTuHienThi(2)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Tiếng Anh THCS")
                        .duongDanSlug("tieng-anh-thcs")
                        .danhMucCha(cap2)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("translate")
                        .thuTuHienThi(3)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Khoa học Tự nhiên (Lý - Hóa - Sinh)")
                        .duongDanSlug("khoa-hoc-tu-nhien")
                        .danhMucCha(cap2)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("science")
                        .thuTuHienThi(4)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Luyện thi vào 10 Chuyên")
                        .duongDanSlug("luyen-thi-vao-10-chuyen")
                        .danhMucCha(cap2)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("military_tech")
                        .thuTuHienThi(5)
                        .kichHoat(true)
                        .build()
        ));

        // Cấp 3: THPT
        com.studywithme.entity.DanhMuc cap3 = danhMucRepository.save(com.studywithme.entity.DanhMuc.builder()
                .tenDanhMuc("THPT (Cấp 3)")
                .duongDanSlug("thpt-cap-3")
                .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.CAP_HOC_K12)
                .linkIcon("school")
                .thuTuHienThi(3)
                .kichHoat(true)
                .build());

        danhMucRepository.saveAll(java.util.List.of(
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Toán THPT & Ôn thi THPT QG")
                        .duongDanSlug("toan-thpt")
                        .danhMucCha(cap3)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("functions")
                        .thuTuHienThi(1)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Vật Lý 10-12")
                        .duongDanSlug("vat-ly-10-12")
                        .danhMucCha(cap3)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("bolt")
                        .thuTuHienThi(2)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Hóa Học 10-12")
                        .duongDanSlug("hoa-hoc-10-12")
                        .danhMucCha(cap3)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("experiment")
                        .thuTuHienThi(3)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Sinh Học 10-12")
                        .duongDanSlug("sinh-hoc-10-12")
                        .danhMucCha(cap3)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("biotech")
                        .thuTuHienThi(4)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Ngữ Văn THPT")
                        .duongDanSlug("ngu-van-thpt")
                        .danhMucCha(cap3)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("history_edu")
                        .thuTuHienThi(5)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Tiếng Anh THPT")
                        .duongDanSlug("tieng-anh-thpt")
                        .danhMucCha(cap3)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("language")
                        .thuTuHienThi(6)
                        .kichHoat(true)
                        .build()
        ));

        // Chứng chỉ quốc tế
        com.studywithme.entity.DanhMuc chungChi = danhMucRepository.save(com.studywithme.entity.DanhMuc.builder()
                .tenDanhMuc("Chứng chỉ Quốc Tế")
                .duongDanSlug("chung-chi-quoc-te")
                .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.CHUNG_CHI_NGOAI_NGU)
                .linkIcon("workspace_premium")
                .thuTuHienThi(4)
                .kichHoat(true)
                .build());

        danhMucRepository.saveAll(java.util.List.of(
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Luyện thi IELTS Academic")
                        .duongDanSlug("ielts-academic")
                        .danhMucCha(chungChi)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.CHUNG_CHI_NGOAI_NGU)
                        .linkIcon("verified")
                        .thuTuHienThi(1)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Luyện thi IELTS General")
                        .duongDanSlug("ielts-general")
                        .danhMucCha(chungChi)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.CHUNG_CHI_NGOAI_NGU)
                        .linkIcon("verified_user")
                        .thuTuHienThi(2)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Luyện thi TOEIC 4 Kỹ năng")
                        .duongDanSlug("toeic-4-ky-nang")
                        .danhMucCha(chungChi)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.CHUNG_CHI_NGOAI_NGU)
                        .linkIcon("record_voice_over")
                        .thuTuHienThi(3)
                        .kichHoat(true)
                        .build(),
                com.studywithme.entity.DanhMuc.builder()
                        .tenDanhMuc("Tiếng Anh Giao tiếp & Phản xạ")
                        .duongDanSlug("tieng-anh-giao-tiep")
                        .danhMucCha(chungChi)
                        .loaiDanhMuc(com.studywithme.entity.enums.shared.LoaiDanhMuc.MON_HOC)
                        .linkIcon("forum")
                        .thuTuHienThi(4)
                        .kichHoat(true)
                        .build()
        ));

        logger.info(">>> Đã khởi tạo cấu trúc cây danh mục môn học & cấp học chuẩn thành công.");
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

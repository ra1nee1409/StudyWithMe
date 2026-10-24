package com.studywithme.config;

import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.giaovien.TrinhDoKhoaHoc;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final VaiTroRepository vaiTroRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final BannerQuangCaoRepository bannerQuangCaoRepository;
    private final DonHangRepository donHangRepository;
    private final DanhMucRepository danhMucRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final ChuongHocRepository chuongHocRepository;
    private final BaiHocRepository baiHocRepository;
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
        initGiangVienMau(roleGiangVien);

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

        // 8. Khởi tạo 8 khóa học mẫu nổi bật nếu chưa có khóa học nào
        if (khoaHocRepository.count() == 0) {
            initKhoaHocMau();
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

    private void initGiangVienMau(VaiTro roleGiangVien) {
        // 1. Thầy Hoàng Văn Nam (Toán)
        taoGiangVienNeuChuaCo("tutor@studywithme.vn", "Tutor@123456", "Thầy Hoàng Văn Nam", "0912345678",
                "https://images.unsplash.com/photo-1568602471122-7832951cc4c5?w=400&auto=format&fit=crop&q=80",
                "Thạc sĩ Sư Phạm Toán - 7 năm KN Luyện Thi",
                "Tốt nghiệp thủ khoa ĐH Sư Phạm Hà Nội. Phương pháp giảng dạy trực quan, tư duy Casio và sơ đồ Mindmap.",
                roleGiangVien);

        // 2. Cô Mai Anh (Tiếng Anh / IELTS)
        taoGiangVienNeuChuaCo("maianh@studywithme.vn", "Tutor@123456", "Cô Mai Anh", "0987112233",
                "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80",
                "Thạc sĩ Ngôn ngữ - IELTS Overall 8.5",
                "Giảng viên Hội đồng Anh với 6 năm kinh nghiệm, chuyên luyện Speaking & Writing band 7.0+ theo phương pháp phản xạ tự nhiên.",
                roleGiangVien);

        // 3. Thầy Vũ Minh Quân (Vật lý / KHTN)
        taoGiangVienNeuChuaCo("minhquan@studywithme.vn", "Tutor@123456", "Thầy Vũ Minh Quân", "0977223344",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                "Thủ khoa Bách Khoa - 5 năm Luyện thi KHTN & Vật Lý",
                "Truyền cảm hứng học tập bằng thí nghiệm thực tế, nắm chắc bản chất vật lý 10-11-12 và mẹo giải trắc nghiệm 30s.",
                roleGiangVien);

        // 4. Cô Nguyễn Thu Hà (Tiểu học / Toán tư duy)
        taoGiangVienNeuChuaCo("thuha@studywithme.vn", "Tutor@123456", "Cô Nguyễn Thu Hà", "0966334455",
                "https://images.unsplash.com/photo-1580894732444-8ecded7900cd?w=400&auto=format&fit=crop&q=80",
                "Cử nhân Sư phạm Tiểu học - Chuyên gia Toán Tư Duy",
                "Đồng hành cùng học sinh lớp 1-5 phát triển tư duy logic, rèn nếp tự học sớm và tiếp cận Toán học qua hình ảnh sinh động.",
                roleGiangVien);
    }

    private HoSoGiangVien taoGiangVienNeuChuaCo(String email, String matKhau, String hoTen, String soDienThoai,
                                               String anhDaiDien, String tieuDe, String gioiThieu, VaiTro roleGiangVien) {
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElseGet(() -> {
            NguoiDung u = NguoiDung.builder()
                    .email(email)
                    .matKhau(passwordEncoder.encode(matKhau))
                    .hoTen(hoTen)
                    .soDienThoai(soDienThoai)
                    .anhDaiDien(anhDaiDien)
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(Set.of(roleGiangVien))
                    .build();
            return nguoiDungRepository.save(u);
        });

        return hoSoGiangVienRepository.findByNguoiDungId(user.getId()).orElseGet(() -> {
            HoSoGiangVien hoSo = HoSoGiangVien.builder()
                    .nguoiDung(user)
                    .tieuDeChuyenMon(tieuDe)
                    .gioiThieuBanThan(gioiThieu)
                    .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                    .soDuVi(BigDecimal.ZERO)
                    .build();
            return hoSoGiangVienRepository.save(hoSo);
        });
    }

    private void initKhoaHocMau() {
        HoSoGiangVien gvNam = hoSoGiangVienRepository.findByNguoiDungId(nguoiDungRepository.findByEmail("tutor@studywithme.vn").map(NguoiDung::getId).orElse(null)).orElse(null);
        HoSoGiangVien gvMaiAnh = hoSoGiangVienRepository.findByNguoiDungId(nguoiDungRepository.findByEmail("maianh@studywithme.vn").map(NguoiDung::getId).orElse(null)).orElse(gvNam);
        HoSoGiangVien gvQuan = hoSoGiangVienRepository.findByNguoiDungId(nguoiDungRepository.findByEmail("minhquan@studywithme.vn").map(NguoiDung::getId).orElse(null)).orElse(gvNam);
        HoSoGiangVien gvHa = hoSoGiangVienRepository.findByNguoiDungId(nguoiDungRepository.findByEmail("thuha@studywithme.vn").map(NguoiDung::getId).orElse(null)).orElse(gvNam);

        if (gvNam == null) return;

        DanhMuc dmToanThpt = danhMucRepository.findByDuongDanSlug("toan-thpt").orElse(null);
        DanhMuc dmIelts = danhMucRepository.findByDuongDanSlug("ielts-academic").orElse(null);
        DanhMuc dmVatLy = danhMucRepository.findByDuongDanSlug("vat-ly-10-12").orElse(null);
        DanhMuc dmVao10 = danhMucRepository.findByDuongDanSlug("luyen-thi-vao-10-chuyen").orElse(null);
        DanhMuc dmToeic = danhMucRepository.findByDuongDanSlug("toeic-4-ky-nang").orElse(null);
        DanhMuc dmToanTieuHoc = danhMucRepository.findByDuongDanSlug("toan-tieu-hoc").orElse(null);
        DanhMuc dmKhtn = danhMucRepository.findByDuongDanSlug("khoa-hoc-tu-nhien").orElse(null);
        DanhMuc dmGiaoTiep = danhMucRepository.findByDuongDanSlug("tieng-anh-giao-tiep").orElse(null);

        // Khóa 1: Toán 12
        taoKhoaHoc("Chinh Phục Điểm 9+ Môn Toán THPT Quốc Gia",
                "chinh-phuc-diem-9-mon-toan-thpt",
                "Khoá học toàn diện bám sát cấu trúc đề thi tốt nghiệp THPT Quốc Gia. Tổng hợp đầy đủ lý thuyết, phân dạng bài tập từ 7+ lên 9+, kỹ năng bấm máy Casio siêu nhanh và các chuyên đề Hình học không gian, Hàm số, Tích phân.",
                "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("1500000"), new BigDecimal("899000"), "CAP_3", 1240, new BigDecimal("4.95"), 158,
                gvNam, dmToanThpt);

        // Khóa 2: IELTS 7.0+
        taoKhoaHoc("Bứt Phá IELTS Academic 7.0+ 4 Kỹ Năng Toàn Diện",
                "but-pha-ielts-academic-7-0-toan-dien",
                "Lộ trình tinh gọn dành cho học viên mục tiêu band 7.0+ IELTS. Nâng cấp từ vựng C1-C2, chiến thuật Writing Task 1 & 2 logic, phản xạ Speaking tự nhiên với giảng viên 8.5 Overall.",
                "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("2400000"), new BigDecimal("1650000"), "IELTS", 980, new BigDecimal("5.00"), 192,
                gvMaiAnh, dmIelts);

        // Khóa 3: Vật Lý 12
        taoKhoaHoc("Lấy Gốc & Nâng Cao Vật Lý 12 - Ôn Thi Cực Nhanh",
                "lay-goc-nang-cao-vat-ly-12",
                "Phương pháp tư duy trực quan đồ thị và thí nghiệm ảo. Giải quyết triệt để các câu hỏi phân loại Dao động cơ, Sóng cơ và Dòng điện xoay chiều một cách dễ hiểu nhất.",
                "https://images.unsplash.com/photo-1532094349884-543bc11b234d?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("1200000"), new BigDecimal("699000"), "CAP_3", 820, new BigDecimal("4.88"), 95,
                gvQuan, dmVatLy);

        // Khóa 4: Toán vào 10
        taoKhoaHoc("Chiến Thuật Luyện Thi Vào 10 Môn Toán Điểm Cao",
                "chien-thuat-luyen-thi-vao-10-mon-toan",
                "Bí quyết làm chủ đề thi tuyển sinh lớp 10 các tỉnh thành và trường chuyên. Rèn luyện kỹ năng trình bày chặt chẽ, tối ưu hóa điểm số phần Hình học và Bất đẳng thức.",
                "https://images.unsplash.com/photo-1596495578065-6e0763fa1178?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("1100000"), new BigDecimal("599000"), "CAP_2", 750, new BigDecimal("4.92"), 84,
                gvNam, dmVao10);

        // Khóa 5: TOEIC 750+
        taoKhoaHoc("Luyện Thi TOEIC Cấp Tốc 750+ Dành Cho Sinh Viên",
                "luyen-thi-toeic-cap-toc-750",
                "Bộ mẹo tránh bẫy đề thi TOEIC mới nhất Format ETS. Nâng cao tốc độ Part 5-6-7, rèn tai nghe bắt từ khóa Part 3-4 để bứt phá mục tiêu chuẩn đầu ra đại học trong 2 tháng.",
                "https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("1300000"), new BigDecimal("750000"), "TOEIC", 690, new BigDecimal("4.85"), 73,
                gvMaiAnh, dmToeic);

        // Khóa 6: Toán tư duy 4-5
        taoKhoaHoc("Toán Tư Duy & Giải Quyết Vấn Đề Lớp 4 - Lớp 5",
                "toan-tu-duy-giai-quyet-van-de-lop-4-5",
                "Giúp học sinh tiểu học khơi gợi niềm đam mê Toán học qua sơ đồ tư duy, các câu đố logic Singapore, chuẩn bị vững chắc kiến thức chuyển cấp lên THCS và thi CLC.",
                "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("950000"), new BigDecimal("499000"), "CAP_1", 560, new BigDecimal("4.96"), 67,
                gvHa, dmToanTieuHoc);

        // Khóa 7: KHTN lớp 9
        taoKhoaHoc("Khoa Học Tự Nhiên Lớp 9 - Trọn Bộ Lý, Hóa, Sinh",
                "khoa-hoc-tu-nhien-lop-9-tron-bo",
                "Bám sát bộ sách giáo khoa mới (Kết nối tri thức, Cánh diều). Tóm lược lý thuyết cốt lõi kèm chuỗi video thí nghiệm sống động giúp học sinh hiểu sâu nhớ lâu.",
                "https://images.unsplash.com/photo-1427504494785-3a9ca7044f45?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("1000000"), new BigDecimal("550000"), "CAP_2", 480, new BigDecimal("4.89"), 52,
                gvQuan, dmKhtn);

        // Khóa 8: Tiếng Anh giao tiếp
        taoKhoaHoc("Tiếng Anh Giao Tiếp Thực Chiến Cho Người Mất Gốc",
                "tieng-anh-giao-tiep-thuc-chien-cho-nguoi-mat-goc",
                "Chuẩn hóa phát âm IPA từ con số 0. Tự tin giao tiếp các chủ đề đời sống và công sở chỉ sau 60 ngày với các tình huống đóng vai thực tế.",
                "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("1200000"), new BigDecimal("650000"), "IELTS", 430, new BigDecimal("4.91"), 46,
                gvMaiAnh, dmGiaoTiep);

        logger.info(">>> Đã khởi tạo 8 khóa học mẫu nổi bật thành công.");
    }

    private void taoKhoaHoc(String tieuDe, String slug, String moTa, String anhDaiDien,
                            BigDecimal giaGoc, BigDecimal giaKhuyenMai, String thePhanLoai,
                            int soHocVien, BigDecimal diemTb, int soDanhGia,
                            HoSoGiangVien giangVien, DanhMuc danhMuc) {
        if (danhMuc == null || giangVien == null) return;
        if (khoaHocRepository.existsByDuongDanSlug(slug)) return;

        KhoaHoc kh = KhoaHoc.builder()
                .tieuDe(tieuDe)
                .duongDanSlug(slug)
                .moTaChiTiet(moTa)
                .anhDaiDienKhoaHoc(anhDaiDien)
                .giaGoc(giaGoc)
                .giaKhuyenMai(giaKhuyenMai)
                .thePhanLoai(thePhanLoai)
                .trinhDo(TrinhDoKhoaHoc.MOI_TRINH_DO)
                .trangThai(TrangThaiKhoaHoc.DA_XUAT_BAN)
                .tongSoHocVien(soHocVien)
                .diemDanhGiaTrungBinh(diemTb)
                .tongSoDanhGia(soDanhGia)
                .giangVien(giangVien)
                .danhMuc(danhMuc)
                .build();
        KhoaHoc savedKh = khoaHocRepository.save(kh);

        // Tạo 1 chương và 2 bài học (có bài học thử)
        ChuongHoc c1 = chuongHocRepository.save(ChuongHoc.builder()
                .khoaHoc(savedKh)
                .tieuDeChuong("Chương 1: Giới thiệu & Lộ trình học tập")
                .thuTuSapXep(1)
                .build());

        baiHocRepository.save(BaiHoc.builder()
                .chuongHoc(c1)
                .tieuDeBaiHoc("Bài 1: Giới thiệu khóa học & Phương pháp học hiệu quả")
                .linkVideo("https://www.youtube.com/embed/dQw4w9WgXcQ")
                .thoiLuongGiay(600)
                .choPhepHocThu(true)
                .thuTuSapXep(1)
                .build());

        baiHocRepository.save(BaiHoc.builder()
                .chuongHoc(c1)
                .tieuDeBaiHoc("Bài 2: Đánh giá năng lực đầu vào & Kế hoạch ôn tập")
                .linkVideo("https://www.youtube.com/embed/dQw4w9WgXcQ")
                .thoiLuongGiay(900)
                .choPhepHocThu(true)
                .thuTuSapXep(2)
                .build());
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

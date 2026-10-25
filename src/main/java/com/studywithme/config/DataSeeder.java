package com.studywithme.config;

import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiXacThuc;
import com.studywithme.entity.enums.giaovien.LoaiChungChi;
import com.studywithme.entity.enums.giaovien.TrinhDoKhoaHoc;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final VaiTroRepository vaiTroRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final ChungChiGiangVienRepository chungChiGiangVienRepository;
    private final BannerQuangCaoRepository bannerQuangCaoRepository;
    private final DonHangRepository donHangRepository;
    private final DanhMucRepository danhMucRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final ChuongHocRepository chuongHocRepository;
    private final BaiHocRepository baiHocRepository;
    private final LopHocTrucTuyenRepository lopHocTrucTuyenRepository;
    private final YeuCauRutTienRepository yeuCauRutTienRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(VaiTroRepository vaiTroRepository,
                      NguoiDungRepository nguoiDungRepository,
                      HoSoGiangVienRepository hoSoGiangVienRepository,
                      ChungChiGiangVienRepository chungChiGiangVienRepository,
                      BannerQuangCaoRepository bannerQuangCaoRepository,
                      DonHangRepository donHangRepository,
                      DanhMucRepository danhMucRepository,
                      KhoaHocRepository khoaHocRepository,
                      ChuongHocRepository chuongHocRepository,
                      BaiHocRepository baiHocRepository,
                      LopHocTrucTuyenRepository lopHocTrucTuyenRepository,
                      YeuCauRutTienRepository yeuCauRutTienRepository,
                      PasswordEncoder passwordEncoder) {
        this.vaiTroRepository = vaiTroRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.chungChiGiangVienRepository = chungChiGiangVienRepository;
        this.bannerQuangCaoRepository = bannerQuangCaoRepository;
        this.donHangRepository = donHangRepository;
        this.danhMucRepository = danhMucRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.chuongHocRepository = chuongHocRepository;
        this.baiHocRepository = baiHocRepository;
        this.lopHocTrucTuyenRepository = lopHocTrucTuyenRepository;
        this.yeuCauRutTienRepository = yeuCauRutTienRepository;
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

        // 9. Khởi tạo các lớp học gia sư trực tuyến đang mở nếu chưa có lớp nào
        if (lopHocTrucTuyenRepository.count() == 0) {
            initLopHocMau();
        }

        // 10. Khởi tạo các lệnh yêu cầu rút tiền mẫu nếu chưa có
        if (yeuCauRutTienRepository.count() == 0) {
            initYeuCauRutTienMau();
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
        VaiTro roleHocVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN).orElse(null);

        // 1. Thầy Hoàng Văn Nam (Toán) - ĐÃ DUYỆT
        HoSoGiangVien hsNam = taoGiangVienNeuChuaCo("tutor@studywithme.vn", "Tutor@123456", "Thầy Hoàng Văn Nam", "0912345678",
                "https://images.unsplash.com/photo-1568602471122-7832951cc4c5?w=400&auto=format&fit=crop&q=80",
                "Thạc sĩ Sư Phạm Toán - 7 năm KN Luyện Thi",
                "Tốt nghiệp thủ khoa ĐH Sư Phạm Hà Nội. Phương pháp giảng dạy trực quan, tư duy Casio và sơ đồ Mindmap.",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                TrangThaiGiangVien.DA_DUYET, null, roleGiangVien);
        taoChungChiNeuChuaCo(hsNam, LoaiChungChi.BANG_DAI_HOC, "Bằng Cử Nhân Sư Phạm Toán Học", "Thủ khoa Xuất sắc", "ĐH Sư Phạm Hà Nội", LocalDate.of(2018, 6, 25), "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=800", TrangThaiXacThuc.DA_XAC_THUC);

        // 2. Cô Mai Anh (Tiếng Anh / IELTS) - ĐÃ DUYỆT
        HoSoGiangVien hsMaiAnh = taoGiangVienNeuChuaCo("maianh@studywithme.vn", "Tutor@123456", "Cô Mai Anh", "0987112233",
                "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80",
                "Thạc sĩ Ngôn ngữ - IELTS Overall 8.5",
                "Giảng viên Hội đồng Anh với 6 năm kinh nghiệm, chuyên luyện Speaking & Writing band 7.0+ theo phương pháp phản xạ tự nhiên.",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                TrangThaiGiangVien.DA_DUYET, null, roleGiangVien);
        taoChungChiNeuChuaCo(hsMaiAnh, LoaiChungChi.IELTS, "IELTS Academic Test Report Form", "Band 8.5 Overall (L9.0, R9.0, W8.0, S8.0)", "British Council", LocalDate.of(2023, 4, 15), "https://images.unsplash.com/photo-1589330694653-dad6ef0140be?w=800", TrangThaiXacThuc.DA_XAC_THUC);

        // 3. Thầy Vũ Minh Quân (Vật lý / KHTN) - ĐÃ DUYỆT
        taoGiangVienNeuChuaCo("minhquan@studywithme.vn", "Tutor@123456", "Thầy Vũ Minh Quân", "0977223344",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                "Thủ khoa Bách Khoa - 5 năm Luyện thi KHTN & Vật Lý",
                "Truyền cảm hứng học tập bằng thí nghiệm thực tế, nắm chắc bản chất vật lý 10-11-12 và mẹo giải trắc nghiệm 30s.",
                null, TrangThaiGiangVien.DA_DUYET, null, roleGiangVien);

        // 4. Cô Nguyễn Thu Hà (Tiểu học / Toán tư duy) - ĐÃ DUYỆT
        taoGiangVienNeuChuaCo("thuha@studywithme.vn", "Tutor@123456", "Cô Nguyễn Thu Hà", "0966334455",
                "https://images.unsplash.com/photo-1580894732444-8ecded7900cd?w=400&auto=format&fit=crop&q=80",
                "Cử nhân Sư phạm Tiểu học - Chuyên gia Toán Tư Duy",
                "Đồng hành cùng học sinh lớp 1-5 phát triển tư duy logic, rèn nếp tự học sớm và tiếp cận Toán học qua hình ảnh sinh động.",
                null, TrangThaiGiangVien.DA_DUYET, null, roleGiangVien);

        // 5. Ứng viên 1: Thầy Lê Quốc Bảo (Hóa Học) - ĐANG CHỜ DUYỆT (CHO_DUYET)
        HoSoGiangVien hsBao = taoGiangVienNeuChuaCo("lebao.chem@studywithme.vn", "Tutor@123456", "Thầy Lê Quốc Bảo", "0934567890",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                "Thạc Sĩ Hóa Học - 4 Năm Luyện Thi THPT & Chuyên Hóa",
                "Tốt nghiệp Thạc sĩ ĐH Khoa học Tự nhiên. Có kinh nghiệm 4 năm bồi dưỡng học sinh giỏi Hóa cấp tỉnh và luyện thi THPT 9+. Mong muốn kết nối và mở các lớp kèm chuyên sâu.",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                TrangThaiGiangVien.CHO_DUYET, null, roleHocVien != null ? roleHocVien : roleGiangVien);
        taoChungChiNeuChuaCo(hsBao, LoaiChungChi.BANG_DAI_HOC, "Bằng Cử Nhân Hóa Học Chính Quy", "Loại Giỏi", "ĐH Khoa Học Tự Nhiên - ĐHQG Hà Nội", LocalDate.of(2020, 7, 10), "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=800", TrangThaiXacThuc.CHO_XAC_THUC);
        taoChungChiNeuChuaCo(hsBao, LoaiChungChi.CHUNG_CHI_SU_PHAM, "Chứng Chỉ Nghiệp Vụ Sư Phạm Giảng Dạy", "Xuất sắc", "ĐH Sư Phạm Hà Nội", LocalDate.of(2021, 3, 20), "https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=800", TrangThaiXacThuc.CHO_XAC_THUC);

        // 6. Ứng viên 2: Cô Trần Thị Thu Thảo (IELTS) - ĐANG CHỜ DUYỆT (CHO_DUYET)
        HoSoGiangVien hsThao = taoGiangVienNeuChuaCo("thuthao.ielts@studywithme.vn", "Tutor@123456", "Cô Trần Thị Thu Thảo", "0945678901",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                "Cử Nhân Sư Phạm Anh - IELTS Overall 8.0",
                "Chuyên luyện thi IELTS cấp tốc cho học sinh THPT chuẩn bị xét tuyển đại học. Tận tâm, lộ trình cá nhân hóa và phương pháp mở rộng từ vựng Collocation sinh động.",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                TrangThaiGiangVien.CHO_DUYET, null, roleHocVien != null ? roleHocVien : roleGiangVien);
        taoChungChiNeuChuaCo(hsThao, LoaiChungChi.IELTS, "IELTS Academic Test Report Form", "Band 8.0 (L8.5, R8.5, W7.5, S7.5)", "IDP Education", LocalDate.of(2024, 2, 28), "https://images.unsplash.com/photo-1589330694653-dad6ef0140be?w=800", TrangThaiXacThuc.CHO_XAC_THUC);

        // 7. Ứng viên 3: Đặng Đức Việt - BỊ TỪ CHỐI (TU_CHOI)
        taoGiangVienNeuChuaCo("ducviet.math@studywithme.vn", "Tutor@123456", "Đặng Đức Việt", "0911223344",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                "Sinh viên năm 3 ĐH Bách Khoa nhận dạy kèm Toán",
                "Nhận dạy kèm Toán cấp 2 và cấp 3 online.",
                null,
                TrangThaiGiangVien.TU_CHOI,
                "Hồ sơ chưa cung cấp đầy đủ bảng điểm/chứng chỉ sư phạm và chưa có video dạy thử demo theo tiêu chuẩn kiểm duyệt của StudyWithMe.",
                roleHocVien != null ? roleHocVien : roleGiangVien);
        
        logger.info(">>> Đã khởi tạo danh sách giảng viên mẫu (4 đã duyệt, 2 chờ duyệt, 1 từ chối) thành công.");
    }

    private HoSoGiangVien taoGiangVienNeuChuaCo(String email, String matKhau, String hoTen, String soDienThoai,
                                               String anhDaiDien, String tieuDe, String gioiThieu,
                                               String linkVideo, TrangThaiGiangVien trangThaiDuyet,
                                               String lyDoTuChoi, VaiTro role) {
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElseGet(() -> {
            NguoiDung u = NguoiDung.builder()
                    .email(email)
                    .matKhau(passwordEncoder.encode(matKhau))
                    .hoTen(hoTen)
                    .soDienThoai(soDienThoai)
                    .anhDaiDien(anhDaiDien)
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(role != null ? Set.of(role) : Set.of())
                    .build();
            return nguoiDungRepository.save(u);
        });

        return hoSoGiangVienRepository.findByNguoiDungId(user.getId()).orElseGet(() -> {
            HoSoGiangVien hoSo = HoSoGiangVien.builder()
                    .nguoiDung(user)
                    .tieuDeChuyenMon(tieuDe)
                    .gioiThieuBanThan(gioiThieu)
                    .linkVideoDayThu(linkVideo)
                    .trangThaiDuyet(trangThaiDuyet)
                    .lyDoTuChoi(lyDoTuChoi)
                    .soDuVi(BigDecimal.ZERO)
                    .tenNganHang("Techcombank")
                    .soTaiKhoanNganHang("19036788990011")
                    .tenChuTaiKhoan(hoTen.toUpperCase())
                    .build();
            return hoSoGiangVienRepository.save(hoSo);
        });
    }

    private void taoChungChiNeuChuaCo(HoSoGiangVien giangVien, LoaiChungChi loai, String ten, String diem,
                                      String noiCap, LocalDate ngayCap, String linkAnh, TrangThaiXacThuc trangThai) {
        if (giangVien == null || giangVien.getId() == null) return;
        List<ChungChiGiangVien> list = chungChiGiangVienRepository.findByGiangVienId(giangVien.getId());
        boolean exists = list.stream().anyMatch(c -> c.getTenChungChi().equalsIgnoreCase(ten));
        if (!exists) {
            ChungChiGiangVien cc = ChungChiGiangVien.builder()
                    .giangVien(giangVien)
                    .loaiChungChi(loai)
                    .tenChungChi(ten)
                    .diemSoHoacXepLoai(diem)
                    .noiCap(noiCap)
                    .ngayCap(ngayCap)
                    .linkAnhChungChi(linkAnh)
                    .trangThaiXacThuc(trangThai)
                    .build();
            chungChiGiangVienRepository.save(cc);
        }
    }

    private void initKhoaHocMau() {
        HoSoGiangVien gvNam = hoSoGiangVienRepository.findByNguoiDungId(nguoiDungRepository.findByEmail("tutor@studywithme.vn").map(NguoiDung::getId).orElse(null)).orElse(null);
        HoSoGiangVien gvMaiAnh = hoSoGiangVienRepository.findByNguoiDungId(nguoiDungRepository.findByEmail("maianh@studywithme.vn").map(NguoiDung::getId).orElse(null)).orElse(gvNam);
        HoSoGiangVien gvQuan = hoSoGiangVienRepository.findByNguoiDungId(nguoiDungRepository.findByEmail("minhquan@studywithme.vn").map(NguoiDung::getId).orElse(null)).orElse(gvNam);
        HoSoGiangVien gvHa = hoSoGiangVienRepository.findByNguoiDungId(nguoiDungRepository.findByEmail("thuha@studywithme.vn").map(NguoiDung::getId).orElse(null)).orElse(gvNam);

        if (gvNam == null) return;

        List<DanhMuc> allCategories = danhMucRepository.findAll();
        if (allCategories.isEmpty()) return;

        DanhMuc defaultDm = allCategories.get(0);
        DanhMuc dmToanThpt = danhMucRepository.findByDuongDanSlug("toan-thpt")
                .orElseGet(() -> allCategories.stream().filter(d -> d.getDuongDanSlug().contains("cap-3") || d.getDuongDanSlug().contains("thpt")).findFirst().orElse(defaultDm));
        DanhMuc dmIelts = danhMucRepository.findByDuongDanSlug("ielts-academic")
                .orElseGet(() -> allCategories.stream().filter(d -> d.getDuongDanSlug().contains("ielts")).findFirst().orElse(defaultDm));
        DanhMuc dmVatLy = danhMucRepository.findByDuongDanSlug("vat-ly-10-12")
                .orElseGet(() -> allCategories.stream().filter(d -> d.getDuongDanSlug().contains("cap-3") || d.getDuongDanSlug().contains("ly")).findFirst().orElse(defaultDm));
        DanhMuc dmVao10 = danhMucRepository.findByDuongDanSlug("luyen-thi-vao-10-chuyen")
                .orElseGet(() -> allCategories.stream().filter(d -> d.getDuongDanSlug().contains("cap-2")).findFirst().orElse(defaultDm));
        DanhMuc dmToeic = danhMucRepository.findByDuongDanSlug("toeic-4-ky-nang")
                .orElseGet(() -> allCategories.stream().filter(d -> d.getDuongDanSlug().contains("toeic")).findFirst().orElse(defaultDm));
        DanhMuc dmToanTieuHoc = danhMucRepository.findByDuongDanSlug("toan-tieu-hoc")
                .orElseGet(() -> allCategories.stream().filter(d -> d.getDuongDanSlug().contains("cap-1")).findFirst().orElse(defaultDm));
        DanhMuc dmKhtn = danhMucRepository.findByDuongDanSlug("khoa-hoc-tu-nhien")
                .orElseGet(() -> allCategories.stream().filter(d -> d.getDuongDanSlug().contains("cap-2")).findFirst().orElse(defaultDm));
        DanhMuc dmGiaoTiep = danhMucRepository.findByDuongDanSlug("tieng-anh-giao-tiep")
                .orElseGet(() -> allCategories.stream().filter(d -> d.getDuongDanSlug().contains("ielts") || d.getDuongDanSlug().contains("toeic")).findFirst().orElse(defaultDm));

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

        // Khóa 9: Đang chờ duyệt (CHO_DUYET)
        taoKhoaHoc("Khóa Học Luyện Đề ĐGNL ĐHQG 2026 - Tư Duy Định Lượng",
                "luyen-de-dgnl-dhqg-2026-tu-duy-dinh-luong",
                "Tổng hợp 20 đề thi chuẩn ma trận đề thi ĐGNL ĐHQG Hà Nội & TP.HCM. Phân tích chi tiết từng dạng câu hỏi phân loại điểm 900+ kèm video hướng dẫn phương pháp giải.",
                "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("1400000"), new BigDecimal("790000"), "CAP_3", 0, BigDecimal.ZERO, 0,
                gvNam, dmToanThpt, TrangThaiKhoaHoc.CHO_DUYET, null);

        // Khóa 10: Đang chờ duyệt (CHO_DUYET)
        taoKhoaHoc("Mastering IELTS Writing Task 2: Cấu Trúc Luận Điểm 8.0+",
                "mastering-ielts-writing-task-2-8-0",
                "Chiến lược tư duy phản biện và phát triển ý tưởng cho 5 dạng đề Writing Task 2. Cung cấp 50 bài luận mẫu Band 8.5 kèm hệ thống từ vựng C1-C2 chuyên sâu.",
                "https://images.unsplash.com/photo-1455390582262-044cdead277a?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("1800000"), new BigDecimal("990000"), "IELTS", 0, BigDecimal.ZERO, 0,
                gvMaiAnh, dmIelts, TrangThaiKhoaHoc.CHO_DUYET, null);

        // Khóa 11: Bị từ chối (TU_CHOI)
        taoKhoaHoc("Tuyệt Kỹ Bấm Máy Tính Casio 580VNX Giải Nhanh Toán 12",
                "tuyet-ky-bam-may-tinh-casio-580vnx",
                "Kỹ thuật giải nhanh trắc nghiệm 30s các dạng Toán giải tích, đạo hàm và số phức dành cho học sinh lớp 12 chuẩn bị thi tốt nghiệp.",
                "https://images.unsplash.com/photo-1594980596870-8aa52a78d8cd?w=800&auto=format&fit=crop&q=80",
                new BigDecimal("800000"), new BigDecimal("390000"), "CAP_3", 0, BigDecimal.ZERO, 0,
                gvNam, dmToanThpt, TrangThaiKhoaHoc.TU_CHOI,
                "Video bài giảng chất lượng 360p mờ, âm thanh micro có tiếng ồn và đề cương chưa đầy đủ các chương trọng tâm.");

        logger.info(">>> Đã khởi tạo các khóa học mẫu (8 đã xuất bản, 2 chờ duyệt, 1 từ chối) thành công.");
    }

    private void taoKhoaHoc(String tieuDe, String slug, String moTa, String anhDaiDien,
                            BigDecimal giaGoc, BigDecimal giaKhuyenMai, String thePhanLoai,
                            int soHocVien, BigDecimal diemTb, int soDanhGia,
                            HoSoGiangVien giangVien, DanhMuc danhMuc) {
        taoKhoaHoc(tieuDe, slug, moTa, anhDaiDien, giaGoc, giaKhuyenMai, thePhanLoai, soHocVien, diemTb, soDanhGia, giangVien, danhMuc, TrangThaiKhoaHoc.DA_XUAT_BAN, null);
    }

    private void taoKhoaHoc(String tieuDe, String slug, String moTa, String anhDaiDien,
                            BigDecimal giaGoc, BigDecimal giaKhuyenMai, String thePhanLoai,
                            int soHocVien, BigDecimal diemTb, int soDanhGia,
                            HoSoGiangVien giangVien, DanhMuc danhMuc,
                            TrangThaiKhoaHoc trangThai, String lyDoTuChoi) {
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
                .trangThai(trangThai)
                .lyDoTuChoi(lyDoTuChoi)
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

    private void initLopHocMau() {
        NguoiDung uNam = nguoiDungRepository.findByEmail("tutor@studywithme.vn").orElse(null);
        NguoiDung uMaiAnh = nguoiDungRepository.findByEmail("maianh@studywithme.vn").orElse(null);
        NguoiDung uMinhQuan = nguoiDungRepository.findByEmail("minhquan@studywithme.vn").orElse(null);

        HoSoGiangVien gvNam = (uNam != null) ? hoSoGiangVienRepository.findByNguoiDungId(uNam.getId()).orElse(null) : null;
        HoSoGiangVien gvMaiAnh = (uMaiAnh != null) ? hoSoGiangVienRepository.findByNguoiDungId(uMaiAnh.getId()).orElse(null) : null;
        HoSoGiangVien gvMinhQuan = (uMinhQuan != null) ? hoSoGiangVienRepository.findByNguoiDungId(uMinhQuan.getId()).orElse(null) : null;

        List<DanhMuc> allCats = danhMucRepository.findAll();
        DanhMuc defaultDm = !allCats.isEmpty() ? allCats.get(0) : null;
        if (defaultDm == null) return;

        DanhMuc dmToanThpt = danhMucRepository.findByDuongDanSlug("toan-thpt")
                .orElseGet(() -> allCats.stream().filter(d -> d.getDuongDanSlug().contains("cap-3") || d.getDuongDanSlug().contains("thpt")).findFirst().orElse(defaultDm));
        DanhMuc dmIelts = danhMucRepository.findByDuongDanSlug("ielts-academic")
                .orElseGet(() -> allCats.stream().filter(d -> d.getDuongDanSlug().contains("ielts")).findFirst().orElse(defaultDm));
        DanhMuc dmVatLy = danhMucRepository.findByDuongDanSlug("vat-ly-10-12")
                .orElseGet(() -> allCats.stream().filter(d -> d.getDuongDanSlug().contains("cap-3") || d.getDuongDanSlug().contains("ly")).findFirst().orElse(defaultDm));

        if (gvNam != null && dmToanThpt != null) {
            lopHocTrucTuyenRepository.save(com.studywithme.entity.LopHocTrucTuyen.builder()
                    .giangVien(gvNam)
                    .danhMuc(dmToanThpt)
                    .tieuDeLop("Lớp Kèm Toán 12 Nâng Cao - Ôn Thi THPT Quốc Gia Điểm 9+")
                    .moTaLop("Học trực tuyến tương tác 2 chiều qua Google Meet. Luyện chuyên đề Hàm số, Tọa độ không gian Oxyz và Tích phân vận dụng cao.")
                    .siSoToiDa(15)
                    .hocPhiMoiHocVien(new BigDecimal("1500000"))
                    .lichHocTrongTuan("Thứ 2 & Thứ 5 (19:30 - 21:00)")
                    .linkPhongHoc("https://meet.google.com/studywithme-math")
                    .trangThai(com.studywithme.entity.enums.giaovien.TrangThaiLopHoc.DANG_MO_DANG_KY)
                    .build());

            lopHocTrucTuyenRepository.save(com.studywithme.entity.LopHocTrucTuyen.builder()
                    .giangVien(gvNam)
                    .danhMuc(dmToanThpt)
                    .tieuDeLop("Khóa Gia Sư 1-Kèm-1: Chinh Phục Hình Học Không Gian")
                    .moTaLop("Kèm riêng 1-1 hỗ trợ lấy lại gốc hình học không gian, dựng hình, khoảng cách và góc theo phương pháp tọa độ hóa.")
                    .siSoToiDa(1)
                    .hocPhiMoiHocVien(new BigDecimal("3500000"))
                    .lichHocTrongTuan("Thứ 7 & Chủ Nhật (08:30 - 10:00)")
                    .linkPhongHoc("https://meet.google.com/studywithme-math1on1")
                    .trangThai(com.studywithme.entity.enums.giaovien.TrangThaiLopHoc.DANG_MO_DANG_KY)
                    .build());
        }

        if (gvMaiAnh != null && dmIelts != null) {
            lopHocTrucTuyenRepository.save(com.studywithme.entity.LopHocTrucTuyen.builder()
                    .giangVien(gvMaiAnh)
                    .danhMuc(dmIelts)
                    .tieuDeLop("Lớp IELTS Speaking & Writing Intensive 7.0+ (Nhóm Nhỏ 8 Bạn)")
                    .moTaLop("Sửa bài 1-1 từng tuần, mock test speaking trực tiếp với giảng viên đạt IELTS 8.5. Cung cấp bộ Forecast đề thi mới nhất.")
                    .siSoToiDa(8)
                    .hocPhiMoiHocVien(new BigDecimal("2800000"))
                    .lichHocTrongTuan("Thứ 3 & Thứ 6 (20:00 - 21:30)")
                    .linkPhongHoc("https://meet.google.com/studywithme-ielts")
                    .trangThai(com.studywithme.entity.enums.giaovien.TrangThaiLopHoc.DANG_MO_DANG_KY)
                    .build());
        }

        if (gvMinhQuan != null && dmVatLy != null) {
            lopHocTrucTuyenRepository.save(com.studywithme.entity.LopHocTrucTuyen.builder()
                    .giangVien(gvMinhQuan)
                    .danhMuc(dmVatLy)
                    .tieuDeLop("Lớp Bứt Phá Vật Lý 12 - Chuyên Đề Dao Động Cơ & Điện Xoay Chiều")
                    .moTaLop("Phương pháp giải nhanh bằng vòng tròn lượng giác và giản đồ vecto buộc chuẩn. Bài tập trắc nghiệm có chấm điểm tự động.")
                    .siSoToiDa(12)
                    .hocPhiMoiHocVien(new BigDecimal("1400000"))
                    .lichHocTrongTuan("Thứ 4 & Thứ 7 (18:30 - 20:00)")
                    .linkPhongHoc("https://meet.google.com/studywithme-physics")
                    .trangThai(com.studywithme.entity.enums.giaovien.TrangThaiLopHoc.DANG_MO_DANG_KY)
                    .build());
        }

        logger.info(">>> Đã khởi tạo các lớp gia sư trực tuyến mẫu thành công.");
    }

    private void initYeuCauRutTienMau() {
        HoSoGiangVien gvNam = hoSoGiangVienRepository.findByNguoiDungEmail("tutor@studywithme.vn").orElse(null);
        HoSoGiangVien gvMaiAnh = hoSoGiangVienRepository.findByNguoiDungEmail("maianh@studywithme.vn").orElse(null);
        HoSoGiangVien gvBao = hoSoGiangVienRepository.findByNguoiDungEmail("lebao.chem@studywithme.vn").orElse(null);

        if (gvNam != null) {
            yeuCauRutTienRepository.save(com.studywithme.entity.YeuCauRutTien.builder()
                    .giangVien(gvNam)
                    .soTienRut(new BigDecimal("2000000"))
                    .tenNganHang("Vietcombank")
                    .soTaiKhoan("0123456789")
                    .tenChuTaiKhoan("HOANG VAN NAM")
                    .trangThai(com.studywithme.entity.enums.admin.TrangThaiRutTien.CHO_XU_LY)
                    .build());
        }

        if (gvMaiAnh != null) {
            yeuCauRutTienRepository.save(com.studywithme.entity.YeuCauRutTien.builder()
                    .giangVien(gvMaiAnh)
                    .soTienRut(new BigDecimal("1500000"))
                    .tenNganHang("MB Bank")
                    .soTaiKhoan("999988887777")
                    .tenChuTaiKhoan("MAI ANH")
                    .trangThai(com.studywithme.entity.enums.admin.TrangThaiRutTien.DA_CHUYEN_TIEN)
                    .ghiChuAdmin("Mã GD: FT26298374928 - Đã hoàn tất chuyển khoản thành công")
                    .ngayXuLy(java.time.LocalDateTime.now().minusHours(4))
                    .build());
        }

        if (gvBao != null) {
            yeuCauRutTienRepository.save(com.studywithme.entity.YeuCauRutTien.builder()
                    .giangVien(gvBao)
                    .soTienRut(new BigDecimal("5000000"))
                    .tenNganHang("Techcombank")
                    .soTaiKhoan("1903456781234")
                    .tenChuTaiKhoan("LE QUOC BAO")
                    .trangThai(com.studywithme.entity.enums.admin.TrangThaiRutTien.TU_CHOI)
                    .ghiChuAdmin("Tên chủ tài khoản Techcombank không khớp với hồ sơ KYC giảng viên")
                    .ngayXuLy(java.time.LocalDateTime.now().minusDays(1))
                    .build());
        }

        logger.info(">>> Đã khởi tạo các lệnh rút tiền mẫu (1 chờ xử lý, 1 đã chuyển, 1 từ chối) thành công.");
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

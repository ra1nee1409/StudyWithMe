package com.studywithme.service.impl;

import com.studywithme.dto.response.*;
import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.shared.LoaiDanhMuc;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import com.studywithme.repository.*;
import com.studywithme.service.AdminThongKeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminThongKeServiceImpl implements AdminThongKeService {

    private final DonHangRepository donHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final YeuCauRutTienRepository yeuCauRutTienRepository;
    private final GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;
    private final DanhMucRepository danhMucRepository;

    public AdminThongKeServiceImpl(DonHangRepository donHangRepository,
                                  NguoiDungRepository nguoiDungRepository,
                                  KhoaHocRepository khoaHocRepository,
                                  HoSoGiangVienRepository hoSoGiangVienRepository,
                                  YeuCauRutTienRepository yeuCauRutTienRepository,
                                  GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository,
                                  DanhMucRepository danhMucRepository) {
        this.donHangRepository = donHangRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.yeuCauRutTienRepository = yeuCauRutTienRepository;
        this.ghiDanhKhoaHocRepository = ghiDanhKhoaHocRepository;
        this.danhMucRepository = danhMucRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeTongQuanResponse layThongKeTongQuan() {
        // 1. KPI Doanh thu
        BigDecimal tongDoanhThu = donHangRepository.sumTongDoanhThu(TrangThaiThanhToan.DA_THANH_TOAN);
        if (tongDoanhThu == null) {
            tongDoanhThu = BigDecimal.ZERO;
        }

        // 2. KPI Người dùng
        long tongNguoiDung = nguoiDungRepository.count();

        // 3. KPI Khóa học
        long tongKhoaHocDangHoatDong = khoaHocRepository.countByTrangThai(TrangThaiKhoaHoc.DA_XUAT_BAN);

        // 4. KPI Chờ duyệt
        long hoSoChoDuyet = hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.CHO_DUYET);
        long lenhRutTienChoDuyet = yeuCauRutTienRepository.countByTrangThai(TrangThaiRutTien.CHO_XU_LY);
        long tongYeuCauChoDuyet = hoSoChoDuyet + lenhRutTienChoDuyet;

        // 5. Top 5 Khóa học bán chạy
        List<KhoaHoc> topKhoaHocEntities = khoaHocRepository.findTop5ByOrderByTongSoHocVienDesc();
        List<TopKhoaHocDto> topKhoaHocDtos = new ArrayList<>();
        for (KhoaHoc k : topKhoaHocEntities) {
            BigDecimal giaHienTai = k.getGiaKhuyenMai() != null ? k.getGiaKhuyenMai() : k.getGiaGoc();
            int soHocVien = k.getTongSoHocVien() != null ? k.getTongSoHocVien() : 0;
            BigDecimal doanhThuUocTinh = giaHienTai.multiply(BigDecimal.valueOf(soHocVien));

            String tenGV = (k.getGiangVien() != null && k.getGiangVien().getNguoiDung() != null)
                    ? k.getGiangVien().getNguoiDung().getHoTen() : "Giảng viên";

            topKhoaHocDtos.add(TopKhoaHocDto.builder()
                    .id(k.getId())
                    .tieuDe(k.getTieuDe())
                    .duongDanSlug(k.getDuongDanSlug())
                    .anhDaiDien(k.getAnhDaiDienKhoaHoc())
                    .tenGiangVien(tenGV)
                    .giaGoc(k.getGiaGoc())
                    .giaHienTai(giaHienTai)
                    .soHocVien(soHocVien)
                    .tongDoanhThuUocTinh(doanhThuUocTinh)
                    .build());
        }

        // 6. 5 Hồ sơ giảng viên mới nộp gần nhất
        List<HoSoGiangVien> giangVienMoiEntities = hoSoGiangVienRepository.findTop5ByOrderByNgayTaoDesc();
        List<GiangVienMoiDto> giangVienMoiDtos = new ArrayList<>();
        for (HoSoGiangVien gv : giangVienMoiEntities) {
            String hoTen = gv.getNguoiDung() != null ? gv.getNguoiDung().getHoTen() : "Chưa cập nhật";
            String email = gv.getNguoiDung() != null ? gv.getNguoiDung().getEmail() : "";
            String sdt = gv.getNguoiDung() != null ? gv.getNguoiDung().getSoDienThoai() : "";
            String avatar = gv.getNguoiDung() != null ? gv.getNguoiDung().getAnhDaiDien() : null;

            giangVienMoiDtos.add(GiangVienMoiDto.builder()
                    .id(gv.getId())
                    .nguoiDungId(gv.getNguoiDung() != null ? gv.getNguoiDung().getId() : null)
                    .hoTen(hoTen)
                    .email(email)
                    .soDienThoai(sdt)
                    .tieuDeChuyenMon(gv.getTieuDeChuyenMon())
                    .anhDaiDien(avatar)
                    .trangThaiDuyet(gv.getTrangThaiDuyet() != null ? gv.getTrangThaiDuyet().name() : "CHO_DUYET")
                    .ngayDangKy(gv.getNgayTao())
                    .build());
        }

        return ThongKeTongQuanResponse.builder()
                .tongDoanhThu(tongDoanhThu)
                .tongNguoiDung(tongNguoiDung)
                .tongKhoaHocDangHoatDong(tongKhoaHocDangHoatDong)
                .lenhRutTienChoDuyet(lenhRutTienChoDuyet)
                .hoSoGiangVienChoDuyet(hoSoChoDuyet)
                .tongYeuCauChoDuyet(tongYeuCauChoDuyet)
                .topKhoaHoc(topKhoaHocDtos)
                .giangVienMoi(giangVienMoiDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DoanhThuThangResponse layDoanhThuTheoNam(Integer nam) {
        int targetYear = (nam != null && nam > 2000) ? nam : LocalDate.now().getYear();
        List<DoanhThuThangResponse.ThangDoanhThu> danhSachThang = new ArrayList<>();
        BigDecimal tongDoanhThuNam = BigDecimal.ZERO;

        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(targetYear, m);
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

            BigDecimal dt = donHangRepository.sumDoanhThuKhoangThoiGian(TrangThaiThanhToan.DA_THANH_TOAN, start, end);
            if (dt == null) {
                dt = BigDecimal.ZERO;
            }

            tongDoanhThuNam = tongDoanhThuNam.add(dt);
            danhSachThang.add(DoanhThuThangResponse.ThangDoanhThu.builder()
                    .thang(m)
                    .nhanThang("Thg " + m)
                    .doanhThu(dt)
                    .build());
        }

        return DoanhThuThangResponse.builder()
                .nam(targetYear)
                .tongDoanhThuNam(tongDoanhThuNam)
                .danhSachThang(danhSachThang)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TyLeDanhMucResponse layTyLeTheoDanhMuc() {
        // Phân loại thành 4 nhóm theo yêu cầu:
        // 1. Cấp 1 (Tiểu học)
        // 2. Cấp 2 (THCS)
        // 3. Cấp 3 (THPT)
        // 4. Ngoại ngữ (IELTS/TOEIC)
        long countCap1 = 0;
        long countCap2 = 0;
        long countCap3 = 0;
        long countNgoaiNgu = 0;

        List<KhoaHoc> allKhoaHoc = khoaHocRepository.findAll();
        for (KhoaHoc k : allKhoaHoc) {
            int students = (k.getTongSoHocVien() != null && k.getTongSoHocVien() > 0) ? k.getTongSoHocVien() : 1;
            String textToSearch = (k.getTieuDe() + " " + (k.getThePhanLoai() != null ? k.getThePhanLoai() : "")).toLowerCase();
            if (k.getDanhMuc() != null) {
                textToSearch += " " + (k.getDanhMuc().getTenDanhMuc() != null ? k.getDanhMuc().getTenDanhMuc() : "") + " "
                        + (k.getDanhMuc().getDuongDanSlug() != null ? k.getDanhMuc().getDuongDanSlug() : "");
                if (k.getDanhMuc().getLoaiDanhMuc() == LoaiDanhMuc.CHUNG_CHI_NGOAI_NGU) {
                    countNgoaiNgu += students;
                    continue;
                }
            }

            if (textToSearch.contains("ielts") || textToSearch.contains("toeic") || textToSearch.contains("tiếng anh") || textToSearch.contains("ngoại ngữ")) {
                countNgoaiNgu += students;
            } else if (textToSearch.contains("tiểu học") || textToSearch.contains("lớp 1") || textToSearch.contains("lớp 2")
                    || textToSearch.contains("lớp 3") || textToSearch.contains("lớp 4") || textToSearch.contains("lớp 5") || textToSearch.contains("cấp 1")) {
                countCap1 += students;
            } else if (textToSearch.contains("thcs") || textToSearch.contains("lớp 6") || textToSearch.contains("lớp 7")
                    || textToSearch.contains("lớp 8") || textToSearch.contains("lớp 9") || textToSearch.contains("cấp 2") || textToSearch.contains("vào 10")) {
                countCap2 += students;
            } else {
                // Mặc định hoặc Cấp 3 / THPT
                countCap3 += students;
            }
        }

        long tongLuot = countCap1 + countCap2 + countCap3 + countNgoaiNgu;
        double tyLeCap1 = 0.0;
        double tyLeCap2 = 0.0;
        double tyLeCap3 = 0.0;
        double tyLeNgoaiNgu = 0.0;

        if (tongLuot > 0) {
            tyLeCap1 = round((double) countCap1 * 100 / tongLuot);
            tyLeCap2 = round((double) countCap2 * 100 / tongLuot);
            tyLeCap3 = round((double) countCap3 * 100 / tongLuot);
            tyLeNgoaiNgu = round(100.0 - (tyLeCap1 + tyLeCap2 + tyLeCap3));
        }

        List<TyLeDanhMucResponse.NhomDanhMucItem> danhSachTyLe = new ArrayList<>();
        danhSachTyLe.add(TyLeDanhMucResponse.NhomDanhMucItem.builder()
                .maNhom("CAP_1")
                .tenNhom("Tiểu học (Cấp 1)")
                .soLuongHocVien(countCap1)
                .tyLePhanTram(tyLeCap1)
                .mauSac("#00A5DF") // Accent Cyan Primary
                .build());

        danhSachTyLe.add(TyLeDanhMucResponse.NhomDanhMucItem.builder()
                .maNhom("CAP_2")
                .tenNhom("THCS (Cấp 2)")
                .soLuongHocVien(countCap2)
                .tyLePhanTram(tyLeCap2)
                .mauSac("#1C699A") // Accent Navy Secondary
                .build());

        danhSachTyLe.add(TyLeDanhMucResponse.NhomDanhMucItem.builder()
                .maNhom("CAP_3")
                .tenNhom("THPT (Cấp 3)")
                .soLuongHocVien(countCap3)
                .tyLePhanTram(tyLeCap3)
                .mauSac("#FF6D20") // Accent Coral Tertiary
                .build());

        danhSachTyLe.add(TyLeDanhMucResponse.NhomDanhMucItem.builder()
                .maNhom("NGOAI_NGU")
                .tenNhom("Ngoại ngữ (IELTS / TOEIC)")
                .soLuongHocVien(countNgoaiNgu)
                .tyLePhanTram(tyLeNgoaiNgu)
                .mauSac("#10B981") // Status Success Green
                .build());

        return TyLeDanhMucResponse.builder()
                .tongLuotHocVien(tongLuot)
                .danhSachTyLe(danhSachTyLe)
                .build();
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}

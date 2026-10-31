package com.studywithme.service.impl;

import com.studywithme.dto.response.DongTienGiaoDichDto;
import com.studywithme.dto.response.ThongKeDongTienResponse;
import com.studywithme.dto.response.ThongKeRutTienResponse;
import com.studywithme.dto.response.YeuCauRutTienResponse;
import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import com.studywithme.entity.enums.shared.LoaiSanPhamDonHang;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.*;
import com.studywithme.service.AdminTaiChinhService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminTaiChinhServiceImpl implements AdminTaiChinhService {

    private final YeuCauRutTienRepository yeuCauRutTienRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final LichSuViTienRepository lichSuViTienRepository;
    private final DonHangRepository donHangRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final LopHocTrucTuyenRepository lopHocTrucTuyenRepository;

    public AdminTaiChinhServiceImpl(YeuCauRutTienRepository yeuCauRutTienRepository,
                                    HoSoGiangVienRepository hoSoGiangVienRepository,
                                    LichSuViTienRepository lichSuViTienRepository,
                                    DonHangRepository donHangRepository,
                                    KhoaHocRepository khoaHocRepository,
                                    LopHocTrucTuyenRepository lopHocTrucTuyenRepository) {
        this.yeuCauRutTienRepository = yeuCauRutTienRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.lichSuViTienRepository = lichSuViTienRepository;
        this.donHangRepository = donHangRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.lopHocTrucTuyenRepository = lopHocTrucTuyenRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<YeuCauRutTienResponse> layDanhSachYeuCauRutTien(TrangThaiRutTien trangThai) {
        List<YeuCauRutTien> list = (trangThai == null)
                ? yeuCauRutTienRepository.findAllByOrderByNgayTaoDesc()
                : yeuCauRutTienRepository.findByTrangThaiOrderByNgayTaoDesc(trangThai);

        return list.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeRutTienResponse layThongKeRutTien() {
        List<YeuCauRutTien> all = yeuCauRutTienRepository.findAll();

        BigDecimal tongTienChoXuLy = BigDecimal.ZERO;
        long soLenhChoXuLy = 0;
        BigDecimal tongTienDaChuyen = BigDecimal.ZERO;
        long soLenhDaChuyen = 0;
        long soLenhTuChoi = 0;

        for (YeuCauRutTien yc : all) {
            if (yc.getTrangThai() == TrangThaiRutTien.CHO_XU_LY) {
                soLenhChoXuLy++;
                if (yc.getSoTienRut() != null) {
                    tongTienChoXuLy = tongTienChoXuLy.add(yc.getSoTienRut());
                }
            } else if (yc.getTrangThai() == TrangThaiRutTien.DA_CHUYEN_TIEN) {
                soLenhDaChuyen++;
                if (yc.getSoTienRut() != null) {
                    tongTienDaChuyen = tongTienDaChuyen.add(yc.getSoTienRut());
                }
            } else if (yc.getTrangThai() == TrangThaiRutTien.TU_CHOI) {
                soLenhTuChoi++;
            }
        }

        return ThongKeRutTienResponse.builder()
                .tongTienChoXuLy(tongTienChoXuLy)
                .soLenhChoXuLy(soLenhChoXuLy)
                .tongTienDaChuyen(tongTienDaChuyen)
                .soLenhDaChuyen(soLenhDaChuyen)
                .soLenhTuChoi(soLenhTuChoi)
                .tongSoLenh((long) all.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public YeuCauRutTienResponse layChiTietYeuCau(Long id) {
        YeuCauRutTien yc = yeuCauRutTienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu rút tiền với mã ID: " + id));
        return mapToResponse(yc);
    }

    @Override
    @Transactional
    public YeuCauRutTienResponse xacNhanChuyenTien(Long id, String ghiChuAdmin) {
        YeuCauRutTien yc = yeuCauRutTienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu rút tiền với mã ID: " + id));

        if (yc.getTrangThai() != TrangThaiRutTien.CHO_XU_LY) {
            throw new IllegalStateException("Lệnh rút tiền này đã được xử lý trước đó, không thể xác nhận lại.");
        }

        yc.setTrangThai(TrangThaiRutTien.DA_CHUYEN_TIEN);
        yc.setNgayXuLy(LocalDateTime.now());
        yc.setGhiChuAdmin(ghiChuAdmin != null && !ghiChuAdmin.isBlank() ? ghiChuAdmin : "Đã hoàn tất chuyển khoản thành công.");
        yc = yeuCauRutTienRepository.save(yc);

        // Ghi nhận sổ cái ví tiền (Ledger)
        HoSoGiangVien gv = yc.getGiangVien();
        if (gv != null) {
            BigDecimal soDuHienTai = gv.getSoDuVi() != null ? gv.getSoDuVi() : BigDecimal.ZERO;
            LichSuViTien lichSu = LichSuViTien.builder()
                    .giangVien(gv)
                    .soTienBienDong(yc.getSoTienRut())
                    .soDuTruoc(soDuHienTai)
                    .soDuSau(soDuHienTai) // Số dư đã bị giữ từ khi tạo lệnh rút
                    .loaiGiaoDich(LoaiGiaoDichVi.RUT_TIEN_VE_NGAN_HANG)
                    .maThamChieu(yc.getId())
                    .moTaGiaoDich("Giải ngân thành công lệnh rút tiền #" + yc.getId() + " về tài khoản " + yc.getTenNganHang() + " - " + yc.getSoTaiKhoan())
                    .build();
            lichSuViTienRepository.save(lichSu);
        }

        return mapToResponse(yc);
    }

    @Override
    @Transactional
    public YeuCauRutTienResponse tuChoiRutTien(Long id, String lyDoTuChoi) {
        YeuCauRutTien yc = yeuCauRutTienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu rút tiền với mã ID: " + id));

        if (yc.getTrangThai() != TrangThaiRutTien.CHO_XU_LY) {
            throw new IllegalStateException("Lệnh rút tiền này đã được xử lý trước đó, không thể từ chối lại.");
        }

        HoSoGiangVien gv = yc.getGiangVien();
        BigDecimal soDuHienTai = gv.getSoDuVi() != null ? gv.getSoDuVi() : BigDecimal.ZERO;
        BigDecimal soTienHoan = yc.getSoTienRut();
        BigDecimal soDuMoi = soDuHienTai.add(soTienHoan);

        gv.setSoDuVi(soDuMoi);
        hoSoGiangVienRepository.save(gv);

        LichSuViTien ls = LichSuViTien.builder()
                .giangVien(gv)
                .soTienBienDong(soTienHoan)
                .soDuTruoc(soDuHienTai)
                .soDuSau(soDuMoi)
                .loaiGiaoDich(LoaiGiaoDichVi.HOAN_TIEN)
                .maThamChieu(yc.getId())
                .moTaGiaoDich("Hoàn tiền yêu cầu rút #" + yc.getId() + " bị từ chối: " + lyDoTuChoi)
                .build();
        lichSuViTienRepository.save(ls);

        yc.setTrangThai(TrangThaiRutTien.TU_CHOI);
        yc.setNgayXuLy(LocalDateTime.now());
        yc.setGhiChuAdmin(lyDoTuChoi);
        yc = yeuCauRutTienRepository.save(yc);

        return mapToResponse(yc);
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeDongTienResponse layThongKeDongTien() {
        // 1. Tổng tiền vào từ các đơn hàng đã thanh toán
        List<DonHang> allOrders = donHangRepository.findAll();
        BigDecimal tongTienVao = BigDecimal.ZERO;
        long soDonHangThanhCong = 0;

        for (DonHang dh : allOrders) {
            if (dh.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN) {
                soDonHangThanhCong++;
                if (dh.getTongTienThanhToan() != null) {
                    tongTienVao = tongTienVao.add(dh.getTongTienThanhToan());
                }
            }
        }

        // 2. Tổng tiền ra từ các lệnh rút tiền đã chuyển khoản
        List<YeuCauRutTien> allPayouts = yeuCauRutTienRepository.findAll();
        BigDecimal tongTienRa = BigDecimal.ZERO;
        long soLenhRutThanhCong = 0;

        for (YeuCauRutTien yc : allPayouts) {
            if (yc.getTrangThai() == TrangThaiRutTien.DA_CHUYEN_TIEN) {
                soLenhRutThanhCong++;
                if (yc.getSoTienRut() != null) {
                    tongTienRa = tongTienRa.add(yc.getSoTienRut());
                }
            }
        }

        // 3. Phí nền tảng 20% giữ lại
        BigDecimal loiNhuanSan = tongTienVao.multiply(BigDecimal.valueOf(0.20));

        // 4. Tổng số dư ví hiện có của các giảng viên
        List<HoSoGiangVien> allTutors = hoSoGiangVienRepository.findAll();
        BigDecimal tongSoDuViGiangVien = BigDecimal.ZERO;
        for (HoSoGiangVien gv : allTutors) {
            if (gv.getSoDuVi() != null) {
                tongSoDuViGiangVien = tongSoDuViGiangVien.add(gv.getSoDuVi());
            }
        }

        return ThongKeDongTienResponse.builder()
                .tongTienVao(tongTienVao)
                .tongTienRa(tongTienRa)
                .loiNhuanSan(loiNhuanSan)
                .tongSoDuViGiangVien(tongSoDuViGiangVien)
                .soDonHangThanhCong(soDonHangThanhCong)
                .soLenhRutThanhCong(soLenhRutThanhCong)
                .tongSoGiaoDich(soDonHangThanhCong + soLenhRutThanhCong)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DongTienGiaoDichDto> layDanhSachDongTien(String loaiFilter, String phanLoaiFilter, String keyword) {
        List<DongTienGiaoDichDto> danhSach = new ArrayList<>();

        Map<Long, String> tenKhoaHocMap = new HashMap<>();
        khoaHocRepository.findAll().forEach(kh -> tenKhoaHocMap.put(kh.getId(), kh.getTieuDe()));

        Map<Long, String> tenLopHocMap = new HashMap<>();
        lopHocTrucTuyenRepository.findAll().forEach(lop -> tenLopHocMap.put(lop.getId(), lop.getTieuDeLop()));

        // 1. Thu thập TIỀN VÀO từ Đơn Hàng (Orders)
        List<DonHang> allOrders = donHangRepository.findAllByOrderByNgayTaoDonDesc();
        for (DonHang dh : allOrders) {
            String tenSp = "Đăng ký học trực tuyến";
            String phanLoai = "KHOA_HOC";

            if (dh.getDanhSachChiTiet() != null && !dh.getDanhSachChiTiet().isEmpty()) {
                ChiTietDonHang firstItem = dh.getDanhSachChiTiet().get(0);
                if (firstItem.getLoaiSanPham() == LoaiSanPhamDonHang.KHOA_HOC_VIDEO) {
                    phanLoai = "KHOA_HOC";
                    tenSp = tenKhoaHocMap.getOrDefault(firstItem.getSanPhamId(), "Khóa học video #" + firstItem.getSanPhamId());
                } else {
                    phanLoai = "GIA_SU";
                    tenSp = tenLopHocMap.getOrDefault(firstItem.getSanPhamId(), "Lớp học gia sư #" + firstItem.getSanPhamId());
                }
                if (dh.getDanhSachChiTiet().size() > 1) {
                    tenSp += " (+" + (dh.getDanhSachChiTiet().size() - 1) + " sản phẩm khác)";
                }
            }

            BigDecimal tongTien = dh.getTongTienThanhToan() != null ? dh.getTongTienThanhToan() : BigDecimal.ZERO;
            BigDecimal phiSan = tongTien.multiply(BigDecimal.valueOf(0.20));
            BigDecimal tienGiangVien = tongTien.multiply(BigDecimal.valueOf(0.80));

            DongTienGiaoDichDto dto = DongTienGiaoDichDto.builder()
                    .maGiaoDich(dh.getMaDonHang())
                    .loaiDongTien("TIEN_VAO")
                    .phanLoaiDichVu(phanLoai)
                    .tenDichVu(tenSp)
                    .nguoiThucHien(dh.getNguoiDung() != null ? dh.getNguoiDung().getHoTen() : "Khách vãng lai")
                    .emailNguoiThucHien(dh.getNguoiDung() != null ? dh.getNguoiDung().getEmail() : "N/A")
                    .vaiTro("HOC_VIEN")
                    .tongTien(tongTien)
                    .phiSan(phiSan)
                    .tienGiangVien(tienGiangVien)
                    .phuongThuc(dh.getPhuongThucThanhToan() != null ? dh.getPhuongThucThanhToan().name() : "PAYOS")
                    .trangThai(dh.getTrangThaiThanhToan() != null ? dh.getTrangThaiThanhToan().name() : "CHO_THANH_TOAN")
                    .thoiGian(dh.getNgayThanhToan() != null ? dh.getNgayThanhToan() : dh.getNgayTaoDon())
                    .moTa("Học viên nộp tiền mua " + (phanLoai.equals("KHOA_HOC") ? "khóa học" : "lớp gia sư") + " (Sàn nhận 20%, GV nhận 80%)")
                    .build();

            danhSach.add(dto);
        }

        // 2. Thu thập TIỀN RA từ Yêu Cầu Rút Tiền của Giảng viên (Payouts)
        List<YeuCauRutTien> allPayouts = yeuCauRutTienRepository.findAllByOrderByNgayTaoDesc();
        for (YeuCauRutTien yc : allPayouts) {
            String tenGv = (yc.getGiangVien() != null && yc.getGiangVien().getNguoiDung() != null)
                    ? yc.getGiangVien().getNguoiDung().getHoTen() : "Giảng viên #" + (yc.getGiangVien() != null ? yc.getGiangVien().getId() : "");
            String emailGv = (yc.getGiangVien() != null && yc.getGiangVien().getNguoiDung() != null)
                    ? yc.getGiangVien().getNguoiDung().getEmail() : "N/A";

            BigDecimal soTien = yc.getSoTienRut() != null ? yc.getSoTienRut() : BigDecimal.ZERO;

            DongTienGiaoDichDto dto = DongTienGiaoDichDto.builder()
                    .maGiaoDich("RUT-" + yc.getId())
                    .loaiDongTien("TIEN_RA")
                    .phanLoaiDichVu("RUT_TIEN")
                    .tenDichVu("Rút tiền về " + (yc.getTenNganHang() != null ? yc.getTenNganHang() : "Ngân hàng") + " (" + yc.getSoTaiKhoan() + ")")
                    .nguoiThucHien(tenGv)
                    .emailNguoiThucHien(emailGv)
                    .vaiTro("GIANG_VIEN")
                    .tongTien(soTien)
                    .phiSan(BigDecimal.ZERO)
                    .tienGiangVien(soTien)
                    .phuongThuc("CHUYEN_KHOAN_NGAN_HANG")
                    .trangThai(yc.getTrangThai() != null ? yc.getTrangThai().name() : "CHO_XU_LY")
                    .thoiGian(yc.getNgayXuLy() != null ? yc.getNgayXuLy() : yc.getNgayTao())
                    .moTa("Giải ngân thu nhập về tài khoản: " + yc.getTenChuTaiKhoan() + " - " + yc.getTenNganHang())
                    .build();

            danhSach.add(dto);
        }

        // 3. Sắp xếp thứ tự thời gian mới nhất lên đầu
        danhSach.sort((a, b) -> {
            if (a.getThoiGian() == null && b.getThoiGian() == null) return 0;
            if (a.getThoiGian() == null) return 1;
            if (b.getThoiGian() == null) return -1;
            return b.getThoiGian().compareTo(a.getThoiGian());
        });

        // 4. Áp dụng bộ lọc (Filter)
        return danhSach.stream()
                .filter(item -> {
                    if (loaiFilter != null && !loaiFilter.isBlank() && !loaiFilter.equalsIgnoreCase("TAT_CA")) {
                        if (!item.getLoaiDongTien().equalsIgnoreCase(loaiFilter)) return false;
                    }

                    if (phanLoaiFilter != null && !phanLoaiFilter.isBlank() && !phanLoaiFilter.equalsIgnoreCase("TAT_CA")) {
                        if (!item.getPhanLoaiDichVu().equalsIgnoreCase(phanLoaiFilter)) return false;
                    }

                    if (keyword != null && !keyword.isBlank()) {
                        String kw = keyword.toLowerCase().trim();
                        boolean matchMa = item.getMaGiaoDich() != null && item.getMaGiaoDich().toLowerCase().contains(kw);
                        boolean matchTen = item.getNguoiThucHien() != null && item.getNguoiThucHien().toLowerCase().contains(kw);
                        boolean matchEmail = item.getEmailNguoiThucHien() != null && item.getEmailNguoiThucHien().toLowerCase().contains(kw);
                        boolean matchDichVu = item.getTenDichVu() != null && item.getTenDichVu().toLowerCase().contains(kw);
                        return matchMa || matchTen || matchEmail || matchDichVu;
                    }

                    return true;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> layTatCaDonHang() {
        return donHangRepository.findAllByOrderByNgayTaoDonDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LichSuViTien> layTatCaLichSuVi() {
        return lichSuViTienRepository.findAllByOrderByNgayTaoDesc();
    }

    private YeuCauRutTienResponse mapToResponse(YeuCauRutTien yc) {
        String hoTen = "N/A";
        String email = "N/A";
        String soDienThoai = "N/A";
        BigDecimal soDuHienTai = BigDecimal.ZERO;

        if (yc.getGiangVien() != null) {
            soDuHienTai = yc.getGiangVien().getSoDuVi() != null ? yc.getGiangVien().getSoDuVi() : BigDecimal.ZERO;
            if (yc.getGiangVien().getNguoiDung() != null) {
                hoTen = yc.getGiangVien().getNguoiDung().getHoTen();
                email = yc.getGiangVien().getNguoiDung().getEmail();
                soDienThoai = yc.getGiangVien().getNguoiDung().getSoDienThoai();
            }
        }

        return YeuCauRutTienResponse.builder()
                .id(yc.getId())
                .giangVienId(yc.getGiangVien() != null ? yc.getGiangVien().getId() : null)
                .tenGiangVien(hoTen)
                .emailGiangVien(email)
                .soDienThoai(soDienThoai)
                .soTienRut(yc.getSoTienRut())
                .soDuViHienTai(soDuHienTai)
                .tenNganHang(yc.getTenNganHang())
                .soTaiKhoan(yc.getSoTaiKhoan())
                .tenChuTaiKhoan(yc.getTenChuTaiKhoan())
                .trangThai(yc.getTrangThai())
                .ngayTao(yc.getNgayTao())
                .ngayXuLy(yc.getNgayXuLy())
                .ghiChuAdmin(yc.getGhiChuAdmin())
                .build();
    }
}

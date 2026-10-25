package com.studywithme.service.impl;

import com.studywithme.dto.cart.CheckoutRequest;
import com.studywithme.dto.cart.PaymentResultDto;
import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import com.studywithme.entity.enums.shared.LoaiSanPhamDonHang;
import com.studywithme.entity.enums.shared.PhuongThucThanhToan;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.*;
import com.studywithme.service.PaymentService;
import com.studywithme.service.VnPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final LichSuViTienRepository lichSuViTienRepository;
    private final VnPayService vnPayService;

    public PaymentServiceImpl(DonHangRepository donHangRepository,
                              ChiTietDonHangRepository chiTietDonHangRepository,
                              KhoaHocRepository khoaHocRepository,
                              GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository,
                              HoSoGiangVienRepository hoSoGiangVienRepository,
                              LichSuViTienRepository lichSuViTienRepository,
                              VnPayService vnPayService) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.ghiDanhKhoaHocRepository = ghiDanhKhoaHocRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.lichSuViTienRepository = lichSuViTienRepository;
        this.vnPayService = vnPayService;
    }

    @Override
    @Transactional
    public Map<String, String> taoDonHangVaLayUrlVnPay(CheckoutRequest request, NguoiDung nguoiDung, String clientIp) {
        if (request.getCourseIds() == null || request.getCourseIds().isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất 1 khóa học.");
        }

        List<KhoaHoc> danhSachKhoaHoc = khoaHocRepository.findAllById(request.getCourseIds());
        if (danhSachKhoaHoc.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin các khóa học đã chọn.");
        }

        BigDecimal tongTien = BigDecimal.ZERO;
        for (KhoaHoc kh : danhSachKhoaHoc) {
            BigDecimal giaBan = (kh.getGiaKhuyenMai() != null && kh.getGiaKhuyenMai().compareTo(BigDecimal.ZERO) >= 0)
                    ? kh.getGiaKhuyenMai()
                    : (kh.getGiaGoc() != null ? kh.getGiaGoc() : BigDecimal.ZERO);
            tongTien = tongTien.add(giaBan);
        }

        // Tạo mã đơn hàng duy nhất SWM-yyyyMMddHHmmss-XXX
        String maDonHang = "SWM-" + System.currentTimeMillis();

        DonHang donHang = DonHang.builder()
                .maDonHang(maDonHang)
                .nguoiDung(nguoiDung)
                .tongTienThanhToan(tongTien)
                .phuongThucThanhToan(PhuongThucThanhToan.VNPAY)
                .trangThaiThanhToan(TrangThaiThanhToan.CHO_THANH_TOAN)
                .build();
        donHangRepository.save(donHang);

        for (KhoaHoc kh : danhSachKhoaHoc) {
            BigDecimal giaBan = (kh.getGiaKhuyenMai() != null && kh.getGiaKhuyenMai().compareTo(BigDecimal.ZERO) >= 0)
                    ? kh.getGiaKhuyenMai()
                    : (kh.getGiaGoc() != null ? kh.getGiaGoc() : BigDecimal.ZERO);

            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .donHang(donHang)
                    .loaiSanPham(LoaiSanPhamDonHang.KHOA_HOC_VIDEO)
                    .sanPhamId(kh.getId())
                    .donGia(giaBan)
                    .build();
            chiTietDonHangRepository.save(chiTiet);
            donHang.getDanhSachChiTiet().add(chiTiet);
        }

        String paymentUrl = vnPayService.createPaymentUrl(donHang, clientIp);

        Map<String, String> response = new HashMap<>();
        response.put("maDonHang", maDonHang);
        response.put("paymentUrl", paymentUrl);
        response.put("tongTien", tongTien.toString());
        return response;
    }

    @Override
    @Transactional
    public PaymentResultDto xuLyVnPayCallback(Map<String, String> queryParams) {
        String maDonHang = queryParams.get("vnp_TxnRef");
        String responseCode = queryParams.get("vnp_ResponseCode");
        String transactionNo = queryParams.get("vnp_TransactionNo");
        String bankCode = queryParams.get("vnp_BankCode");
        String amountStr = queryParams.get("vnp_Amount");

        BigDecimal soTien = BigDecimal.ZERO;
        if (amountStr != null && !amountStr.isBlank()) {
            try {
                soTien = new BigDecimal(amountStr).divide(BigDecimal.valueOf(100));
            } catch (Exception ignored) {}
        }

        boolean validSignature = vnPayService.validateCallback(queryParams);
        if (!validSignature) {
            log.error("Cảnh báo bảo mật: Sai chữ ký checksum VNPAY với đơn hàng #{}", maDonHang);
            return PaymentResultDto.builder()
                    .thanhCong(false)
                    .maDonHang(maDonHang)
                    .maLoi("INVALID_CHECKSUM")
                    .thongDiep("Chữ ký bảo mật giao dịch không hợp lệ.")
                    .build();
        }

        DonHang donHang = donHangRepository.findByMaDonHang(maDonHang).orElse(null);
        if (donHang == null) {
            return PaymentResultDto.builder()
                    .thanhCong(false)
                    .maDonHang(maDonHang)
                    .maLoi("ORDER_NOT_FOUND")
                    .thongDiep("Không tìm thấy đơn hàng trên hệ thống.")
                    .build();
        }

        List<String> tenKhoaHocs = new ArrayList<>();

        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            if (donHang.getTrangThaiThanhToan() == TrangThaiThanhToan.CHO_THANH_TOAN) {
                donHang.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
                donHang.setNgayThanhToan(LocalDateTime.now());
                donHangRepository.save(donHang);

                NguoiDung hocVien = donHang.getNguoiDung();
                List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangRepository.findByDonHangId(donHang.getId());
                if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
                    danhSachChiTiet = donHang.getDanhSachChiTiet() != null ? donHang.getDanhSachChiTiet() : Collections.emptyList();
                }

                for (ChiTietDonHang ct : danhSachChiTiet) {
                    if (ct.getLoaiSanPham() == LoaiSanPhamDonHang.KHOA_HOC_VIDEO) {
                        KhoaHoc kh = khoaHocRepository.findById(ct.getSanPhamId()).orElse(null);
                        if (kh != null) {
                            tenKhoaHocs.add(kh.getTieuDe());

                            // Ghi danh nếu chưa có
                            if (!ghiDanhKhoaHocRepository.existsByNguoiDungIdAndKhoaHocId(hocVien.getId(), kh.getId())) {
                                GhiDanhKhoaHoc ghiDanh = GhiDanhKhoaHoc.builder()
                                        .nguoiDung(hocVien)
                                        .khoaHoc(kh)
                                        .donHang(donHang)
                                        .phanTramTienDo(0)
                                        .build();
                                ghiDanhKhoaHocRepository.save(ghiDanh);

                                kh.setSoHocVien(kh.getSoHocVien() != null ? kh.getSoHocVien() + 1 : 1);
                                khoaHocRepository.save(kh);
                            }

                            // Cộng doanh thu 80% cho Giảng viên
                            HoSoGiangVien gv = kh.getGiangVien();
                            if (gv != null) {
                                BigDecimal tienGiangVien = ct.getDonGia().multiply(BigDecimal.valueOf(0.80));
                                BigDecimal soDuTruoc = gv.getSoDuVi() != null ? gv.getSoDuVi() : BigDecimal.ZERO;
                                BigDecimal soDuSau = soDuTruoc.add(tienGiangVien);

                                gv.setSoDuVi(soDuSau);
                                hoSoGiangVienRepository.save(gv);

                                LichSuViTien ls = LichSuViTien.builder()
                                        .giangVien(gv)
                                        .soTienBienDong(tienGiangVien)
                                        .soDuTruoc(soDuTruoc)
                                        .soDuSau(soDuSau)
                                        .loaiGiaoDich(LoaiGiaoDichVi.TIEN_BAN_KHOA_HOC)
                                        .maThamChieu(donHang.getId())
                                        .moTaGiaoDich("Thu nhập 80% bán khóa học: " + kh.getTieuDe() + " (Đơn #" + donHang.getMaDonHang() + ")")
                                        .build();
                                lichSuViTienRepository.save(ls);
                            }
                        }
                    }
                }
            } else {
                List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangRepository.findByDonHangId(donHang.getId());
                if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
                    danhSachChiTiet = donHang.getDanhSachChiTiet() != null ? donHang.getDanhSachChiTiet() : Collections.emptyList();
                }
                for (ChiTietDonHang ct : danhSachChiTiet) {
                    KhoaHoc kh = khoaHocRepository.findById(ct.getSanPhamId()).orElse(null);
                    if (kh != null) tenKhoaHocs.add(kh.getTieuDe());
                }
            }

            return PaymentResultDto.builder()
                    .thanhCong(true)
                    .maDonHang(maDonHang)
                    .maGiaoDichVnpay(transactionNo)
                    .soTien(soTien)
                    .nganHang(bankCode)
                    .thoiGianThanhToan(donHang.getNgayThanhToan() != null ? donHang.getNgayThanhToan() : LocalDateTime.now())
                    .thongDiep("Giao dịch thanh toán thành công qua VNPAY.")
                    .danhSachKhoaHoc(tenKhoaHocs)
                    .build();
        } else {
            // Thanh toán thất bại hoặc khách hủy
            donHang.setTrangThaiThanhToan(TrangThaiThanhToan.THAT_BAI);
            donHangRepository.save(donHang);

            String thongDiep = switch (responseCode) {
                case "24" -> "Giao dịch không thành công: Bạn đã hủy giao dịch trên cổng thanh toán.";
                case "11" -> "Giao dịch không thành công: Đã hết hạn chờ thanh toán.";
                case "51" -> "Giao dịch không thành công: Tài khoản của quý khách không đủ số dư.";
                case "79" -> "Giao dịch không thành công: Nhập sai mật khẩu OTP quá số lần quy định.";
                default -> "Giao dịch không thành công. Mã lỗi phản hồi từ VNPAY: " + responseCode;
            };

            return PaymentResultDto.builder()
                    .thanhCong(false)
                    .maDonHang(maDonHang)
                    .maLoi(responseCode)
                    .thongDiep(thongDiep)
                    .soTien(soTien)
                    .nganHang(bankCode)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DonHang layDonHangTheoMa(String maDonHang) {
        return donHangRepository.findByMaDonHang(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + maDonHang));
    }
}

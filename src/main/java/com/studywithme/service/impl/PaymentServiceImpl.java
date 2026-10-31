package com.studywithme.service.impl;

import com.studywithme.dto.cart.CheckoutRequest;
import com.studywithme.dto.cart.PaymentResultDto;
import com.studywithme.entity.*;
import com.studywithme.entity.enums.shared.LoaiSanPhamDonHang;
import com.studywithme.entity.enums.shared.PhuongThucThanhToan;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.*;
import com.studywithme.service.PayOsService;
import com.studywithme.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final PayOsService payOsService;

    public PaymentServiceImpl(DonHangRepository donHangRepository,
                              ChiTietDonHangRepository chiTietDonHangRepository,
                              KhoaHocRepository khoaHocRepository,
                              PayOsService payOsService) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.payOsService = payOsService;
    }

    @Override
    @Transactional
    public Map<String, String> taoDonHangVaLayUrlPayOs(CheckoutRequest request, NguoiDung nguoiDung, String baseUrl) {
        List<Long> courseIds = request.getCourseIds();
        if (courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải chứa ít nhất 1 khóa học.");
        }

        List<KhoaHoc> khoaHocs = khoaHocRepository.findAllById(courseIds);
        if (khoaHocs.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học hợp lệ trong giỏ hàng.");
        }

        BigDecimal tongTien = BigDecimal.ZERO;
        for (KhoaHoc kh : khoaHocs) {
            BigDecimal gia = (kh.getGiaKhuyenMai() != null && kh.getGiaKhuyenMai().compareTo(BigDecimal.ZERO) >= 0)
                    ? kh.getGiaKhuyenMai()
                    : (kh.getGiaGoc() != null ? kh.getGiaGoc() : BigDecimal.ZERO);
            if (gia != null) {
                tongTien = tongTien.add(gia);
            }
        }

        String maDonHang = "SWM-" + System.currentTimeMillis();

        DonHang donHang = DonHang.builder()
                .maDonHang(maDonHang)
                .nguoiDung(nguoiDung)
                .tongTienThanhToan(tongTien)
                .phuongThucThanhToan(PhuongThucThanhToan.PAYOS)
                .trangThaiThanhToan(TrangThaiThanhToan.CHO_THANH_TOAN)
                .build();
        donHang = donHangRepository.save(donHang);

        for (KhoaHoc kh : khoaHocs) {
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

        String paymentUrl = payOsService.taoLinkThanhToan(donHang, baseUrl);

        Map<String, String> response = new HashMap<>();
        response.put("maDonHang", maDonHang);
        response.put("paymentUrl", paymentUrl);
        response.put("tongTien", tongTien.toString());
        response.put("orderCode", donHang.getOrderCodePayos() != null ? donHang.getOrderCodePayos().toString() : "");
        return response;
    }

    @Override
    @Transactional
    public PaymentResultDto xuLyPayOsCallback(Long orderCode) {
        if (orderCode == null) {
            return PaymentResultDto.builder()
                    .thanhCong(false)
                    .maLoi("INVALID_ORDER_CODE")
                    .thongDiep("Mã đơn hàng payOS không hợp lệ.")
                    .build();
        }

        DonHang donHang = donHangRepository.findByOrderCodePayos(orderCode).orElse(null);
        if (donHang == null) {
            return PaymentResultDto.builder()
                    .thanhCong(false)
                    .maLoi("ORDER_NOT_FOUND")
                    .thongDiep("Không tìm thấy đơn hàng trên hệ thống.")
                    .build();
        }

        List<String> tenKhoaHocs = new ArrayList<>();
        List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangRepository.findByDonHangId(donHang.getId());
        if (danhSachChiTiet != null) {
            for (ChiTietDonHang ct : danhSachChiTiet) {
                if (ct.getLoaiSanPham() == LoaiSanPhamDonHang.KHOA_HOC_VIDEO) {
                    khoaHocRepository.findById(ct.getSanPhamId()).ifPresent(kh -> tenKhoaHocs.add(kh.getTieuDe()));
                }
            }
        }

        // Nếu đã thanh toán qua Webhook trước đó
        if (donHang.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN) {
            return PaymentResultDto.builder()
                    .thanhCong(true)
                    .maDonHang(donHang.getMaDonHang())
                    .maGiaoDich(donHang.getMaGiaoDichPayos() != null ? donHang.getMaGiaoDichPayos() : String.valueOf(orderCode))
                    .soTien(donHang.getTongTienThanhToan())
                    .nganHang("VietQR / payOS")
                    .thoiGianThanhToan(donHang.getNgayThanhToan())
                    .danhSachKhoaHoc(tenKhoaHocs)
                    .thongDiep("Thanh toán đơn hàng qua VietQR payOS thành công.")
                    .build();
        }

        // Nếu chưa, kiểm tra trực tiếp trạng thái từ payOS
        PaymentLink linkInfo = payOsService.layThongTinThanhToan(orderCode);
        if (linkInfo != null && "PAID".equalsIgnoreCase(linkInfo.getStatus() != null ? linkInfo.getStatus().name() : "")) {
            WebhookData dummyData = WebhookData.builder()
                    .orderCode(orderCode)
                    .accountNumber("123456789")
                    .reference(String.valueOf(orderCode))
                    .build();
            payOsService.xuLyDonHangThanhCong(dummyData);

            return PaymentResultDto.builder()
                    .thanhCong(true)
                    .maDonHang(donHang.getMaDonHang())
                    .maGiaoDich(String.valueOf(orderCode))
                    .soTien(donHang.getTongTienThanhToan())
                    .nganHang("VietQR / payOS")
                    .thoiGianThanhToan(LocalDateTime.now())
                    .danhSachKhoaHoc(tenKhoaHocs)
                    .thongDiep("Thanh toán đơn hàng qua VietQR payOS thành công.")
                    .build();
        }

        return PaymentResultDto.builder()
                .thanhCong(false)
                .maDonHang(donHang.getMaDonHang())
                .maLoi("PENDING")
                .thongDiep("Giao dịch đang chờ thanh toán hoặc chưa hoàn tất chuyển khoản.")
                .soTien(donHang.getTongTienThanhToan())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DonHang layDonHangTheoMa(String maDonHang) {
        return donHangRepository.findByMaDonHang(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + maDonHang));
    }
}

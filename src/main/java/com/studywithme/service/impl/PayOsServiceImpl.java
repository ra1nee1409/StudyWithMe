package com.studywithme.service.impl;

import com.studywithme.config.PayOSConfig;
import com.studywithme.entity.ChiTietDonHang;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.GhiDanhKhoaHoc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.LichSuViTien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import com.studywithme.entity.enums.shared.LoaiSanPhamDonHang;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import com.studywithme.repository.ChiTietDonHangRepository;
import com.studywithme.repository.DonHangRepository;
import com.studywithme.repository.GhiDanhKhoaHocRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.repository.LichSuViTienRepository;
import com.studywithme.service.PayOsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.ConfirmWebhookResponse;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PayOsServiceImpl implements PayOsService {

    private static final Logger log = LoggerFactory.getLogger(PayOsServiceImpl.class);

    private final PayOS payOS;
    private final PayOSConfig payOSConfig;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final LichSuViTienRepository lichSuViTienRepository;

    public PayOsServiceImpl(PayOS payOS,
                            PayOSConfig payOSConfig,
                            DonHangRepository donHangRepository,
                            ChiTietDonHangRepository chiTietDonHangRepository,
                            KhoaHocRepository khoaHocRepository,
                            GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository,
                            HoSoGiangVienRepository hoSoGiangVienRepository,
                            LichSuViTienRepository lichSuViTienRepository) {
        this.payOS = payOS;
        this.payOSConfig = payOSConfig;
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.ghiDanhKhoaHocRepository = ghiDanhKhoaHocRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.lichSuViTienRepository = lichSuViTienRepository;
    }

    @Override
    @Transactional
    public String taoLinkThanhToan(DonHang donHang, String baseUrl) {
        // Sinh orderCode dạng số nguyên dương an toàn 10 chữ số cho chuẩn VietQR
        long orderCode = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(3));
        donHang.setOrderCodePayos(orderCode);
        donHangRepository.save(donHang);

        // Mô tả giao dịch tối đa 25 ký tự không dấu chuẩn quy định ngân hàng
        String description = "SWM Don " + donHang.getId();
        if (description.length() > 25) {
            description = description.substring(0, 25);
        }

        // Tạo danh sách mặt hàng
        List<PaymentLinkItem> items = new ArrayList<>();
        List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangRepository.findByDonHangId(donHang.getId());
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            danhSachChiTiet = donHang.getDanhSachChiTiet() != null ? donHang.getDanhSachChiTiet() : Collections.emptyList();
        }

        for (ChiTietDonHang ct : danhSachChiTiet) {
            String tenSp = "Khoa hoc #" + ct.getSanPhamId();
            if (ct.getLoaiSanPham() == LoaiSanPhamDonHang.KHOA_HOC_VIDEO) {
                KhoaHoc kh = khoaHocRepository.findById(ct.getSanPhamId()).orElse(null);
                if (kh != null && kh.getTieuDe() != null) {
                    tenSp = kh.getTieuDe();
                }
            }
            if (tenSp.length() > 50) {
                tenSp = tenSp.substring(0, 47) + "...";
            }

            PaymentLinkItem item = PaymentLinkItem.builder()
                    .name(tenSp)
                    .quantity(1)
                    .price(ct.getDonGia() != null ? ct.getDonGia().longValue() : 0L)
                    .build();
            items.add(item);
        }

        String returnUrl = (baseUrl != null ? baseUrl : "http://localhost:8080") + "/payment/payos-callback?orderCode=" + orderCode;
        String cancelUrl = (baseUrl != null ? baseUrl : "http://localhost:8080") + "/checkout?cancel=true";

        long totalAmount = donHang.getTongTienThanhToan() != null ? donHang.getTongTienThanhToan().longValue() : 0L;

        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(totalAmount)
                .description(description)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .items(items)
                .buyerName(donHang.getNguoiDung() != null ? donHang.getNguoiDung().getHoTen() : null)
                .buyerEmail(donHang.getNguoiDung() != null ? donHang.getNguoiDung().getEmail() : null)
                .build();

        log.info("Khởi tạo link thanh toán payOS cho đơn hàng #{} với orderCode: {}", donHang.getMaDonHang(), orderCode);
        CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);

        if (response != null) {
            if (response.getPaymentLinkId() != null) {
                donHang.setMaGiaoDichPayos(response.getPaymentLinkId());
                donHangRepository.save(donHang);
            }
            return response.getCheckoutUrl();
        }

        throw new IllegalStateException("Không nhận được checkoutUrl từ payOS");
    }

    @Override
    public WebhookData xacThucWebhook(Object webhookBody) {
        log.info("Xác thực Webhook payload từ payOS...");
        return payOS.webhooks().verify(webhookBody);
    }

    @Override
    @Transactional
    public boolean xuLyDonHangThanhCong(WebhookData webhookData) {
        if (webhookData == null || webhookData.getOrderCode() == null) {
            log.warn("Dữ liệu webhook không hợp lệ: null");
            return false;
        }

        Long orderCode = webhookData.getOrderCode();
        DonHang donHang = donHangRepository.findByOrderCodePayos(orderCode)
                .orElse(null);

        if (donHang == null) {
            log.warn("Không tìm thấy đơn hàng với payOS orderCode: {} (có thể là Webhook test từ payOS Dashboard).", orderCode);
            return false;
        }

        if (donHang.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN) {
            log.info("Đơn hàng #{} đã được xử lý thanh toán trước đó (Idempotency check).", donHang.getMaDonHang());
            return true;
        }

        // Cập nhật trạng thái đơn hàng
        donHang.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
        donHang.setNgayThanhToan(LocalDateTime.now());
        if (webhookData.getReference() != null && !webhookData.getReference().isBlank()) {
            donHang.setMaGiaoDichPayos(webhookData.getReference());
        }
        donHangRepository.save(donHang);

        // Kích hoạt khóa học cho học viên & cộng 80% ví giảng viên
        NguoiDung hocVien = donHang.getNguoiDung();
        List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangRepository.findByDonHangId(donHang.getId());
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            danhSachChiTiet = donHang.getDanhSachChiTiet() != null ? donHang.getDanhSachChiTiet() : Collections.emptyList();
        }

        for (ChiTietDonHang ct : danhSachChiTiet) {
            if (ct.getLoaiSanPham() == LoaiSanPhamDonHang.KHOA_HOC_VIDEO) {
                KhoaHoc kh = khoaHocRepository.findById(ct.getSanPhamId()).orElse(null);
                if (kh != null) {
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
                                .moTaGiaoDich("Thu nhập 80% bán khóa học (payOS): " + kh.getTieuDe() + " (Đơn #" + donHang.getMaDonHang() + ")")
                                .build();
                        lichSuViTienRepository.save(ls);
                    }
                }
            }
        }

        log.info("Đã kích hoạt bản quyền khóa học và cộng ví giảng viên thành công cho đơn #{}", donHang.getMaDonHang());
        return true;
    }

    @Override
    public PaymentLink layThongTinThanhToan(Long orderCode) {
        if (orderCode == null) return null;
        try {
            return payOS.paymentRequests().get(orderCode);
        } catch (Exception e) {
            log.error("Lỗi lấy thông tin payment link từ payOS: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public PaymentLink huyLinkThanhToan(Long orderCode, String lyDoHuy) {
        if (orderCode == null) return null;
        try {
            return payOS.paymentRequests().cancel(orderCode, lyDoHuy != null ? lyDoHuy : "Hủy giao dịch theo yêu cầu");
        } catch (Exception e) {
            log.error("Lỗi khi hủy payment link trên payOS: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String dangKyWebhook(String webhookUrl) {
        try {
            log.info("Đăng ký Webhook URL với payOS: {}", webhookUrl);
            ConfirmWebhookResponse response = payOS.webhooks().confirm(webhookUrl);
            return response != null ? response.getWebhookUrl() : webhookUrl;
        } catch (Exception e) {
            log.error("Lỗi khi đăng ký webhook URL với payOS: {}", e.getMessage());
            throw new RuntimeException("Không thể xác nhận webhook URL với payOS: " + e.getMessage(), e);
        }
    }
}

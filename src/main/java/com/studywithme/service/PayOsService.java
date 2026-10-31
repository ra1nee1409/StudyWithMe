package com.studywithme.service;

import com.studywithme.entity.DonHang;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.webhooks.WebhookData;

public interface PayOsService {

    /**
     * Tạo link thanh toán chuẩn VietQR payOS cho đơn hàng học viên.
     * @param donHang Đơn hàng đã được lưu trạng thái CHO_THANH_TOAN
     * @param baseUrl Base URL của hệ thống (VD: http://localhost:8080)
     * @return URL trang thanh toán VietQR của payOS
     */
    String taoLinkThanhToan(DonHang donHang, String baseUrl);

    /**
     * Xác thực dữ liệu Webhook từ payOS gửi về bằng Checksum Key.
     * @param webhookBody Đối tượng Webhook hoặc JSON payload
     * @return WebhookData đã được verify an toàn
     */
    WebhookData xacThucWebhook(Object webhookBody);

    /**
     * Xử lý cộng tiền ví giảng viên, ghi danh khóa học và cập nhật đơn hàng sang DA_THANH_TOAN.
     * @param webhookData Dữ liệu giao dịch từ payOS
     * @return true nếu xử lý thành công
     */
    boolean xuLyDonHangThanhCong(WebhookData webhookData);

    /**
     * Tra cứu thông tin trạng thái thanh toán từ payOS qua mã orderCode.
     */
    PaymentLink layThongTinThanhToan(Long orderCode);
}

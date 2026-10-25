package com.studywithme.service;

import com.studywithme.entity.DonHang;

import java.util.Map;

public interface VnPayService {

    /**
     * Tạo đường dẫn thanh toán chuyển hướng sang cổng VNPAY Sandbox.
     * @param donHang Đơn hàng cần thanh toán
     * @param clientIp Địa chỉ IP của máy khách
     * @return URL thanh toán VNPAY đã ký checksum SHA512
     */
    String createPaymentUrl(DonHang donHang, String clientIp);

    /**
     * Kiểm tra tính toàn vẹn của dữ liệu callback hoặc IPN gửi về từ VNPAY.
     * @param queryParams Bản đồ các tham số query do VNPAY gửi về
     * @return true nếu chữ ký hợp lệ, false nếu giả mạo
     */
    boolean validateCallback(Map<String, String> queryParams);
}

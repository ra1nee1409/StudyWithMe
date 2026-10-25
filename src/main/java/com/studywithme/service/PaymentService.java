package com.studywithme.service;

import com.studywithme.dto.cart.CheckoutRequest;
import com.studywithme.dto.cart.PaymentResultDto;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.NguoiDung;

import java.util.Map;

public interface PaymentService {

    /**
     * Tạo đơn hàng mới và sinh đường dẫn thanh toán VNPAY Sandbox đã ký bảo mật SHA512.
     * @param request Thông tin giỏ hàng hoặc khóa học cần mua
     * @param nguoiDung Học viên thực hiện mua hàng
     * @param clientIp Địa chỉ IP máy khách
     * @return Map chứa thông tin đơn hàng và URL chuyển hướng VNPAY
     */
    Map<String, String> taoDonHangVaLayUrlVnPay(CheckoutRequest request, NguoiDung nguoiDung, String clientIp);

    /**
     * Xử lý callback do VNPAY Sandbox chuyển hướng về sau khi học viên thanh toán.
     * @param queryParams Toàn bộ query params từ VNPAY
     * @return Kết quả thanh toán chi tiết (thành công / thất bại)
     */
    PaymentResultDto xuLyVnPayCallback(Map<String, String> queryParams);

    /**
     * Lấy thông tin đơn hàng theo mã đơn
     */
    DonHang layDonHangTheoMa(String maDonHang);
}

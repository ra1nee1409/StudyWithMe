package com.studywithme.service;

import com.studywithme.dto.cart.CheckoutRequest;
import com.studywithme.dto.cart.PaymentResultDto;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.NguoiDung;

import java.util.Map;

public interface PaymentService {

    /**
     * Tạo đơn hàng mới và sinh đường dẫn thanh toán VietQR payOS.
     * @param request Thông tin giỏ hàng hoặc khóa học cần mua
     * @param nguoiDung Học viên thực hiện mua hàng
     * @param baseUrl URL gốc của server để sinh redirect URL
     * @return Map chứa thông tin đơn hàng và paymentUrl từ payOS
     */
    Map<String, String> taoDonHangVaLayUrlPayOs(CheckoutRequest request, NguoiDung nguoiDung, String baseUrl);

    /**
     * Xử lý kết quả trả về khi người dùng hoàn tất chuyển khoản payOS và quay lại web.
     * @param orderCode Mã đơn hàng dạng số của payOS
     * @return Kết quả thanh toán chi tiết
     */
    PaymentResultDto xuLyPayOsCallback(Long orderCode);

    /**
     * Lấy thông tin đơn hàng theo mã đơn
     */
    DonHang layDonHangTheoMa(String maDonHang);
}

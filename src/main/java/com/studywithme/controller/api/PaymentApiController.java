package com.studywithme.controller.api;

import com.studywithme.dto.cart.CheckoutRequest;
import com.studywithme.dto.cart.PaymentResultDto;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.entity.NguoiDung;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    private final PaymentService paymentService;
    private final NguoiDungRepository nguoiDungRepository;

    public PaymentApiController(PaymentService paymentService,
                                NguoiDungRepository nguoiDungRepository) {
        this.paymentService = paymentService;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    /**
     * API Tạo đơn hàng và lấy URL VNPAY Sandbox
     */
    @PostMapping("/tao-don-hang")
    public ResponseEntity<ApiResponse<Map<String, String>>> taoDonHang(
            @Valid @RequestBody CheckoutRequest request,
            Principal principal,
            HttpServletRequest httpServletRequest) {

        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Vui lòng đăng nhập để thanh toán đơn hàng."));
        }

        NguoiDung user = nguoiDungRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản người dùng."));

        String clientIp = getClientIp(httpServletRequest);
        Map<String, String> paymentData = paymentService.taoDonHangVaLayUrlVnPay(request, user, clientIp);

        return ResponseEntity.ok(ApiResponse.success("Khởi tạo đơn hàng thành công.", paymentData));
    }

    /**
     * API kiểm tra kết quả callback / webhook
     */
    @GetMapping("/vnpay-callback")
    public ResponseEntity<ApiResponse<PaymentResultDto>> xuLyCallback(@RequestParam Map<String, String> queryParams) {
        PaymentResultDto result = paymentService.xuLyVnPayCallback(queryParams);
        if (result.isThanhCong()) {
            return ResponseEntity.ok(ApiResponse.success("Thanh toán thành công.", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getThongDiep(), result));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return (ip != null) ? ip : "127.0.0.1";
    }
}

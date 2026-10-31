package com.studywithme.controller.api;

import com.studywithme.dto.cart.CheckoutRequest;
import com.studywithme.dto.cart.PaymentResultDto;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.entity.NguoiDung;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.PayOsService;
import com.studywithme.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {

    private static final Logger log = LoggerFactory.getLogger(PaymentApiController.class);

    private final PaymentService paymentService;
    private final PayOsService payOsService;
    private final NguoiDungRepository nguoiDungRepository;

    public PaymentApiController(PaymentService paymentService,
                                PayOsService payOsService,
                                NguoiDungRepository nguoiDungRepository) {
        this.paymentService = paymentService;
        this.payOsService = payOsService;
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
     * API Tạo đơn hàng và lấy URL thanh toán VietQR payOS
     */
    @PostMapping("/payos/tao-don-hang")
    public ResponseEntity<ApiResponse<Map<String, String>>> taoDonHangPayOs(
            @Valid @RequestBody CheckoutRequest request,
            Principal principal,
            HttpServletRequest httpServletRequest) {

        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Vui lòng đăng nhập để thanh toán đơn hàng."));
        }

        NguoiDung user = nguoiDungRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản người dùng."));

        String baseUrl = getBaseUrl(httpServletRequest);
        Map<String, String> paymentData = paymentService.taoDonHangVaLayUrlPayOs(request, user, baseUrl);

        return ResponseEntity.ok(ApiResponse.success("Khởi tạo đơn hàng payOS thành công.", paymentData));
    }

    /**
     * Endpoint Webhook chính thức nhận thông báo chuyển khoản thành công từ payOS
     * Hỗ trợ tự động xác thực URL khi bấm nút 'Xác nhận Webhook' trên Dashboard payOS
     * Tài liệu chuẩn: https://payos.vn/docs/du-lieu-tra-ve/webhook/
     */
    @PostMapping("/payos_transfer_handler")
    public ResponseEntity<Map<String, Object>> handlePayOSWebhook(@RequestBody Webhook webhookPayload) {
        try {
            log.info("Nhận Webhook từ payOS: code={}, desc={}, success={}",
                    webhookPayload.getCode(), webhookPayload.getDesc(), webhookPayload.getSuccess());

            // 1. Xác thực tính toàn vẹn và chữ ký số HMAC-SHA256 với checksumKey
            WebhookData data = payOsService.xacThucWebhook(webhookPayload);

            if (data != null) {
                log.info("Dữ liệu giao dịch payOS: orderCode={}, amount={}, reference={}",
                        data.getOrderCode(), data.getAmount(), data.getReference());

                // 2. Xử lý kích hoạt đơn hàng & cộng tiền giảng viên
                boolean xuLyOk = payOsService.xuLyDonHangThanhCong(data);
                if (xuLyOk) {
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "Xử lý đơn hàng và kích hoạt khóa học thành công"
                    ));
                } else {
                    // Dữ liệu hợp lệ nhưng là bản tin test xác nhận webhook từ Dashboard my.payos.vn
                    log.info("Bản tin Webhook test từ payOS Dashboard đã được xác thực chữ ký hợp lệ.");
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "Webhook URL verified successfully"
                    ));
                }
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Webhook received"));
        } catch (Exception e) {
            log.error("Cảnh báo: Lỗi chữ ký hoặc dữ liệu webhook payOS không hợp lệ: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Invalid webhook signature: " + e.getMessage()
            ));
        }
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

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
            return scheme + "://" + serverName;
        }
        return scheme + "://" + serverName + ":" + serverPort;
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

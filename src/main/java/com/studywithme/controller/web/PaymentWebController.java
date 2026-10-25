package com.studywithme.controller.web;

import com.studywithme.dto.cart.CheckoutRequest;
import com.studywithme.dto.cart.PaymentResultDto;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.NguoiDung;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
public class PaymentWebController {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebController.class);

    private final PaymentService paymentService;
    private final KhoaHocRepository khoaHocRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public PaymentWebController(PaymentService paymentService,
                                KhoaHocRepository khoaHocRepository,
                                NguoiDungRepository nguoiDungRepository) {
        this.paymentService = paymentService;
        this.khoaHocRepository = khoaHocRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    private Optional<NguoiDung> getUserFromPrincipal(Principal principal) {
        if (principal == null) return Optional.empty();
        return nguoiDungRepository.findByEmail(principal.getName());
    }

    /**
     * Trang giỏ hàng
     */
    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserFromPrincipal(principal);
        model.addAttribute("currentUser", userOpt.orElse(null));
        model.addAttribute("isLoggedIn", userOpt.isPresent());
        return "cart/index";
    }

    /**
     * Trang thanh toán đơn hàng (Checkout)
     */
    @GetMapping("/checkout")
    public String checkout(@RequestParam(required = false) Long courseId,
                           @RequestParam(required = false) String courseIds,
                           Model model,
                           Principal principal) {
        Optional<NguoiDung> userOpt = getUserFromPrincipal(principal);
        
        List<KhoaHoc> items = new ArrayList<>();
        Set<Long> idsToFetch = new HashSet<>();

        if (courseId != null) {
            idsToFetch.add(courseId);
        }
        if (courseIds != null && !courseIds.isBlank()) {
            for (String part : courseIds.split(",")) {
                try {
                    idsToFetch.add(Long.parseLong(part.trim()));
                } catch (NumberFormatException ignored) {}
            }
        }

        if (!idsToFetch.isEmpty()) {
            items = khoaHocRepository.findAllById(idsToFetch);
        }

        java.math.BigDecimal tongTien = java.math.BigDecimal.ZERO;
        for (KhoaHoc kh : items) {
            java.math.BigDecimal gia = (kh.getGiaKhuyenMai() != null && kh.getGiaKhuyenMai().compareTo(java.math.BigDecimal.ZERO) >= 0)
                    ? kh.getGiaKhuyenMai()
                    : (kh.getGiaGoc() != null ? kh.getGiaGoc() : java.math.BigDecimal.ZERO);
            tongTien = tongTien.add(gia);
        }

        model.addAttribute("currentUser", userOpt.orElse(null));
        model.addAttribute("isLoggedIn", userOpt.isPresent());
        model.addAttribute("items", items);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("buyNowCourse", items.isEmpty() ? null : items.get(0));
        model.addAttribute("selectedCourseId", courseId);

        // Thông tin test VNPAY Sandbox thuận tiện cho người dùng và giám khảo chấm đồ án
        model.addAttribute("vnpayCardBank", "NCB");
        model.addAttribute("vnpayCardNumber", "9704198526191432198");
        model.addAttribute("vnpayCardHolder", "NGUYEN VAN A");
        model.addAttribute("vnpayCardDate", "07/15");
        model.addAttribute("vnpayCardOtp", "123456");
        model.addAttribute("vnpayTmnCode", "8QVUIHRL");

        return "checkout/index";
    }

    /**
     * Xử lý gửi thanh toán chuyển tiếp sang VNPAY Gateway
     */
    @PostMapping("/checkout/vnpay")
    public String processVnpayCheckout(@RequestParam(name = "courseIds", required = false) List<Long> courseIds,
                                       @RequestParam(name = "ghiChu", required = false) String ghiChu,
                                       Principal principal,
                                       HttpServletRequest request,
                                       Model model) {
        if (courseIds == null || courseIds.isEmpty()) {
            model.addAttribute("error", "Vui lòng chọn ít nhất 1 khóa học để thanh toán.");
            return "redirect:/cart";
        }

        Optional<NguoiDung> userOpt = getUserFromPrincipal(principal);
        if (userOpt.isEmpty()) {
            // Nếu chưa đăng nhập, chuyển tới trang đăng nhập kèm redirect
            return "redirect:/login?redirect=/checkout" + (courseIds.size() == 1 ? "?courseId=" + courseIds.get(0) : "");
        }

        NguoiDung user = userOpt.get();
        String clientIp = getClientIp(request);

        CheckoutRequest checkoutReq = new CheckoutRequest();
        checkoutReq.setCourseIds(courseIds);
        checkoutReq.setPaymentMethod("VNPAY");
        checkoutReq.setGhiChu(ghiChu);

        try {
            Map<String, String> result = paymentService.taoDonHangVaLayUrlVnPay(checkoutReq, user, clientIp);
            String paymentUrl = result.get("paymentUrl");
            return "redirect:" + paymentUrl;
        } catch (Exception e) {
            log.error("Lỗi khi tạo đơn hàng VNPAY", e);
            model.addAttribute("error", "Đã xảy ra lỗi khi tạo đơn hàng: " + e.getMessage());
            return "redirect:/checkout?error=" + e.getMessage();
        }
    }

    /**
     * Nhận kết quả callback từ VNPAY chuyển hướng về
     */
    @GetMapping("/payment/vnpay-callback")
    public String vnpayCallback(@RequestParam Map<String, String> allParams,
                                Model model,
                                Principal principal) {
        log.info("Nhận callback từ VNPAY Sandbox: {}", allParams);
        PaymentResultDto result = paymentService.xuLyVnPayCallback(allParams);
        
        Optional<NguoiDung> userOpt = getUserFromPrincipal(principal);
        model.addAttribute("result", result);
        model.addAttribute("currentUser", userOpt.orElse(null));
        model.addAttribute("isLoggedIn", userOpt.isPresent());

        return "checkout/result";
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

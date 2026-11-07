package com.studywithme.controller.auth;

import com.studywithme.dto.request.DangKyRequest;
import com.studywithme.dto.request.DangNhapRequest;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.JwtAuthResponse;
import com.studywithme.security.UserPrincipal;
import com.studywithme.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,63}$");

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> dangNhap(@Valid @RequestBody DangNhapRequest request) {
        JwtAuthResponse response = authService.dangNhap(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    @PostMapping("/dang-ky")
    public ResponseEntity<ApiResponse<String>> dangKy(@Valid @RequestBody DangKyRequest request) {
        String thongBao = authService.dangKy(request);
        return ResponseEntity.ok(ApiResponse.success(thongBao, null));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkEmail(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email không được để trống"));
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (normalizedEmail.length() < 5 || normalizedEmail.length() > 50) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email phải có độ dài từ 5 đến 50 ký tự"));
        }
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email không đúng định dạng chuẩn (ví dụ: user@example.com)"));
        }
        boolean exists = authService.kiemTraEmailTonTai(normalizedEmail);
        return ResponseEntity.ok(ApiResponse.success(
                exists ? "Email đã được sử dụng" : "Email khả dụng có thể sử dụng",
                Map.of("exists", exists, "available", !exists, "email", normalizedEmail)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserPrincipal>> layThongTinHienTai(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Chưa đăng nhập"));
        }
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", userPrincipal));
    }
}

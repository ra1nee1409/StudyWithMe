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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserPrincipal>> layThongTinHienTai(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Chưa đăng nhập"));
        }
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", userPrincipal));
    }
}

package com.studywithme.controller.api;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.user.CapNhatHoSoRequest;
import com.studywithme.dto.user.DoiMatKhauRequest;
import com.studywithme.entity.NguoiDung;
import com.studywithme.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileApiController {

    private final ProfileService profileService;

    /**
     * API Lấy thông tin cá nhân hiện tại
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> layThongTinCaNhan(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Vui lòng đăng nhập."));
        }

        NguoiDung user = profileService.layThongTinNguoiDung(principal.getName());
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("email", user.getEmail());
        data.put("hoTen", user.getHoTen());
        data.put("soDienThoai", user.getSoDienThoai());
        data.put("anhDaiDien", user.getAnhDaiDien());
        data.put("trangThai", user.getTrangThai());
        data.put("danhSachVaiTro", user.getDanhSachVaiTro());

        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin tài khoản thành công.", data));
    }

    /**
     * API Cập nhật thông tin cá nhân (Họ tên, SĐT, Avatar, Giới thiệu) - KHÔNG ĐỔI EMAIL
     */
    @PutMapping("/cap-nhat")
    public ResponseEntity<ApiResponse<String>> capNhatThongTin(
            @Valid @RequestBody CapNhatHoSoRequest request,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Vui lòng đăng nhập."));
        }

        try {
            profileService.capNhatThongTin(principal.getName(), request);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin cá nhân thành công."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * API Đổi mật khẩu
     */
    @PostMapping("/doi-mat-khau")
    public ResponseEntity<ApiResponse<String>> doiMatKhau(
            @Valid @RequestBody DoiMatKhauRequest request,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Vui lòng đăng nhập."));
        }

        try {
            profileService.doiMatKhau(principal.getName(), request);
            return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}


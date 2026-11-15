package com.studywithme.controller.api;

import com.studywithme.dto.notification.ThongBaoDto;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.entity.NguoiDung;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ThongBaoApiController {

    private final ThongBaoService thongBaoService;
    private final NguoiDungRepository nguoiDungRepository;

    private NguoiDung getAuthenticatedUser(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Vui lòng đăng nhập để sử dụng tính năng thông báo.");
        }
        return nguoiDungRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản đăng nhập."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThongBaoDto>>> layDanhSachThongBao(
            @RequestParam(defaultValue = "10") int limit,
            Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        List<ThongBaoDto> list = thongBaoService.layDanhSachThongBao(user.getId(), limit);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thông báo thành công", list));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> demSoChuaDoc(Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        long count = thongBaoService.demSoChuaDoc(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Đếm thông báo chưa đọc thành công", Map.of("count", count)));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Boolean>> danhDauDaDoc(
            @PathVariable Long id,
            Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        boolean updated = thongBaoService.danhDauDaDoc(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Đánh dấu đã đọc thành công", updated));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>> danhDauTatCaDaDoc(Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        int updatedCount = thongBaoService.danhDauTatCaDaDoc(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu tất cả thông báo là đã đọc", updatedCount));
    }
}

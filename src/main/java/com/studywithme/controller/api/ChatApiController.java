package com.studywithme.controller.api;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.chat.CuocTroChuyenDto;
import com.studywithme.dto.chat.GuiTinNhanRequest;
import com.studywithme.dto.chat.TinNhanDto;
import com.studywithme.entity.NguoiDung;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.ChatMongoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatMongoService chatMongoService;
    private final NguoiDungRepository nguoiDungRepository;

    private NguoiDung getAuthenticatedUser(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Vui lòng đăng nhập để sử dụng tính năng chat.");
        }
        return nguoiDungRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản đăng nhập."));
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<CuocTroChuyenDto>>> layDanhSachCuocTroChuyen(Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        List<CuocTroChuyenDto> list = chatMongoService.layDanhSachCuocTroChuyen(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách cuộc trò chuyện thành công", list));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<List<TinNhanDto>>> layLichSuTinNhan(@PathVariable String id, Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        List<TinNhanDto> messages = chatMongoService.layLichSuTinNhan(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử tin nhắn thành công", messages));
    }

    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<TinNhanDto>> guiTinNhan(@Valid @RequestBody GuiTinNhanRequest request, Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        TinNhanDto sent = chatMongoService.guiTinNhan(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Gửi tin nhắn thành công", sent));
    }

    @PostMapping("/conversations/{id}/read")
    public ResponseEntity<ApiResponse<Integer>> danhDauDaDoc(@PathVariable String id, Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        int count = chatMongoService.danhDauDaDoc(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu đã đọc", count));
    }

    @PostMapping("/conversations/with-user/{targetUserId}")
    public ResponseEntity<ApiResponse<CuocTroChuyenDto>> layHoacTaoCuocTroChuyen(@PathVariable Long targetUserId, Principal principal) {
        NguoiDung user = getAuthenticatedUser(principal);
        CuocTroChuyenDto conv = chatMongoService.layHoacTaoCuocTroChuyen(user.getId(), targetUserId);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin cuộc trò chuyện thành công", conv));
    }
}

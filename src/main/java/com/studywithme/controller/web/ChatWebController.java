package com.studywithme.controller.web;

import com.studywithme.dto.chat.CuocTroChuyenDto;
import com.studywithme.entity.NguoiDung;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.ChatMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

/**
 * Web Controller quản lý Trung tâm Tin nhắn Trực tuyến /chat (WBS 4.2)
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebController {

    private final ChatMongoService chatMongoService;
    private final NguoiDungRepository nguoiDungRepository;

    @GetMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public String index(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String convId,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        NguoiDung currentUser = nguoiDungRepository.findByEmail(principal.getName()).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<CuocTroChuyenDto> conversations = chatMongoService.layDanhSachCuocTroChuyen(currentUser.getId());

        // Nếu có tham số userId (Ví dụ: click "Nhắn tin với gia sư" từ trang chi tiết gia sư)
        // mà chưa có conversation sẵn, tự động tạo hoặc tìm conversation tương ứng
        String activeConvId = convId;
        Long targetUserId = userId;

        if (userId != null && !userId.equals(currentUser.getId())) {
            try {
                CuocTroChuyenDto conv = chatMongoService.layHoacTaoCuocTroChuyen(currentUser.getId(), userId);
                activeConvId = conv.getId();
                // Refresh lại danh sách hội thoại để đảm bảo mục mới xuất hiện
                conversations = chatMongoService.layDanhSachCuocTroChuyen(currentUser.getId());
            } catch (Exception e) {
                log.warn("Không thể tự động khởi tạo cuộc trò chuyện với user ID {}: {}", userId, e.getMessage());
            }
        } else if (activeConvId == null && !conversations.isEmpty()) {
            // Mặc định chọn cuộc trò chuyện đầu tiên nếu không truyền param
            activeConvId = conversations.get(0).getId();
            targetUserId = conversations.get(0).getDoiPhuongId();
        }

        model.addAttribute("title", "Trung Tâm Tin Nhắn Trực Tuyến - StudyWithMe");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("currentUserName", currentUser.getHoTen());
        model.addAttribute("currentUserAvatar", currentUser.getAnhDaiDien());
        model.addAttribute("conversations", conversations);
        model.addAttribute("activeConvId", activeConvId);
        model.addAttribute("activeTargetUserId", targetUserId);

        return "chat/messenger";
    }
}

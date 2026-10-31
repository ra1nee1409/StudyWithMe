package com.studywithme.controller.api;

import com.studywithme.dto.request.TienDoBaiHocRequest;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.TienDoKhoaHocResponse;
import com.studywithme.entity.NguoiDung;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.security.UserPrincipal;
import com.studywithme.service.StudentLearningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping
@PreAuthorize("isAuthenticated()")
public class StudentApiController {

    private final StudentLearningService studentLearningService;
    private final NguoiDungRepository nguoiDungRepository;

    public StudentApiController(StudentLearningService studentLearningService,
                                NguoiDungRepository nguoiDungRepository) {
        this.studentLearningService = studentLearningService;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    private Long resolveUserId(UserPrincipal userPrincipal, Principal principal) {
        if (userPrincipal != null && userPrincipal.getId() != null) {
            return userPrincipal.getId();
        }
        if (principal != null) {
            return nguoiDungRepository.findByEmail(principal.getName())
                    .map(NguoiDung::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Không xác định được người dùng"));
        }
        throw new IllegalArgumentException("Người dùng chưa được xác thực");
    }

    /**
     * Cập nhật trạng thái hoàn thành bài học (LMS Learning Player)
     */
    @PostMapping({"/api/hoc-vien/tien-do", "/api/student/learning/toggle-complete"})
    public ResponseEntity<ApiResponse<TienDoKhoaHocResponse>> capNhatTienDo(
            @Valid @RequestBody TienDoBaiHocRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Principal principal) {

        Long userId = resolveUserId(userPrincipal, principal);
        TienDoKhoaHocResponse response = studentLearningService.capNhatTienDo(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật tiến độ học tập thành công", response));
    }
}


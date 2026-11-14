package com.studywithme.controller.api.tutor;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.tutor.LichDaySlotDto;
import com.studywithme.dto.tutor.LichDaySlotRequest;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.LichDayGiaSu;
import com.studywithme.entity.NguoiDung;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.LichDayGiaSuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/giang-vien/lich-day", "/api/tutor/lich-day"})
@PreAuthorize("hasAuthority('ROLE_GIANG_VIEN')")
@RequiredArgsConstructor
@Slf4j
public class TutorLichDayApiController {

    private final LichDayGiaSuService lichDayGiaSuService;
    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;

    private HoSoGiangVien getGiangVienFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Vui lòng đăng nhập để thực hiện thao tác này.");
        }
        NguoiDung user = nguoiDungRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        return hoSoGiangVienRepository.findByNguoiDungId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa có hồ sơ giảng viên/gia sư."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LichDaySlotDto>>> layDanhSachLichDay(Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        List<LichDaySlotDto> list = lichDayGiaSuService.layDanhSachLichDay(gv.getId());
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch dạy thành công", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LichDaySlotDto>> taoHoacCapNhatSlot(@Valid @RequestBody LichDaySlotRequest request, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        LichDayGiaSu entity = lichDayGiaSuService.taoHoacCapNhatSlot(gv.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Lưu khung giờ lịch dạy thành công", LichDaySlotDto.fromEntity(entity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> xoaSlot(@PathVariable Long id, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        lichDayGiaSuService.xoaSlot(gv.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Xóa khung giờ thành công", null));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<LichDaySlotDto>> toggleTrangThai(@PathVariable Long id, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        LichDayGiaSu entity = lichDayGiaSuService.toggleTrangThaiSlot(gv.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đổi trạng thái khung giờ thành công", LichDaySlotDto.fromEntity(entity)));
    }
}

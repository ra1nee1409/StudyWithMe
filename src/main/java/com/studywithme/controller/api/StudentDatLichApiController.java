package com.studywithme.controller.api;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.tutor.DatCaHoc1k1Request;
import com.studywithme.dto.tutor.DatLich1k1Dto;
import com.studywithme.dto.tutor.LichDaySlotDto;
import com.studywithme.entity.DatLich1k1;
import com.studywithme.entity.NguoiDung;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.DatLich1k1Service;
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
@RequiredArgsConstructor
@Slf4j
public class StudentDatLichApiController {

    private final DatLich1k1Service datLich1k1Service;
    private final LichDayGiaSuService lichDayGiaSuService;
    private final NguoiDungRepository nguoiDungRepository;

    private NguoiDung getUserFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Vui lòng đăng nhập để thực hiện thao tác này.");
        }
        return nguoiDungRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
    }

    /**
     * Học viên đặt ca học kèm 1-1 với gia sư
     */
    @PostMapping("/api/hoc-vien/dat-lich-1k1")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DatLich1k1Dto>> datCaHoc(
            @Valid @RequestBody DatCaHoc1k1Request request,
            Principal principal) {
        NguoiDung student = getUserFromPrincipal(principal);
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(student.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Gửi yêu cầu đặt ca học kèm 1-1 thành công", DatLich1k1Dto.fromEntity(caHoc)));
    }

    /**
     * Danh sách ca học 1-1 của học viên đang đăng nhập
     */
    @GetMapping("/api/hoc-vien/dat-lich-1k1")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<DatLich1k1Dto>>> layDanhSachCaHocHocVien(Principal principal) {
        NguoiDung student = getUserFromPrincipal(principal);
        List<DatLich1k1Dto> list = datLich1k1Service.layDanhSachCaHocCuaHocVien(student.getId());
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách ca học thành công", list));
    }

    /**
     * Học viên hủy yêu cầu đặt ca học (khi đang chờ xác nhận hoặc chờ thanh toán)
     */
    @PutMapping("/api/hoc-vien/dat-lich-1k1/{id}/huy")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DatLich1k1Dto>> huyCaHoc(
            @PathVariable Long id,
            @RequestParam(required = false) String lyDoHuy,
            Principal principal) {
        NguoiDung student = getUserFromPrincipal(principal);
        DatLich1k1 caHoc = datLich1k1Service.hocVienHuyCaHoc(student.getId(), id, lyDoHuy);
        return ResponseEntity.ok(ApiResponse.success("Hủy ca học thành công", DatLich1k1Dto.fromEntity(caHoc)));
    }

    /**
     * Lấy danh sách khung giờ rảnh khả dụng của gia sư (Public API)
     */
    @GetMapping("/api/public/gia-su/{giangVienId}/lich-day")
    public ResponseEntity<ApiResponse<List<LichDaySlotDto>>> layLichDayKhaDungGiaSu(@PathVariable Long giangVienId) {
        List<LichDaySlotDto> list = lichDayGiaSuService.layDanhSachLichDayKhaDung(giangVienId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch rảnh thành công", list));
    }
}

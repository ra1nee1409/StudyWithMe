package com.studywithme.controller.api.tutor;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.tutor.CapNhatLinkMeetRequest;
import com.studywithme.dto.tutor.DatLich1k1Dto;
import com.studywithme.dto.tutor.XacNhanCaHocRequest;
import com.studywithme.entity.DatLich1k1;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.DatLich1k1Service;
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
@RequestMapping({"/api/giang-vien/ca-hoc", "/api/tutor/ca-hoc"})
@PreAuthorize("hasAuthority('ROLE_GIANG_VIEN')")
@RequiredArgsConstructor
@Slf4j
public class TutorCaHocApiController {

    private final DatLich1k1Service datLich1k1Service;
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
    public ResponseEntity<ApiResponse<List<DatLich1k1Dto>>> layDanhSachCaHoc(
            @RequestParam(required = false) TrangThaiDatLich trangThai,
            Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        List<DatLich1k1Dto> list = datLich1k1Service.layDanhSachCaHocCuaGiaSu(gv.getId(), trangThai);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách ca học 1-1 thành công", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DatLich1k1Dto>> layChiTietCaHoc(@PathVariable Long id) {
        DatLich1k1Dto dto = datLich1k1Service.layChiTietCaHoc(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết ca học thành công", dto));
    }

    @PostMapping("/{id}/phan-hoi")
    public ResponseEntity<ApiResponse<DatLich1k1Dto>> phanHoiCaHoc(
            @PathVariable Long id,
            @RequestBody XacNhanCaHocRequest request,
            Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        DatLich1k1 entity = datLich1k1Service.giaSuPhanHoiCaHoc(gv.getId(), id, request);
        String msg = request.isChapNhan() ? "Đã chấp nhận ca học" : "Đã từ chối ca học";
        return ResponseEntity.ok(ApiResponse.success(msg, DatLich1k1Dto.fromEntity(entity)));
    }

    @PutMapping("/{id}/link-meet")
    public ResponseEntity<ApiResponse<DatLich1k1Dto>> capNhatLinkMeet(
            @PathVariable Long id,
            @Valid @RequestBody CapNhatLinkMeetRequest request,
            Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        DatLich1k1 entity = datLich1k1Service.capNhatLinkMeet(gv.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đường link phòng học thành công", DatLich1k1Dto.fromEntity(entity)));
    }

    @PostMapping("/{id}/hoan-tat")
    public ResponseEntity<ApiResponse<DatLich1k1Dto>> hoanTatCaHoc(
            @PathVariable Long id,
            Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        DatLich1k1 entity = datLich1k1Service.hoanTatCaHoc(gv.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Hoàn tất ca dạy và giải phóng học phí vào ví thành công", DatLich1k1Dto.fromEntity(entity)));
    }

    @PostMapping("/{id}/huy")
    public ResponseEntity<ApiResponse<DatLich1k1Dto>> giaSuHuyCaHoc(
            @PathVariable Long id,
            @RequestParam(required = false) String lyDoHuy,
            Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        DatLich1k1 entity = datLich1k1Service.giaSuHuyCaHoc(gv.getId(), id, lyDoHuy);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy ca học và hoàn tiền cho học viên thành công", DatLich1k1Dto.fromEntity(entity)));
    }
}

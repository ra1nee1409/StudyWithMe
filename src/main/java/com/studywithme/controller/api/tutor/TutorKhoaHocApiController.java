package com.studywithme.controller.api.tutor;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.tutor.CapNhatKhoaHocRequest;
import com.studywithme.dto.tutor.TaoBaiHocRequest;
import com.studywithme.dto.tutor.TaoChuongHocRequest;
import com.studywithme.dto.tutor.TaoKhoaHocRequest;
import com.studywithme.entity.*;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.TutorKhoaHocService;
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
@RequestMapping("/api/giang-vien/khoa-hoc")
@PreAuthorize("hasAnyAuthority('ROLE_GIANG_VIEN', 'ROLE_QUAN_TRI')")
@RequiredArgsConstructor
@Slf4j
public class TutorKhoaHocApiController {

    private final TutorKhoaHocService tutorKhoaHocService;
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

    /**
     * Lấy danh sách khóa học của giảng viên
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<KhoaHoc>>> layDanhSachKhoaHoc(Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        List<KhoaHoc> list = tutorKhoaHocService.layDanhSachKhoaHocCuaGiangVien(gv.getId());
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học thành công", list));
    }

    /**
     * Lấy chi tiết khóa học và toàn bộ đề cương chương/bài học
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhoaHoc>> layChiTietKhoaHoc(@PathVariable Long id, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        KhoaHoc kh = tutorKhoaHocService.layChiTietKhoaHocCuaGiangVien(gv.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết khóa học thành công", kh));
    }

    /**
     * Tạo mới khóa học (Bản nháp)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<KhoaHoc>> taoKhoaHoc(@Valid @RequestBody TaoKhoaHocRequest request, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        KhoaHoc kh = tutorKhoaHocService.taoKhoaHocMoi(gv.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Tạo khóa học mới thành công", kh));
    }

    /**
     * Cập nhật thông tin khóa học
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhoaHoc>> capNhatKhoaHoc(@PathVariable Long id,
                                                               @Valid @RequestBody CapNhatKhoaHocRequest request,
                                                               Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        KhoaHoc kh = tutorKhoaHocService.capNhatKhoaHoc(gv.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật khóa học thành công", kh));
    }

    /**
     * Gửi duyệt khóa học lên Admin
     */
    @PostMapping("/{id}/gui-duyet")
    public ResponseEntity<ApiResponse<KhoaHoc>> guiDuyet(@PathVariable Long id, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        KhoaHoc kh = tutorKhoaHocService.guiKiemDuyet(gv.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Khóa học đã được gửi lên ban quản trị xét duyệt", kh));
    }

    /**
     * Xóa khóa học
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> xoaKhoaHoc(@PathVariable Long id, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        tutorKhoaHocService.xoaKhoaHoc(gv.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Xóa khóa học thành công", null));
    }

    /**
     * Thêm chương học mới vào khóa học
     */
    @PostMapping("/{id}/chuong-hoc")
    public ResponseEntity<ApiResponse<ChuongHoc>> themChuongHoc(@PathVariable Long id,
                                                                @Valid @RequestBody TaoChuongHocRequest request,
                                                                Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        ChuongHoc chuong = tutorKhoaHocService.themChuongHoc(gv.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Thêm chương học thành công", chuong));
    }

    /**
     * Xóa chương học
     */
    @DeleteMapping("/chuong-hoc/{chuongId}")
    public ResponseEntity<ApiResponse<Void>> xoaChuongHoc(@PathVariable Long chuongId, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        tutorKhoaHocService.xoaChuongHoc(gv.getId(), chuongId);
        return ResponseEntity.ok(ApiResponse.success("Xóa chương học thành công", null));
    }

    /**
     * Thêm bài học mới vào chương học
     */
    @PostMapping("/chuong-hoc/{chuongId}/bai-hoc")
    public ResponseEntity<ApiResponse<BaiHoc>> themBaiHoc(@PathVariable Long chuongId,
                                                          @Valid @RequestBody TaoBaiHocRequest request,
                                                          Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        BaiHoc baiHoc = tutorKhoaHocService.themBaiHoc(gv.getId(), chuongId, request);
        return ResponseEntity.ok(ApiResponse.success("Thêm bài học thành công", baiHoc));
    }

    /**
     * Xóa bài học
     */
    @DeleteMapping("/bai-hoc/{baiHocId}")
    public ResponseEntity<ApiResponse<Void>> xoaBaiHoc(@PathVariable Long baiHocId, Principal principal) {
        HoSoGiangVien gv = getGiangVienFromPrincipal(principal);
        tutorKhoaHocService.xoaBaiHoc(gv.getId(), baiHocId);
        return ResponseEntity.ok(ApiResponse.success("Xóa bài học thành công", null));
    }
}


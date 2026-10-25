package com.studywithme.controller.api.admin;

import com.studywithme.dto.request.TuChoiKhoaHocRequest;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.KhoaHocKiemDuyetResponse;
import com.studywithme.dto.response.ThongKeKiemDuyetKhoaHocResponse;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.service.AdminKhoaHocService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/khoa-hoc")
@PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
public class AdminKhoaHocApiController {

    private final AdminKhoaHocService adminKhoaHocService;

    public AdminKhoaHocApiController(AdminKhoaHocService adminKhoaHocService) {
        this.adminKhoaHocService = adminKhoaHocService;
    }

    /**
     * 1. Lấy danh sách khóa học theo trạng thái (CHO_DUYET, DA_XUAT_BAN, TU_CHOI hoặc null để lấy tất cả)
     */
    @GetMapping("/danh-sach")
    public ResponseEntity<ApiResponse<List<KhoaHocKiemDuyetResponse>>> layDanhSachKhoaHoc(
            @RequestParam(required = false) TrangThaiKhoaHoc trangThai) {
        List<KhoaHocKiemDuyetResponse> data = adminKhoaHocService.layDanhSachKhoaHoc(trangThai);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học thành công", data));
    }

    /**
     * 2. Lấy thông tin chi tiết đề cương giáo trình, chương và video bài giảng để admin thẩm định
     */
    @GetMapping("/{id}/chi-tiet")
    public ResponseEntity<ApiResponse<KhoaHocKiemDuyetResponse>> layChiTietKhoaHoc(@PathVariable Long id) {
        KhoaHocKiemDuyetResponse data = adminKhoaHocService.layChiTietKhoaHoc(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết khóa học thành công", data));
    }

    /**
     * 3. Phê duyệt khóa học -> Trạng thái chuyển sang DA_XUAT_BAN
     */
    @PostMapping("/{id}/phe-duyet")
    public ResponseEntity<ApiResponse<KhoaHocKiemDuyetResponse>> pheDuyetKhoaHoc(@PathVariable Long id) {
        KhoaHocKiemDuyetResponse data = adminKhoaHocService.pheDuyetKhoaHoc(id);
        return ResponseEntity.ok(ApiResponse.success("Đã phê duyệt và xuất bản khóa học thành công", data));
    }

    /**
     * 4. Từ chối khóa học -> Trạng thái chuyển sang TU_CHOI kèm lưu lý do
     */
    @PostMapping("/{id}/tu-choi")
    public ResponseEntity<ApiResponse<KhoaHocKiemDuyetResponse>> tuChoiKhoaHoc(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) TuChoiKhoaHocRequest request) {
        String lyDo = request != null ? request.getLyDo() : null;
        KhoaHocKiemDuyetResponse data = adminKhoaHocService.tuChoiKhoaHoc(id, lyDo);
        return ResponseEntity.ok(ApiResponse.success("Đã từ chối khóa học và lưu lý do thành công", data));
    }

    /**
     * 5. Lấy thống kê số lượng khóa học theo từng trạng thái kiểm duyệt
     */
    @GetMapping("/thong-ke")
    public ResponseEntity<ApiResponse<ThongKeKiemDuyetKhoaHocResponse>> layThongKe() {
        ThongKeKiemDuyetKhoaHocResponse data = adminKhoaHocService.layThongKe();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê kiểm duyệt khóa học thành công", data));
    }
}

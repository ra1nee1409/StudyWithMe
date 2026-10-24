package com.studywithme.controller.api.admin;

import com.studywithme.dto.request.DanhMucRequest;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.DanhMucResponse;
import com.studywithme.service.DanhMucService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/danh-muc")
@PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
public class AdminDanhMucApiController {

    private final DanhMucService danhMucService;

    public AdminDanhMucApiController(DanhMucService danhMucService) {
        this.danhMucService = danhMucService;
    }

    /**
     * API: Lấy toàn bộ cây danh mục cha-con
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DanhMucResponse>>> layCayDanhMuc() {
        List<DanhMucResponse> cayDanhMuc = danhMucService.layCayDanhMuc();
        return ResponseEntity.ok(ApiResponse.success("Lấy cây danh mục thành công", cayDanhMuc));
    }

    /**
     * API: Lấy danh sách danh mục phẳng (phục vụ chọn danh mục cha trong dropdown)
     */
    @GetMapping("/phang")
    public ResponseEntity<ApiResponse<List<DanhMucResponse>>> layDanhSachPhang() {
        List<DanhMucResponse> danhSachPhang = danhMucService.layTatCaDanhMucPhang();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách danh mục phẳng thành công", danhSachPhang));
    }

    /**
     * API: Lấy chi tiết 1 danh mục theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DanhMucResponse>> layChiTietDanhMuc(@PathVariable Long id) {
        DanhMucResponse chiTiet = danhMucService.layChiTietDanhMuc(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin danh mục thành công", chiTiet));
    }

    /**
     * API: Thêm danh mục mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DanhMucResponse>> themDanhMuc(@Valid @RequestBody DanhMucRequest request) {
        DanhMucResponse danhMucMoi = danhMucService.themDanhMuc(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo danh mục mới thành công", danhMucMoi));
    }

    /**
     * API: Cập nhật danh mục
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DanhMucResponse>> capNhatDanhMuc(
            @PathVariable Long id,
            @Valid @RequestBody DanhMucRequest request) {
        DanhMucResponse danhMucCapNhat = danhMucService.capNhatDanhMuc(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công", danhMucCapNhat));
    }

    /**
     * API: Bật/Tắt trạng thái kích hoạt danh mục
     */
    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<ApiResponse<DanhMucResponse>> doiTrangThaiKichHoat(@PathVariable Long id) {
        DanhMucResponse res = danhMucService.doiTrangThaiKichHoat(id);
        String msg = Boolean.TRUE.equals(res.getKichHoat()) ? "Đã kích hoạt hiển thị danh mục" : "Đã tạm ẩn danh mục";
        return ResponseEntity.ok(ApiResponse.success(msg, res));
    }

    /**
     * API: Xóa danh mục (kiểm tra ràng buộc con & khóa học)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> xoaDanhMuc(@PathVariable Long id) {
        danhMucService.xoaDanhMuc(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa danh mục thành công", null));
    }

    /**
     * API: Thống kê nhanh danh mục
     */
    @GetMapping("/thong-ke")
    public ResponseEntity<ApiResponse<Map<String, Object>>> layThongKe() {
        Map<String, Object> stats = danhMucService.layThongKeDanhMuc();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê danh mục thành công", stats));
    }
}

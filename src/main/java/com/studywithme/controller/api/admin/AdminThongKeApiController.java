package com.studywithme.controller.api.admin;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.DoanhThuThangResponse;
import com.studywithme.dto.response.ThongKeTongQuanResponse;
import com.studywithme.dto.response.TyLeDanhMucResponse;
import com.studywithme.service.AdminThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/thong-ke")
@PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
@RequiredArgsConstructor
public class AdminThongKeApiController {

    private final AdminThongKeService adminThongKeService;

    /**
     * API 1: Lấy số liệu 4 thẻ KPI và bảng phụ trợ trên Dashboard
     */
    @GetMapping("/tong-quan")
    public ResponseEntity<ApiResponse<ThongKeTongQuanResponse>> layThongKeTongQuan() {
        ThongKeTongQuanResponse response = adminThongKeService.layThongKeTongQuan();
        return ResponseEntity.ok(ApiResponse.success("Lấy số liệu tổng quan thành công", response));
    }

    /**
     * API 2: Lấy mảng doanh thu 12 tháng theo năm (phục vụ Line Chart)
     */
    @GetMapping("/doanh-thu")
    public ResponseEntity<ApiResponse<DoanhThuThangResponse>> layDoanhThuTheoNam(
            @RequestParam(value = "nam", required = false) Integer nam) {
        DoanhThuThangResponse response = adminThongKeService.layDoanhThuTheoNam(nam);
        return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu doanh thu thành công", response));
    }

    /**
     * API 3: Lấy tỷ lệ học viên theo từng nhóm môn học / cấp học (phục vụ Pie Chart)
     */
    @GetMapping("/ty-le-danh-muc")
    public ResponseEntity<ApiResponse<TyLeDanhMucResponse>> layTyLeDanhMuc() {
        TyLeDanhMucResponse response = adminThongKeService.layTyLeTheoDanhMuc();
        return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu tỷ lệ nhóm danh mục thành công", response));
    }
}

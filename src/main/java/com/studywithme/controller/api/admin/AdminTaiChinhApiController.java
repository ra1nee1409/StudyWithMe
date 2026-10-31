package com.studywithme.controller.api.admin;

import com.studywithme.dto.request.TuChoiRutTienRequest;
import com.studywithme.dto.request.XacNhanChuyenTienRequest;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.ThongKeRutTienResponse;
import com.studywithme.dto.response.YeuCauRutTienResponse;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import com.studywithme.service.AdminTaiChinhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tai-chinh")
@PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
public class AdminTaiChinhApiController {

    private final AdminTaiChinhService adminTaiChinhService;

    public AdminTaiChinhApiController(AdminTaiChinhService adminTaiChinhService) {
        this.adminTaiChinhService = adminTaiChinhService;
    }

    /**
     * 1. Lấy danh sách các lệnh rút tiền (lọc theo CHO_XU_LY, DA_CHUYEN_TIEN, TU_CHOI hoặc null để lấy tất cả)
     */
    @GetMapping("/yeu-cau-rut-tien")
    public ResponseEntity<ApiResponse<List<YeuCauRutTienResponse>>> layDanhSachYeuCau(
            @RequestParam(required = false) TrangThaiRutTien trangThai) {
        List<YeuCauRutTienResponse> data = adminTaiChinhService.layDanhSachYeuCauRutTien(trangThai);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lệnh rút tiền thành công", data));
    }

    /**
     * 2. Lấy số liệu thống kê tài chính & rút tiền (KPIs)
     */
    @GetMapping("/thong-ke")
    public ResponseEntity<ApiResponse<ThongKeRutTienResponse>> layThongKe() {
        ThongKeRutTienResponse stats = adminTaiChinhService.layThongKeRutTien();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê rút tiền thành công", stats));
    }

    /**
     * 3. Lấy chi tiết lệnh rút tiền
     */
    @GetMapping("/yeu-cau-rut-tien/{id}")
    public ResponseEntity<ApiResponse<YeuCauRutTienResponse>> layChiTiet(@PathVariable Long id) {
        YeuCauRutTienResponse data = adminTaiChinhService.layChiTietYeuCau(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết lệnh rút tiền thành công", data));
    }

    /**
     * 4. Phê duyệt & xác nhận đã chuyển khoản thành công
     */
    @PostMapping("/yeu-cau-rut-tien/{id}/xac-nhan")
    public ResponseEntity<ApiResponse<YeuCauRutTienResponse>> xacNhanChuyenTien(
            @PathVariable Long id,
            @RequestBody(required = false) XacNhanChuyenTienRequest request) {
        String ghiChu = (request != null) ? request.getGhiChu() : null;
        if (request != null && request.getMaGiaoDichNganHang() != null && !request.getMaGiaoDichNganHang().isBlank()) {
            ghiChu = "Mã GD: " + request.getMaGiaoDichNganHang() + (ghiChu != null ? " - " + ghiChu : "");
        }
        YeuCauRutTienResponse data = adminTaiChinhService.xacNhanChuyenTien(id, ghiChu);
        return ResponseEntity.ok(ApiResponse.success("Đã xác nhận giải ngân chuyển tiền thành công", data));
    }

    /**
     * 5. Từ chối lệnh rút tiền và hoàn tiền lại vào ví giảng viên
     */
    @PostMapping("/yeu-cau-rut-tien/{id}/tu-choi")
    public ResponseEntity<ApiResponse<YeuCauRutTienResponse>> tuChoiRutTien(
            @PathVariable Long id,
            @Valid @RequestBody TuChoiRutTienRequest request) {
        YeuCauRutTienResponse data = adminTaiChinhService.tuChoiRutTien(id, request.getLyDoTuChoi());
        return ResponseEntity.ok(ApiResponse.success("Đã từ chối lệnh rút tiền và hoàn tiền về ví giảng viên thành công", data));
    }

    /**
     * 6. Lấy danh sách dòng tiền giao dịch toàn hệ thống (Tiền vào, Tiền ra)
     */
    @GetMapping("/dong-tien")
    public ResponseEntity<ApiResponse<List<com.studywithme.dto.response.DongTienGiaoDichDto>>> layDanhSachDongTien(
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) String phanLoai,
            @RequestParam(required = false) String keyword) {
        var data = adminTaiChinhService.layDanhSachDongTien(loai, phanLoai, keyword);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách dòng tiền hệ thống thành công", data));
    }

    /**
     * 7. Lấy số liệu thống kê dòng tiền tổng thể (Tổng vào, Tổng ra, Phí sàn 20%, Số dư ví GV)
     */
    @GetMapping("/dong-tien/thong-ke")
    public ResponseEntity<ApiResponse<com.studywithme.dto.response.ThongKeDongTienResponse>> layThongKeDongTien() {
        var stats = adminTaiChinhService.layThongKeDongTien();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê dòng tiền thành công", stats));
    }
}

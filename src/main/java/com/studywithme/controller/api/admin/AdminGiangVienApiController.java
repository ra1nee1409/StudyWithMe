package com.studywithme.controller.api.admin;

import com.studywithme.dto.request.TuChoiKycRequest;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.HoSoGiangVienKycResponse;
import com.studywithme.dto.response.ThongKeKycResponse;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.service.GiangVienKycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/giang-vien")
@PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
public class AdminGiangVienApiController {

    private final GiangVienKycService giangVienKycService;

    public AdminGiangVienApiController(GiangVienKycService giangVienKycService) {
        this.giangVienKycService = giangVienKycService;
    }

    /**
     * 1. Lấy danh sách hồ sơ gia sư theo trạng thái lọc (CHO_DUYET, DA_DUYET, TU_CHOI hoặc null để lấy tất cả)
     */
    @GetMapping("/yeu-cau-duyet")
    public ResponseEntity<ApiResponse<List<HoSoGiangVienKycResponse>>> layDanhSachYeuCauDuyet(
            @RequestParam(required = false) TrangThaiGiangVien trangThai) {
        List<HoSoGiangVienKycResponse> data = giangVienKycService.layDanhSachHoSo(trangThai);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách hồ sơ giảng viên thành công", data));
    }

    /**
     * 2. Lấy thông tin chi tiết hồ sơ gia sư (kèm bằng cấp, chứng chỉ và video dạy thử)
     */
    @GetMapping("/{id}/chi-tiet")
    public ResponseEntity<ApiResponse<HoSoGiangVienKycResponse>> layChiTietHoSo(@PathVariable Long id) {
        HoSoGiangVienKycResponse data = giangVienKycService.layChiTietHoSo(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết hồ sơ thành công", data));
    }

    /**
     * 3. Phê duyệt hồ sơ gia sư (Nâng quyền người dùng thành ROLE_GIANG_VIEN và xác thực chứng chỉ)
     */
    @PostMapping("/{id}/phe-duyet")
    public ResponseEntity<ApiResponse<HoSoGiangVienKycResponse>> pheDuyetHoSo(@PathVariable Long id) {
        HoSoGiangVienKycResponse data = giangVienKycService.pheDuyetHoSo(id);
        return ResponseEntity.ok(ApiResponse.success("Đã phê duyệt hồ sơ gia sư và cấp quyền ROLE_GIANG_VIEN thành công", data));
    }

    /**
     * 4. Từ chối hồ sơ gia sư kèm lưu lý do từ chối
     */
    @PostMapping("/{id}/tu-choi")
    public ResponseEntity<ApiResponse<HoSoGiangVienKycResponse>> tuChoiHoSo(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) TuChoiKycRequest request) {
        String lyDo = request != null ? request.getLyDo() : null;
        HoSoGiangVienKycResponse data = giangVienKycService.tuChoiHoSo(id, lyDo);
        return ResponseEntity.ok(ApiResponse.success("Đã từ chối hồ sơ gia sư", data));
    }

    /**
     * 5. Lấy số liệu thống kê nhanh hồ sơ KYC
     */
    @GetMapping("/thong-ke")
    public ResponseEntity<ApiResponse<ThongKeKycResponse>> layThongKeKyc() {
        ThongKeKycResponse data = giangVienKycService.layThongKeKyc();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê hồ sơ gia sư thành công", data));
    }
}

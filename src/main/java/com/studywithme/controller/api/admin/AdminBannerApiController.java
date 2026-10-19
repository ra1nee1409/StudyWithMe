package com.studywithme.controller.api.admin;

import com.studywithme.dto.request.BannerRequest;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.entity.BannerQuangCao;
import com.studywithme.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banners")
@PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
@RequiredArgsConstructor
public class AdminBannerApiController {

    private final BannerService bannerService;

    /**
     * API: Danh sách toàn bộ banner (kể cả đang ẩn)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerQuangCao>>> layDanhSachBanner() {
        List<BannerQuangCao> danhSach = bannerService.layTatCaBanner();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách banner thành công", danhSach));
    }

    /**
     * API: Lấy chi tiết 1 banner theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerQuangCao>> layChiTietBanner(@PathVariable Long id) {
        BannerQuangCao banner = bannerService.layBannerTheoId(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết banner thành công", banner));
    }

    /**
     * API: Thêm mới banner (hỗ trợ cả JSON body hoặc Multipart form upload ảnh)
     */
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<BannerQuangCao>> themBannerJson(@RequestBody BannerRequest request) {
        BannerQuangCao banner = bannerService.themBanner(request, null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm banner mới thành công", banner));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<BannerQuangCao>> themBannerMultipart(
            @ModelAttribute BannerRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        BannerQuangCao banner = bannerService.themBanner(request, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm banner mới thành công", banner));
    }

    /**
     * API: Cập nhật thông tin banner
     */
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<BannerQuangCao>> capNhatBannerJson(
            @PathVariable Long id,
            @RequestBody BannerRequest request) {
        BannerQuangCao banner = bannerService.capNhatBanner(id, request, null);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin banner thành công", banner));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<BannerQuangCao>> capNhatBannerMultipart(
            @PathVariable Long id,
            @ModelAttribute BannerRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        BannerQuangCao banner = bannerService.capNhatBanner(id, request, file);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin banner thành công", banner));
    }

    /**
     * API: Bật/Tắt trạng thái kích hoạt hiển thị của banner
     */
    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<ApiResponse<BannerQuangCao>> doiTrangThaiKichHoat(@PathVariable Long id) {
        BannerQuangCao banner = bannerService.doiTrangThaiKichHoat(id);
        String msg = Boolean.TRUE.equals(banner.getKichHoat()) ? "Đã bật hiển thị banner" : "Đã tắt hiển thị banner";
        return ResponseEntity.ok(ApiResponse.success(msg, banner));
    }

    /**
     * API: Xóa banner
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> xoaBanner(@PathVariable Long id) {
        bannerService.xoaBanner(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa banner thành công", null));
    }
}

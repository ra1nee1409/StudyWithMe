package com.studywithme.controller.api;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.DanhMucResponse;
import com.studywithme.dto.response.TopKhoaHocDto;
import com.studywithme.entity.BannerQuangCao;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.service.BannerService;
import com.studywithme.service.DanhMucService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicApiController {

    private final BannerService bannerService;
    private final KhoaHocRepository khoaHocRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final DanhMucService danhMucService;

    public PublicApiController(BannerService bannerService,
                               KhoaHocRepository khoaHocRepository,
                               HoSoGiangVienRepository hoSoGiangVienRepository,
                               DanhMucService danhMucService) {
        this.bannerService = bannerService;
        this.khoaHocRepository = khoaHocRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.danhMucService = danhMucService;
    }

    /**
     * 1. Lấy danh sách banner quảng cáo đang kích hoạt
     */
    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<List<BannerQuangCao>>> layDanhSachBanner() {
        List<BannerQuangCao> banners = bannerService.layBannerDangKichHoat();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách banner thành công", banners));
    }

    /**
     * 2. Lấy top 8 khóa học bán chạy / nổi bật nhất
     */
    @GetMapping("/khoa-hoc/noi-bat")
    public ResponseEntity<ApiResponse<List<TopKhoaHocDto>>> layKhoaHocNoiBat() {
        List<KhoaHoc> courses = khoaHocRepository.findTop8ByTrangThaiOrderByTongSoHocVienDesc(TrangThaiKhoaHoc.DA_XUAT_BAN);
        
        List<TopKhoaHocDto> dtos = courses.stream().map(k -> TopKhoaHocDto.builder()
                .id(k.getId())
                .tieuDe(k.getTieuDe())
                .duongDanSlug(k.getDuongDanSlug())
                .anhDaiDien(k.getAnhDaiDienKhoaHoc())
                .tenGiangVien(k.getGiangVien() != null && k.getGiangVien().getNguoiDung() != null ? k.getGiangVien().getNguoiDung().getHoTen() : "Gia sư StudyWithMe")
                .giaGoc(k.getGiaGoc())
                .giaHienTai(k.getGiaKhuyenMai() != null ? k.getGiaKhuyenMai() : k.getGiaGoc())
                .soHocVien(k.getTongSoHocVien())
                .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học nổi bật thành công", dtos));
    }

    /**
     * 3. Lấy danh sách gia sư tiêu biểu
     */
    @GetMapping("/giang-vien/tieu-bieu")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> layGiangVienTieuBieu() {
        List<HoSoGiangVien> tutors = hoSoGiangVienRepository.findTop4ByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.DA_DUYET);

        List<Map<String, Object>> result = tutors.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("hoTen", t.getNguoiDung() != null ? t.getNguoiDung().getHoTen() : "Gia sư");
            map.put("anhDaiDien", t.getNguoiDung() != null ? t.getNguoiDung().getAnhDaiDien() : null);
            map.put("tieuDeChuyenMon", t.getTieuDeChuyenMon());
            map.put("gioiThieuBanThan", t.getGioiThieuBanThan());
            map.put("rating", 5.0);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách gia sư tiêu biểu thành công", result));
    }

    /**
     * 4. Cây danh mục nổi bật
     */
    @GetMapping("/danh-muc/noi-bat")
    public ResponseEntity<ApiResponse<List<DanhMucResponse>>> layDanhMucNoiBat() {
        List<DanhMucResponse> categories = danhMucService.layCayDanhMuc();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh mục thành công", categories));
    }
}

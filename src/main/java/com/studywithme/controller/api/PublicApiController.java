package com.studywithme.controller.api;

import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.DanhMucResponse;
import com.studywithme.dto.response.TopKhoaHocDto;
import com.studywithme.entity.BannerQuangCao;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.LopHocTrucTuyen;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.service.BannerService;
import com.studywithme.service.DanhMucService;
import com.studywithme.service.GiaSuService;
import com.studywithme.service.KhoaHocService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller cung cấp các API công khai (Public) không yêu cầu xác thực JWT.
 */
@RestController
@RequestMapping("/api/public")
public class PublicApiController {

    private final BannerService bannerService;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final DanhMucService danhMucService;
    private final KhoaHocService khoaHocService;
    private final GiaSuService giaSuService;

    public PublicApiController(BannerService bannerService,
                               HoSoGiangVienRepository hoSoGiangVienRepository,
                               DanhMucService danhMucService,
                               KhoaHocService khoaHocService,
                               GiaSuService giaSuService) {
        this.bannerService = bannerService;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.danhMucService = danhMucService;
        this.khoaHocService = khoaHocService;
        this.giaSuService = giaSuService;
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
    public ResponseEntity<ApiResponse<List<TopKhoaHocDto>>> layTopKhoaHoc() {
        List<TopKhoaHocDto> dtos = khoaHocService.layTop8KhoaHocNoiBat();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học nổi bật thành công", dtos));
    }

    /**
     * 3. API Tìm kiếm & Lọc khóa học đa tiêu chí phân trang
     */
    @GetMapping("/khoa-hoc")
    public ResponseEntity<ApiResponse<Page<KhoaHoc>>> locKhoaHoc(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long danhMucId,
            @RequestParam(required = false) String thePhanLoai,
            @RequestParam(required = false) BigDecimal giaMin,
            @RequestParam(required = false) BigDecimal giaMax,
            @RequestParam(required = false) BigDecimal soSaoMin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "moi-nhat") String sort) {

        Sort sortObj = Sort.by(Sort.Direction.DESC, "ngayTao");
        if ("gia-tang".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.ASC, "giaGoc");
        } else if ("gia-giam".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "giaGoc");
        } else if ("hoc-vien-nhieu".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "tongSoHocVien");
        } else if ("danh-gia-cao".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "diemDanhGiaTrungBinh");
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), size, sortObj);
        Page<KhoaHoc> ketQua = khoaHocService.locKhoaHoc(keyword, danhMucId, thePhanLoai, giaMin, giaMax, soSaoMin, pageable);
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm khóa học thành công", ketQua));
    }

    /**
     * 4. Lấy danh sách gia sư tiêu biểu
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
        }).toList();

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách gia sư tiêu biểu thành công", result));
    }

    /**
     * 5. Cây danh mục nổi bật
     */
    @GetMapping("/danh-muc/noi-bat")
    public ResponseEntity<ApiResponse<List<DanhMucResponse>>> layDanhMucNoiBat() {
        List<DanhMucResponse> categories = danhMucService.layCayDanhMuc();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh mục thành công", categories));
    }

    /**
     * 6. API Tìm kiếm & Lọc danh sách gia sư phân trang
     */
    @GetMapping("/giang-vien")
    public ResponseEntity<ApiResponse<Page<HoSoGiangVien>>> locGiaSu(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String loaiChungChi,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "moi-nhat") String sort) {

        Sort sortObj = Sort.by(Sort.Direction.DESC, "ngayTao");
        Pageable pageable = PageRequest.of(Math.max(0, page), size, sortObj);
        Page<HoSoGiangVien> ketQua = giaSuService.locGiaSu(keyword, loaiChungChi, pageable);
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm gia sư thành công", ketQua));
    }

    /**
     * 7. API Xem chi tiết hồ sơ gia sư
     */
    @GetMapping("/giang-vien/{id}")
    public ResponseEntity<ApiResponse<HoSoGiangVien>> layChiTietGiaSu(@PathVariable Long id) {
        HoSoGiangVien hoSo = giaSuService.layChiTietGiaSu(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin gia sư thành công", hoSo));
    }

    /**
     * 8. API Lấy các lớp học trực tuyến đang mở của gia sư
     */
    @GetMapping("/giang-vien/{id}/lop-hoc")
    public ResponseEntity<ApiResponse<List<LopHocTrucTuyen>>> layLopHocCuaGiaSu(@PathVariable Long id) {
        List<LopHocTrucTuyen> lopHoc = giaSuService.layLopHocCuaGiaSu(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lớp học thành công", lopHoc));
    }

    /**
     * 9. API Lấy các khóa học video của gia sư
     */
    @GetMapping("/giang-vien/{id}/khoa-hoc")
    public ResponseEntity<ApiResponse<List<KhoaHoc>>> layKhoaHocCuaGiaSu(@PathVariable Long id) {
        List<KhoaHoc> khoaHoc = giaSuService.layKhoaHocCuaGiaSu(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học thành công", khoaHoc));
    }
}

package com.studywithme.controller.api;

import com.studywithme.dto.request.TienDoBaiHocRequest;
import com.studywithme.dto.response.ApiResponse;
import com.studywithme.dto.response.PhongHocDto;
import com.studywithme.dto.response.TienDoKhoaHocResponse;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.GhiDanhKhoaHoc;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.NguoiDung;
import com.studywithme.repository.DonHangRepository;
import com.studywithme.repository.GhiDanhKhoaHocRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.security.UserPrincipal;
import com.studywithme.service.StudentLearningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@PreAuthorize("isAuthenticated()")
public class StudentApiController {

    private final StudentLearningService studentLearningService;
    private final NguoiDungRepository nguoiDungRepository;
    private final GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;
    private final DonHangRepository donHangRepository;

    public StudentApiController(StudentLearningService studentLearningService,
                                NguoiDungRepository nguoiDungRepository,
                                GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository,
                                DonHangRepository donHangRepository) {
        this.studentLearningService = studentLearningService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.ghiDanhKhoaHocRepository = ghiDanhKhoaHocRepository;
        this.donHangRepository = donHangRepository;
    }

    private Long resolveUserId(UserPrincipal userPrincipal, Principal principal) {
        if (userPrincipal != null && userPrincipal.getId() != null) {
            return userPrincipal.getId();
        }
        if (principal != null) {
            return nguoiDungRepository.findByEmail(principal.getName())
                    .map(NguoiDung::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Không xác định được người dùng"));
        }
        throw new IllegalArgumentException("Người dùng chưa được xác thực");
    }

    /**
     * Danh sách khóa học đã ghi danh của học viên
     */
    @GetMapping("/api/hoc-vien/khoa-hoc")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> layKhoaHocCuaToi(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Principal principal) {
        Long userId = resolveUserId(userPrincipal, principal);
        List<GhiDanhKhoaHoc> list = ghiDanhKhoaHocRepository.findByNguoiDungIdOrderByNgayGhiDanhDesc(userId);
        List<Map<String, Object>> data = list.stream().map(gd -> {
            KhoaHoc kh = gd.getKhoaHoc();
            Map<String, Object> item = new HashMap<>();
            item.put("id", gd.getId());
            item.put("khoaHocId", kh != null ? kh.getId() : null);
            item.put("tieuDe", kh != null ? kh.getTieuDe() : null);
            item.put("duongDanSlug", kh != null ? kh.getDuongDanSlug() : null);
            item.put("anhDaiDien", kh != null ? kh.getAnhDaiDienKhoaHoc() : null);
            item.put("tenDanhMuc", kh != null && kh.getDanhMuc() != null ? kh.getDanhMuc().getTenDanhMuc() : null);
            item.put("tenGiangVien", kh != null && kh.getGiangVien() != null && kh.getGiangVien().getNguoiDung() != null
                    ? kh.getGiangVien().getNguoiDung().getHoTen() : null);
            item.put("phanTramTienDo", gd.getPhanTramTienDo());
            item.put("ngayGhiDanh", gd.getNgayGhiDanh());
            return item;
        }).toList();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khóa học đã ghi danh thành công", data));
    }

    /**
     * Lấy dữ liệu phòng học video LMS (chương, bài học, video embed, tiến độ)
     */
    @GetMapping("/api/hoc-vien/hoc-tap/{slug}")
    public ResponseEntity<ApiResponse<PhongHocDto>> layThongTinPhongHoc(
            @PathVariable String slug,
            @RequestParam(required = false) Long lessonId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Principal principal) {
        Long userId = resolveUserId(userPrincipal, principal);
        PhongHocDto phongHoc = studentLearningService.layThongTinPhongHoc(slug, userId, lessonId);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin phòng học thành công", phongHoc));
    }

    /**
     * Lịch sử đơn hàng của học viên
     */
    @GetMapping("/api/hoc-vien/don-hang")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> layLichSuDonHang(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Principal principal) {
        Long userId = resolveUserId(userPrincipal, principal);
        List<DonHang> orders = donHangRepository.findByNguoiDungIdOrderByNgayTaoDonDesc(userId);
        List<Map<String, Object>> data = orders.stream().map(dh -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", dh.getId());
            item.put("maDonHang", dh.getMaDonHang());
            item.put("tongTienThanhToan", dh.getTongTienThanhToan());
            item.put("phuongThucThanhToan", dh.getPhuongThucThanhToan());
            item.put("trangThaiThanhToan", dh.getTrangThaiThanhToan());
            item.put("ngayTaoDon", dh.getNgayTaoDon());
            item.put("ngayThanhToan", dh.getNgayThanhToan());
            item.put("orderCodePayos", dh.getOrderCodePayos());
            item.put("chiTiet", dh.getDanhSachChiTiet().stream().map(ct -> Map.of(
                    "id", ct.getId(),
                    "loaiSanPham", ct.getLoaiSanPham(),
                    "sanPhamId", ct.getSanPhamId(),
                    "tenSanPham", ct.getTenSanPham(),
                    "donGia", ct.getDonGia()
            )).toList());
            return item;
        }).toList();
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử đơn hàng thành công", data));
    }

    /**
     * Cập nhật trạng thái hoàn thành bài học (LMS Learning Player)
     */
    @PostMapping({"/api/hoc-vien/tien-do", "/api/student/learning/toggle-complete"})
    public ResponseEntity<ApiResponse<TienDoKhoaHocResponse>> capNhatTienDo(
            @Valid @RequestBody TienDoBaiHocRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Principal principal) {

        Long userId = resolveUserId(userPrincipal, principal);
        TienDoKhoaHocResponse response = studentLearningService.capNhatTienDo(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật tiến độ học tập thành công", response));
    }
}


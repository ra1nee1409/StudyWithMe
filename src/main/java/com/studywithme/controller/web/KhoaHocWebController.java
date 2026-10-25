package com.studywithme.controller.web;

import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.repository.DanhMucRepository;
import com.studywithme.service.KhoaHocService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/khoa-hoc")
public class KhoaHocWebController {

    private final KhoaHocService khoaHocService;
    private final DanhMucRepository danhMucRepository;

    public KhoaHocWebController(KhoaHocService khoaHocService, DanhMucRepository danhMucRepository) {
        this.khoaHocService = khoaHocService;
        this.danhMucRepository = danhMucRepository;
    }

    /**
     * Trang H2: Khám phá & Danh mục Khóa học (Course Catalog)
     */
    @GetMapping({"", "/"})
    public String danhSachKhoaHoc(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "danhMucId", required = false) Long danhMucId,
            @RequestParam(value = "thePhanLoai", required = false) String thePhanLoai,
            @RequestParam(value = "khoangGia", required = false) String khoangGia,
            @RequestParam(value = "soSao", required = false) BigDecimal soSao,
            @RequestParam(value = "sort", defaultValue = "moi-nhat") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        // 1. Xác định giá min/max theo khoảng giá
        BigDecimal giaMin = null;
        BigDecimal giaMax = null;

        if ("mien-phi".equals(khoangGia)) {
            giaMin = BigDecimal.ZERO;
            giaMax = BigDecimal.ZERO;
        } else if ("duoi-500k".equals(khoangGia)) {
            giaMin = BigDecimal.ZERO;
            giaMax = BigDecimal.valueOf(500000);
        } else if ("500k-1tr".equals(khoangGia)) {
            giaMin = BigDecimal.valueOf(500000);
            giaMax = BigDecimal.valueOf(1000000);
        } else if ("tren-1tr".equals(khoangGia)) {
            giaMin = BigDecimal.valueOf(1000000);
        }

        // 2. Cấu hình sắp xếp
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

        // 3. Phân trang 12 khóa học / trang
        int pageSize = 12;
        Pageable pageable = PageRequest.of(Math.max(0, page), pageSize, sortObj);

        // 4. Lấy dữ liệu khóa học
        Page<KhoaHoc> coursesPage = khoaHocService.locKhoaHoc(
                keyword, 
                danhMucId, 
                thePhanLoai, 
                giaMin, 
                giaMax, 
                soSao, 
                pageable
        );

        // 5. Lấy danh sách danh mục phân cấp cho Sidebar
        List<DanhMuc> rootCategories = danhMucRepository.findByDanhMucChaIsNullOrderByThuTuHienThiAsc();

        // 6. Gửi dữ liệu vào Model
        model.addAttribute("title", "Khám Phá Khóa Học - StudyWithMe");
        model.addAttribute("coursesPage", coursesPage);
        model.addAttribute("courses", coursesPage.getContent());
        model.addAttribute("rootCategories", rootCategories);
        model.addAttribute("totalCourses", coursesPage.getTotalElements());
        model.addAttribute("totalPages", coursesPage.getTotalPages());
        model.addAttribute("currentPage", page);

        // Giữ lại trạng thái bộ lọc trên UI
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("danhMucId", danhMucId);
        model.addAttribute("thePhanLoai", thePhanLoai);
        model.addAttribute("khoangGia", khoangGia);
        model.addAttribute("soSao", soSao);
        model.addAttribute("currentSort", sort);

        return "courses/index";
    }

    /**
     * Trang H3: Chi tiết Khóa học & Đề cương bài học (Course Detail)
     */
    @GetMapping("/{slug}")
    public String chiTietKhoaHoc(@PathVariable("slug") String slug, Model model) {
        KhoaHoc course = khoaHocService.layChiTietKhoaHocTheoSlug(slug);
        List<KhoaHoc> relatedCourses = khoaHocService.layKhoaHocLienQuan(
                course.getDanhMuc() != null ? course.getDanhMuc().getId() : null, 
                course.getId()
        );

        model.addAttribute("title", course.getTieuDe() + " - StudyWithMe");
        model.addAttribute("course", course);
        model.addAttribute("relatedCourses", relatedCourses);
        return "courses/detail";
    }
}

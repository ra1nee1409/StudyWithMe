package com.studywithme.controller.web;

import com.studywithme.entity.BannerQuangCao;
import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.repository.DanhMucRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.service.BannerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeWebController {

    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final DanhMucRepository danhMucRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final BannerService bannerService;

    public HomeWebController(HoSoGiangVienRepository hoSoGiangVienRepository,
                             DanhMucRepository danhMucRepository,
                             KhoaHocRepository khoaHocRepository,
                             BannerService bannerService) {
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.danhMucRepository = danhMucRepository;
        this.khoaHocRepository = khoaHocRepository;
        this.bannerService = bannerService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "StudyWithMe - Nền Tảng Gia Sư & Học Trực Tuyến Hàng Đầu");
        
        // 1. Banner Slider từ CMS Database
        List<BannerQuangCao> banners = bannerService.layBannerDangKichHoat();
        model.addAttribute("banners", banners);

        // 2. Danh sách Khóa học bán chạy / nổi bật nhất
        List<KhoaHoc> featuredCourses = khoaHocRepository.findTop8ByTrangThaiOrderByTongSoHocVienDesc(TrangThaiKhoaHoc.DA_XUAT_BAN);
        model.addAttribute("featuredCourses", featuredCourses);

        // 3. Gia sư tiêu biểu
        List<HoSoGiangVien> tutors = hoSoGiangVienRepository.findTop4ByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.DA_DUYET);
        model.addAttribute("featuredTutors", tutors);

        // 4. Cây Danh mục cấp học
        List<DanhMuc> categories = danhMucRepository.findByDanhMucChaIsNullOrderByThuTuHienThiAsc();
        model.addAttribute("categories", categories);

        return "index";
    }

    @GetMapping({"/login", "/dang-nhap"})
    public String login(Model model) {
        model.addAttribute("title", "Đăng nhập tài khoản - StudyWithMe");
        return "login";
    }

    @GetMapping({"/register", "/dang-ky"})
    public String register(Model model) {
        model.addAttribute("title", "Đăng ký tài khoản - StudyWithMe");
        return "register";
    }

    @GetMapping("/auth")
    public String auth() {
        return "redirect:/login";
    }
}

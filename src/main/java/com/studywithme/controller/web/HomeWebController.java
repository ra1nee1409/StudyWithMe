package com.studywithme.controller.web;

import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.enums.TrangThaiGiangVien;
import com.studywithme.repository.DanhMucRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeWebController {

    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final DanhMucRepository danhMucRepository;

    public HomeWebController(HoSoGiangVienRepository hoSoGiangVienRepository,
                             DanhMucRepository danhMucRepository) {
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.danhMucRepository = danhMucRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "StudyWithMe - Nền Tảng Gia Sư & Học Trực Tuyến Hàng Đầu");
        
        List<HoSoGiangVien> tutors = hoSoGiangVienRepository.findTop4ByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.DA_DUYET);
        model.addAttribute("featuredTutors", tutors);

        List<DanhMuc> categories = danhMucRepository.findByDanhMucChaIsNullOrderByThuTuHienThiAsc();
        model.addAttribute("categories", categories);

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Đăng nhập tài khoản - StudyWithMe");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Đăng ký tài khoản - StudyWithMe");
        return "register";
    }

    @GetMapping("/auth")
    public String auth() {
        return "redirect:/login";
    }
}

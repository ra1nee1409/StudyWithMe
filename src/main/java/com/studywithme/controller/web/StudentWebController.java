package com.studywithme.controller.web;

import com.studywithme.entity.DangKyLopHoc;
import com.studywithme.entity.DanhSachYeuThich;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.GhiDanhKhoaHoc;
import com.studywithme.entity.NguoiDung;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.DanhSachYeuThichRepository;
import com.studywithme.repository.DonHangRepository;
import com.studywithme.repository.GhiDanhKhoaHocRepository;
import com.studywithme.repository.NguoiDungRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
@PreAuthorize("isAuthenticated()")
public class StudentWebController {

    private final NguoiDungRepository nguoiDungRepository;
    private final DangKyLopHocRepository dangKyLopHocRepository;
    private final DonHangRepository donHangRepository;
    private final GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;
    private final DanhSachYeuThichRepository danhSachYeuThichRepository;

    public StudentWebController(NguoiDungRepository nguoiDungRepository,
                                DangKyLopHocRepository dangKyLopHocRepository,
                                DonHangRepository donHangRepository,
                                GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository,
                                DanhSachYeuThichRepository danhSachYeuThichRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.dangKyLopHocRepository = dangKyLopHocRepository;
        this.donHangRepository = donHangRepository;
        this.ghiDanhKhoaHocRepository = ghiDanhKhoaHocRepository;
        this.danhSachYeuThichRepository = danhSachYeuThichRepository;
    }

    private Optional<NguoiDung> getUserByPrincipal(Principal principal) {
        if (principal == null) return Optional.empty();
        return nguoiDungRepository.findByEmail(principal.getName());
    }

    @ModelAttribute
    public void loadStudentCommonData(Model model, Principal principal) {
        String studentName = "Học viên";
        String studentEmail = "";
        long soLopDangHoc = 0;
        long soKhoaHocDangHoc = 0;
        long soYeuThich = 0;
        NguoiDung currentUser = null;

        if (principal != null) {
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(principal.getName());
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                studentName = (currentUser.getHoTen() != null && !currentUser.getHoTen().isBlank()) 
                        ? currentUser.getHoTen() 
                        : currentUser.getEmail();
                studentEmail = currentUser.getEmail();
                soLopDangHoc = dangKyLopHocRepository.countByHocVienId(currentUser.getId());
                soKhoaHocDangHoc = ghiDanhKhoaHocRepository.countByNguoiDungId(currentUser.getId());
                soYeuThich = danhSachYeuThichRepository.countByNguoiDungId(currentUser.getId());
            } else {
                studentName = principal.getName();
                studentEmail = principal.getName();
            }
        }

        model.addAttribute("studentName", studentName);
        model.addAttribute("studentEmail", studentEmail);
        model.addAttribute("soLopDangHoc", soLopDangHoc);
        model.addAttribute("soKhoaHocDangHoc", soKhoaHocDangHoc);
        model.addAttribute("soYeuThich", soYeuThich);
        model.addAttribute("currentUser", currentUser);
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DangKyLopHoc> danhSachDangKy = userOpt
                .map(user -> dangKyLopHocRepository.findByHocVienId(user.getId()))
                .orElse(new ArrayList<>());
        List<GhiDanhKhoaHoc> danhSachGhiDanh = userOpt
                .map(user -> ghiDanhKhoaHocRepository.findByNguoiDungIdOrderByNgayGhiDanhDesc(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        model.addAttribute("danhSachGhiDanh", danhSachGhiDanh);
        return "student/dashboard";
    }

    @GetMapping({"/khoa-hoc", "/courses"})
    public String courses(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<GhiDanhKhoaHoc> danhSachGhiDanh = userOpt
                .map(user -> ghiDanhKhoaHocRepository.findByNguoiDungIdOrderByNgayGhiDanhDesc(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachGhiDanh", danhSachGhiDanh);
        return "student/courses";
    }

    @GetMapping({"/lop-hoc", "/classes"})
    public String classes(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DangKyLopHoc> danhSachDangKy = userOpt
                .map(user -> dangKyLopHocRepository.findByHocVienId(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        return "student/classes";
    }

    @GetMapping({"/lich-hoc", "/schedule"})
    public String schedule(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DangKyLopHoc> danhSachDangKy = userOpt
                .map(user -> dangKyLopHocRepository.findByHocVienId(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        return "student/schedule";
    }

    @GetMapping({"/gia-su", "/tutors"})
    public String tutors(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DangKyLopHoc> danhSachDangKy = userOpt
                .map(user -> dangKyLopHocRepository.findByHocVienId(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        return "student/tutors";
    }

    @GetMapping({"/don-hang", "/orders"})
    public String orders(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DonHang> danhSachDonHang = userOpt
                .map(user -> donHangRepository.findByNguoiDungIdOrderByNgayTaoDonDesc(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDonHang", danhSachDonHang);
        return "student/orders";
    }

    @GetMapping({"/yeu-thich", "/wishlist"})
    public String wishlist(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DanhSachYeuThich> danhSachYeuThich = userOpt
                .map(user -> danhSachYeuThichRepository.findByNguoiDungId(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachYeuThich", danhSachYeuThich);
        return "student/wishlist";
    }

    @GetMapping({"/ho-so", "/profile"})
    public String profile(Model model, Principal principal) {
        return "student/profile";
    }
}

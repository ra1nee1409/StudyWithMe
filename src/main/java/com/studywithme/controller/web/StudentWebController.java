package com.studywithme.controller.web;

import com.studywithme.entity.DangKyLopHoc;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.NguoiDung;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.DonHangRepository;
import com.studywithme.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
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

    public StudentWebController(NguoiDungRepository nguoiDungRepository,
                               DangKyLopHocRepository dangKyLopHocRepository,
                               DonHangRepository donHangRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.dangKyLopHocRepository = dangKyLopHocRepository;
        this.donHangRepository = donHangRepository;
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
            } else {
                studentName = principal.getName();
                studentEmail = principal.getName();
            }
        }

        model.addAttribute("studentName", studentName);
        model.addAttribute("studentEmail", studentEmail);
        model.addAttribute("soLopDangHoc", soLopDangHoc);
        model.addAttribute("currentUser", currentUser);
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DangKyLopHoc> danhSachDangKy = userOpt
                .map(user -> dangKyLopHocRepository.findByHocVienId(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        return "student/dashboard";
    }

    @GetMapping("/lop-hoc")
    public String classes(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DangKyLopHoc> danhSachDangKy = userOpt
                .map(user -> dangKyLopHocRepository.findByHocVienId(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        return "student/classes";
    }

    @GetMapping("/lich-hoc")
    public String schedule(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DangKyLopHoc> danhSachDangKy = userOpt
                .map(user -> dangKyLopHocRepository.findByHocVienId(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        return "student/schedule";
    }

    @GetMapping("/gia-su")
    public String tutors(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DangKyLopHoc> danhSachDangKy = userOpt
                .map(user -> dangKyLopHocRepository.findByHocVienId(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        return "student/tutors";
    }

    @GetMapping("/don-hang")
    public String orders(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        List<DonHang> danhSachDonHang = userOpt
                .map(user -> donHangRepository.findByNguoiDungIdOrderByNgayTaoDonDesc(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDonHang", danhSachDonHang);
        return "student/orders";
    }

    @GetMapping("/ho-so")
    public String profile(Model model, Principal principal) {
        return "student/profile";
    }
}

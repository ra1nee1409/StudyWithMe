package com.studywithme.controller.web;

import com.studywithme.dto.response.PhongHocDto;
import com.studywithme.dto.tutor.DatLich1k1Dto;
import com.studywithme.dto.user.CapNhatHoSoRequest;
import com.studywithme.dto.user.DoiMatKhauRequest;
import com.studywithme.entity.DangKyLopHoc;
import com.studywithme.entity.DanhSachYeuThich;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.GhiDanhKhoaHoc;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.DanhSachYeuThichRepository;
import com.studywithme.repository.DatLich1k1Repository;
import com.studywithme.repository.DonHangRepository;
import com.studywithme.repository.GhiDanhKhoaHocRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.DatLich1k1Service;
import com.studywithme.service.ProfileService;
import com.studywithme.service.StudentLearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class StudentWebController {

    private final NguoiDungRepository nguoiDungRepository;
    private final DangKyLopHocRepository dangKyLopHocRepository;
    private final DonHangRepository donHangRepository;
    private final GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;
    private final DanhSachYeuThichRepository danhSachYeuThichRepository;
    private final StudentLearningService studentLearningService;
    private final ProfileService profileService;
    private final DatLich1k1Service datLich1k1Service;
    private final DatLich1k1Repository datLich1k1Repository;

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
        long soCaHoc1k1 = 0;
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
                soCaHoc1k1 = datLich1k1Repository.countByHocVienId(currentUser.getId());
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
        model.addAttribute("soCaHoc1k1", soCaHoc1k1);
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
        List<DatLich1k1Dto> danhSachCaHoc1k1 = userOpt
                .map(user -> datLich1k1Service.layDanhSachCaHocCuaHocVien(user.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachDangKy", danhSachDangKy);
        model.addAttribute("danhSachCaHoc1k1", danhSachCaHoc1k1);
        return "student/schedule";
    }

    /**
     * WBS 3.1: Quản lý danh sách ca học kèm 1-1 của Học viên
     */
    @GetMapping({"/ca-hoc", "/bookings"})
    public String caHoc1k1(Model model, Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        Long hocVienId = userOpt.get().getId();
        List<DatLich1k1Dto> danhSachCaHoc = datLich1k1Service.layDanhSachCaHocCuaHocVien(hocVienId);

        long tongCaHoc = danhSachCaHoc.size();
        long choXacNhan = danhSachCaHoc.stream().filter(c -> c.getTrangThai() == TrangThaiDatLich.CHO_XAC_NHAN).count();
        long choThanhToan = danhSachCaHoc.stream().filter(c -> c.getTrangThai() == TrangThaiDatLich.CHO_THANH_TOAN).count();
        long daKyQuy = danhSachCaHoc.stream().filter(c -> c.getTrangThai() == TrangThaiDatLich.DA_THANH_TOAN_KY_QUY || c.getTrangThai() == TrangThaiDatLich.DANG_DIEN_RA).count();
        long hoanThanh = danhSachCaHoc.stream().filter(c -> c.getTrangThai() == TrangThaiDatLich.HOAN_THANH).count();

        model.addAttribute("danhSachCaHoc", danhSachCaHoc);
        model.addAttribute("tongCaHoc", tongCaHoc);
        model.addAttribute("choXacNhan", choXacNhan);
        model.addAttribute("choThanhToan", choThanhToan);
        model.addAttribute("daKyQuy", daKyQuy);
        model.addAttribute("hoanThanh", hoanThanh);

        return "student/ca-hoc";
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

    @PostMapping({"/ho-so/cap-nhat", "/profile/cap-nhat"})
    public String capNhatHoSo(CapNhatHoSoRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            profileService.capNhatThongTin(principal.getName(), request);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin học viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật hồ sơ: " + e.getMessage());
        }
        return "redirect:/student/profile";
    }

    @PostMapping({"/ho-so/doi-mat-khau", "/profile/doi-mat-khau"})
    public String doiMatKhau(DoiMatKhauRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            profileService.doiMatKhau(principal.getName(), request);
            redirectAttributes.addFlashAttribute("passwordSuccessMessage", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", e.getMessage());
        }
        return "redirect:/student/profile";
    }

    /**
     * Trang S3: Phòng học video trực tuyến & Trình phát LMS Player
     */
    @GetMapping({"/learn/{slug}", "/hoc-tap/{slug}"})
    public String learn(@PathVariable("slug") String slug,
                        @RequestParam(value = "lessonId", required = false) Long lessonId,
                        Model model,
                        Principal principal) {
        Optional<NguoiDung> userOpt = getUserByPrincipal(principal);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        try {
            PhongHocDto phongHoc = studentLearningService.layThongTinPhongHoc(slug, userOpt.get().getId(), lessonId);
            model.addAttribute("title", phongHoc.getKhoaHoc().getTieuDe() + " - Phòng Học Trực Tuyến");
            model.addAttribute("phongHoc", phongHoc);
            model.addAttribute("khoaHoc", phongHoc.getKhoaHoc());
            model.addAttribute("danhSachChuongHoc", phongHoc.getDanhSachChuongHoc());
            model.addAttribute("baiHocHienTai", phongHoc.getBaiHocHienTai());
            model.addAttribute("baiHocTruoc", phongHoc.getBaiHocTruoc());
            model.addAttribute("baiHocTiepTheo", phongHoc.getBaiHocTiepTheo());
            model.addAttribute("tapHopBaiHocDaHoanThanh", phongHoc.getTapHopBaiHocDaHoanThanh());
            model.addAttribute("phanTramTienDo", phongHoc.getPhanTramTienDo());
            model.addAttribute("soBaiDaHoanThanh", phongHoc.getSoBaiDaHoanThanh());
            model.addAttribute("tongSoBaiHoc", phongHoc.getTongSoBaiHoc());
            model.addAttribute("embedVideoUrl", phongHoc.getEmbedVideoUrl());
            return "student/learning";
        } catch (AccessDeniedException e) {
            return "redirect:/khoa-hoc/" + slug + "?chuaGhiDanh=true";
        } catch (Exception e) {
            return "redirect:/student/courses?error=notfound";
        }
    }
}

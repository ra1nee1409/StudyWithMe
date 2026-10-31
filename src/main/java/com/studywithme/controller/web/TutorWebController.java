package com.studywithme.controller.web;

import com.studywithme.dto.user.CapNhatHoSoRequest;
import com.studywithme.dto.user.DoiMatKhauRequest;
import com.studywithme.entity.ChungChiGiangVien;
import com.studywithme.entity.DangKyLopHoc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.LichSuViTien;
import com.studywithme.entity.LopHocTrucTuyen;
import com.studywithme.entity.NguoiDung;
import com.studywithme.repository.ChungChiGiangVienRepository;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LichSuViTienRepository;
import com.studywithme.repository.LopHocTrucTuyenRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/tutor")
@PreAuthorize("hasAnyAuthority('ROLE_GIANG_VIEN', 'ROLE_QUAN_TRI')")
@RequiredArgsConstructor
public class TutorWebController {

    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final LopHocTrucTuyenRepository lopHocTrucTuyenRepository;
    private final DangKyLopHocRepository dangKyLopHocRepository;
    private final LichSuViTienRepository lichSuViTienRepository;
    private final ChungChiGiangVienRepository chungChiGiangVienRepository;
    private final ProfileService profileService;

    private Optional<HoSoGiangVien> getHoSoByPrincipal(Principal principal) {
        if (principal == null) return Optional.empty();
        Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(principal.getName());
        return userOpt.flatMap(user -> hoSoGiangVienRepository.findByNguoiDungId(user.getId()));
    }

    @ModelAttribute
    public void loadTutorCommonData(Model model, Principal principal) {
        String tutorName = "Giảng viên";
        long tongLopHoc = 0;
        long tongHocVien = 0;
        BigDecimal soDuVi = BigDecimal.ZERO;
        HoSoGiangVien hoSo = null;

        if (principal != null) {
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(principal.getName());
            if (userOpt.isPresent()) {
                NguoiDung user = userOpt.get();
                tutorName = (user.getHoTen() != null && !user.getHoTen().isBlank()) ? user.getHoTen() : user.getEmail();
                Optional<HoSoGiangVien> hoSoOpt = hoSoGiangVienRepository.findByNguoiDungId(user.getId());
                if (hoSoOpt.isPresent()) {
                    hoSo = hoSoOpt.get();
                    tongLopHoc = lopHocTrucTuyenRepository.countByGiangVienId(hoSo.getId());
                    tongHocVien = dangKyLopHocRepository.countByLopHocGiangVienId(hoSo.getId());
                    if (hoSo.getSoDuVi() != null) {
                        soDuVi = hoSo.getSoDuVi();
                    }
                }
            }
        }

        NumberFormat vnFormat = NumberFormat.getInstance(Locale.of("vi", "VN"));
        model.addAttribute("tutorName", tutorName);
        model.addAttribute("currentUser", principal != null ? nguoiDungRepository.findByEmail(principal.getName()).orElse(null) : null);
        model.addAttribute("tongLopHoc", tongLopHoc);
        model.addAttribute("tongHocVien", tongHocVien);
        model.addAttribute("soDuViFormat", vnFormat.format(soDuVi));
        model.addAttribute("hoSoGiangVien", hoSo);
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<LopHocTrucTuyen> danhSachLop = new ArrayList<>();
        List<DangKyLopHoc> danhSachHocVien = new ArrayList<>();

        if (hoSoOpt.isPresent()) {
            Long giangVienId = hoSoOpt.get().getId();
            danhSachLop = lopHocTrucTuyenRepository.findByGiangVienIdOrderByNgayTaoDesc(giangVienId);
            danhSachHocVien = dangKyLopHocRepository.findByLopHocGiangVienId(giangVienId);
        }

        model.addAttribute("danhSachLop", danhSachLop);
        model.addAttribute("danhSachHocVien", danhSachHocVien);
        return "tutor/dashboard";
    }

    @GetMapping({"/lop-hoc", "/classes"})
    public String classes(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<LopHocTrucTuyen> danhSachLop = hoSoOpt
                .map(hoSo -> lopHocTrucTuyenRepository.findByGiangVienIdOrderByNgayTaoDesc(hoSo.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachLop", danhSachLop);
        return "tutor/classes";
    }

    @GetMapping({"/lich-day", "/schedule"})
    public String schedule(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<LopHocTrucTuyen> danhSachLop = hoSoOpt
                .map(hoSo -> lopHocTrucTuyenRepository.findByGiangVienIdOrderByNgayTaoDesc(hoSo.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachLop", danhSachLop);
        return "tutor/schedule";
    }

    @GetMapping({"/hoc-vien", "/students"})
    public String students(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<DangKyLopHoc> danhSachHocVien = hoSoOpt
                .map(hoSo -> dangKyLopHocRepository.findByLopHocGiangVienId(hoSo.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachHocVien", danhSachHocVien);
        return "tutor/students";
    }

    @GetMapping({"/vi-tien", "/wallet"})
    public String wallet(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<LichSuViTien> danhSachGiaoDich = hoSoOpt
                .map(hoSo -> lichSuViTienRepository.findByGiangVienIdOrderByNgayTaoDesc(hoSo.getId()))
                .orElse(new ArrayList<>());

        BigDecimal tongThuNhap = danhSachGiaoDich.stream()
                .map(LichSuViTien::getSoTienBienDong)
                .filter(tien -> tien != null && tien.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        NumberFormat vnFormat = NumberFormat.getInstance(Locale.of("vi", "VN"));
        model.addAttribute("danhSachGiaoDich", danhSachGiaoDich);
        model.addAttribute("tongThuNhapFormat", vnFormat.format(tongThuNhap));
        return "tutor/wallet";
    }

    @GetMapping({"/ho-so", "/profile"})
    public String profile(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<ChungChiGiangVien> danhSachChungChi = new ArrayList<>();
        if (hoSoOpt.isPresent()) {
            danhSachChungChi = chungChiGiangVienRepository.findByGiangVienId(hoSoOpt.get().getId());
        }
        model.addAttribute("danhSachChungChi", danhSachChungChi);
        return "tutor/profile";
    }

    @PostMapping({"/ho-so/cap-nhat", "/profile/cap-nhat"})
    public String capNhatHoSo(CapNhatHoSoRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            profileService.capNhatThongTin(principal.getName(), request);
            redirectAttributes.addFlashAttribute("message", "Cập nhật hồ sơ giảng viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật hồ sơ: " + e.getMessage());
        }
        return "redirect:/tutor/profile";
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
        return "redirect:/tutor/profile";
    }
}

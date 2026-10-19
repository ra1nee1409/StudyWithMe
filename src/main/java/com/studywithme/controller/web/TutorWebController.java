package com.studywithme.controller.web;

import com.studywithme.entity.DangKyLopHoc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.LopHocTrucTuyen;
import com.studywithme.entity.NguoiDung;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LopHocTrucTuyenRepository;
import com.studywithme.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
    private final com.studywithme.repository.LichSuViTienRepository lichSuViTienRepository;

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

    @GetMapping("/lop-hoc")
    public String classes(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<LopHocTrucTuyen> danhSachLop = hoSoOpt
                .map(hoSo -> lopHocTrucTuyenRepository.findByGiangVienIdOrderByNgayTaoDesc(hoSo.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachLop", danhSachLop);
        return "tutor/classes";
    }

    @GetMapping("/lich-day")
    public String schedule(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<LopHocTrucTuyen> danhSachLop = hoSoOpt
                .map(hoSo -> lopHocTrucTuyenRepository.findByGiangVienIdOrderByNgayTaoDesc(hoSo.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachLop", danhSachLop);
        return "tutor/schedule";
    }

    @GetMapping("/hoc-vien")
    public String students(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<DangKyLopHoc> danhSachHocVien = hoSoOpt
                .map(hoSo -> dangKyLopHocRepository.findByLopHocGiangVienId(hoSo.getId()))
                .orElse(new ArrayList<>());

        model.addAttribute("danhSachHocVien", danhSachHocVien);
        return "tutor/students";
    }

    @GetMapping("/vi-tien")
    public String wallet(Model model, Principal principal) {
        Optional<HoSoGiangVien> hoSoOpt = getHoSoByPrincipal(principal);
        List<com.studywithme.entity.LichSuViTien> danhSachGiaoDich = hoSoOpt
                .map(hoSo -> lichSuViTienRepository.findByGiangVienIdOrderByNgayTaoDesc(hoSo.getId()))
                .orElse(new ArrayList<>());

        BigDecimal tongThuNhap = danhSachGiaoDich.stream()
                .map(com.studywithme.entity.LichSuViTien::getSoTienBienDong)
                .filter(tien -> tien != null && tien.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        NumberFormat vnFormat = NumberFormat.getInstance(Locale.of("vi", "VN"));
        model.addAttribute("danhSachGiaoDich", danhSachGiaoDich);
        model.addAttribute("tongThuNhapFormat", vnFormat.format(tongThuNhap));
        return "tutor/wallet";
    }

    @GetMapping("/ho-so")
    public String profile(Model model, Principal principal) {
        return "tutor/profile";
    }
}

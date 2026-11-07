package com.studywithme.controller.web;

import com.studywithme.dto.request.DangKyGiangVienRequest;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.GiangVienKycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TroThanhGiangVienWebController {

    private final GiangVienKycService giangVienKycService;
    private final NguoiDungRepository nguoiDungRepository;

    @GetMapping({"/tro-thanh-giang-vien", "/gia-su/dang-ky"})
    public String trangTroThanhGiangVien(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?redirect=/tro-thanh-giang-vien";
        }

        NguoiDung currentUser = nguoiDungRepository.findByEmailIgnoreCase(principal.getName()).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activePage", "become-tutor");

        // Kiểm tra xem người dùng đã là giảng viên được phê duyệt chưa
        boolean daLaGiangVien = currentUser.getDanhSachVaiTro().stream()
                .anyMatch(vt -> vt.getTenVaiTro() == VaiTroName.ROLE_GIANG_VIEN);

        Optional<HoSoGiangVien> hoSoOpt = giangVienKycService.layHoSoTheoUserEmail(currentUser.getEmail());

        if (daLaGiangVien && hoSoOpt.isPresent() && hoSoOpt.get().getTrangThaiDuyet() == TrangThaiGiangVien.DA_DUYET) {
            model.addAttribute("trangThaiKyc", "DA_DUYET");
            model.addAttribute("hoSo", hoSoOpt.get());
            return "tutors/apply";
        }

        if (hoSoOpt.isPresent()) {
            HoSoGiangVien hoSo = hoSoOpt.get();
            model.addAttribute("hoSo", hoSo);
            if (hoSo.getTrangThaiDuyet() == TrangThaiGiangVien.CHO_DUYET) {
                model.addAttribute("trangThaiKyc", "CHO_DUYET");
                return "tutors/apply";
            } else if (hoSo.getTrangThaiDuyet() == TrangThaiGiangVien.TU_CHOI) {
                model.addAttribute("trangThaiKyc", "TU_CHOI");
                if (!model.containsAttribute("requestDto")) {
                    DangKyGiangVienRequest dto = DangKyGiangVienRequest.builder()
                            .tieuDeChuyenMon(hoSo.getTieuDeChuyenMon())
                            .gioiThieuBanThan(hoSo.getGioiThieuBanThan())
                            .linkVideoDayThu(hoSo.getLinkVideoDayThu())
                            .tenNganHang(hoSo.getTenNganHang())
                            .soTaiKhoanNganHang(hoSo.getSoTaiKhoanNganHang())
                            .tenChuTaiKhoan(hoSo.getTenChuTaiKhoan())
                            .build();
                    model.addAttribute("requestDto", dto);
                }
                return "tutors/apply";
            }
        }

        model.addAttribute("trangThaiKyc", "CHUA_NOP");
        if (!model.containsAttribute("requestDto")) {
            model.addAttribute("requestDto", new DangKyGiangVienRequest());
        }
        return "tutors/apply";
    }

    @PostMapping({"/tro-thanh-giang-vien", "/gia-su/dang-ky"})
    public String guiHoSoGiangVien(
            @Valid @ModelAttribute("requestDto") DangKyGiangVienRequest requestDto,
            BindingResult bindingResult,
            @RequestParam(value = "fileChungChi", required = false) MultipartFile fileChungChi,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login?redirect=/tro-thanh-giang-vien";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("requestDto", requestDto);
            return "redirect:/tro-thanh-giang-vien";
        }

        try {
            giangVienKycService.nopHoSoGiangVien(principal.getName(), requestDto, fileChungChi);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Gửi hồ sơ đăng ký giảng viên thành công! Ban Quản Trị StudyWithMe sẽ kiểm duyệt và phản hồi trong thời gian sớm nhất.");
        } catch (Exception e) {
            log.error("Lỗi khi nộp hồ sơ giảng viên: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("requestDto", requestDto);
        }

        return "redirect:/tro-thanh-giang-vien";
    }
}


package com.studywithme.controller.web;

import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.DanhGiaNhanXetRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LopHocTrucTuyenRepository;
import com.studywithme.repository.NguoiDungRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
@RequiredArgsConstructor
public class AdminWebController {

    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final DangKyLopHocRepository dangKyLopHocRepository;
    private final LopHocTrucTuyenRepository lopHocTrucTuyenRepository;
    private final DanhGiaNhanXetRepository danhGiaNhanXetRepository;
    private final com.studywithme.service.AdminThongKeService adminThongKeService;
    private final com.studywithme.service.BannerService bannerService;
    private final com.studywithme.service.DanhMucService danhMucService;

    @ModelAttribute
    public void loadAdminCommonData(Model model, Principal principal) {
        model.addAttribute("adminName", principal != null ? principal.getName() : "Quản Trị Viên");
        model.addAttribute("tongNguoiDung", nguoiDungRepository.count());
        model.addAttribute("tongGiaSu", hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.DA_DUYET));
        model.addAttribute("hoSoChoDuyet", hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.CHO_DUYET));
        model.addAttribute("tongLopHoc", lopHocTrucTuyenRepository.count());
    }

    // ==========================================
    // 1. MỤC TỔNG QUAN (ADMIN DASHBOARD)
    // ==========================================
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        com.studywithme.dto.response.ThongKeTongQuanResponse thongKe = adminThongKeService.layThongKeTongQuan();
        model.addAttribute("thongKe", thongKe);
        model.addAttribute("tongDoanhThu", thongKe.getTongDoanhThu());
        model.addAttribute("tongNguoiDung", thongKe.getTongNguoiDung());
        model.addAttribute("tongKhoaHoc", thongKe.getTongKhoaHocDangHoatDong());
        model.addAttribute("lenhRutTienChoDuyet", thongKe.getLenhRutTienChoDuyet());
        model.addAttribute("hoSoChoDuyet", thongKe.getHoSoGiangVienChoDuyet());
        model.addAttribute("tongChoDuyet", thongKe.getTongYeuCauChoDuyet());
        model.addAttribute("topKhoaHoc", thongKe.getTopKhoaHoc());
        model.addAttribute("giangVienMoi", thongKe.getGiangVienMoi());
        return "admin/dashboard";
    }

    // ==========================================
    // 5. MỤC QUẢN LÝ BANNER TRANG CHỦ (CMS BANNER)
    // ==========================================
    @GetMapping("/banners")
    public String bannersPage(Model model) {
        model.addAttribute("danhSachBanner", bannerService.layTatCaBanner());
        return "admin/banners";
    }

    // ==========================================
    // 6. MỤC QUẢN LÝ CÂY DANH MỤC & CẤP HỌC (TRANG A3)
    // ==========================================
    @GetMapping("/danh-muc")
    public String danhMucPage(Model model) {
        model.addAttribute("cayDanhMuc", danhMucService.layCayDanhMuc());
        model.addAttribute("thongKeDanhMuc", danhMucService.layThongKeDanhMuc());
        return "admin/categories";
    }

    // ==========================================
    // 2. MỤC DUYỆT GIA SƯ (CHUYÊN BIỆT)
    // ==========================================
    @GetMapping("/duyet-gia-su")
    public String duyetGiaSuPage(Model model) {
        model.addAttribute("danhSachChoDuyet", hoSoGiangVienRepository.findByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.CHO_DUYET));
        model.addAttribute("danhSachDaDuyet", hoSoGiangVienRepository.findByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.DA_DUYET));
        model.addAttribute("danhSachTuChoi", hoSoGiangVienRepository.findByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.TU_CHOI));
        return "admin/tutors";
    }

    @PostMapping("/duyet-gia-su/{id}")
    public String duyetGiaSuAction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        hoSoGiangVienRepository.findById(id).ifPresent(hoSo -> {
            hoSo.setTrangThaiDuyet(TrangThaiGiangVien.DA_DUYET);
            hoSoGiangVienRepository.save(hoSo);
        });
        redirectAttributes.addFlashAttribute("message", "Đã phê duyệt hồ sơ gia sư thành công!");
        return "redirect:/admin/duyet-gia-su";
    }

    @PostMapping("/tu-choi-gia-su/{id}")
    public String tuChoiGiaSuAction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        hoSoGiangVienRepository.findById(id).ifPresent(hoSo -> {
            hoSo.setTrangThaiDuyet(TrangThaiGiangVien.TU_CHOI);
            hoSoGiangVienRepository.save(hoSo);
        });
        redirectAttributes.addFlashAttribute("message", "Đã từ chối hồ sơ gia sư!");
        return "redirect:/admin/duyet-gia-su";
    }

    // ==========================================
    // 3. MỤC QUẢN LÝ NGƯỜI DÙNG (CHUYÊN BIỆT)
    // ==========================================
    @GetMapping("/nguoi-dung")
    public String nguoiDungPage(Model model) {
        List<NguoiDung> allUsers = nguoiDungRepository.findAllByOrderByNgayTaoDesc();
        long activeCount = allUsers.stream().filter(u -> u.getTrangThai() == TrangThaiNguoiDung.HOAT_DONG).count();
        long lockedCount = allUsers.size() - activeCount;

        model.addAttribute("danhSachNguoiDung", allUsers);
        model.addAttribute("activeUsersCount", activeCount);
        model.addAttribute("lockedUsersCount", lockedCount);
        return "admin/users";
    }

    @PostMapping("/doi-trang-thai-nguoi-dung/{id}")
    public String doiTrangThaiNguoiDungAction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        nguoiDungRepository.findById(id).ifPresent(nd -> {
            if (nd.getTrangThai() == TrangThaiNguoiDung.HOAT_DONG) {
                nd.setTrangThai(TrangThaiNguoiDung.TAM_KHOA);
            } else {
                nd.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);
            }
            nguoiDungRepository.save(nd);
        });
        redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái người dùng thành công!");
        return "redirect:/admin/nguoi-dung";
    }

    // ==========================================
    // 4. MỤC LỚP HỌC TRỰC TUYẾN (CHUYÊN BIỆT)
    // ==========================================
    @GetMapping("/lop-hoc")
    public String lopHocPage(Model model) {
        model.addAttribute("danhSachLopHoc", lopHocTrucTuyenRepository.findAll());
        model.addAttribute("danhSachDanhGia", danhGiaNhanXetRepository.findAll());
        return "admin/classes";
    }
}

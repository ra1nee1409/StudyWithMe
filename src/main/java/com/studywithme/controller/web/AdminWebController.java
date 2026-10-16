package com.studywithme.controller.web;

import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.TrangThaiGiangVien;
import com.studywithme.entity.enums.TrangThaiNguoiDung;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.DanhGiaNhanXetRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LopHocTrucTuyenRepository;
import com.studywithme.repository.NguoiDungRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final DangKyLopHocRepository dangKyLopHocRepository;
    private final LopHocTrucTuyenRepository lopHocTrucTuyenRepository;
    private final DanhGiaNhanXetRepository danhGiaNhanXetRepository;

    public AdminWebController(NguoiDungRepository nguoiDungRepository,
                              HoSoGiangVienRepository hoSoGiangVienRepository,
                              DangKyLopHocRepository dangKyLopHocRepository,
                              LopHocTrucTuyenRepository lopHocTrucTuyenRepository,
                              DanhGiaNhanXetRepository danhGiaNhanXetRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.dangKyLopHocRepository = dangKyLopHocRepository;
        this.lopHocTrucTuyenRepository = lopHocTrucTuyenRepository;
        this.danhGiaNhanXetRepository = danhGiaNhanXetRepository;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, Principal principal) {
        // 1. Thống kê số liệu thực tế từ Database
        long countNguoiDung = nguoiDungRepository.count();
        long countGiaSu = hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.DA_DUYET);
        long countChoDuyet = hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.CHO_DUYET);
        long countLopHoc = lopHocTrucTuyenRepository.count();

        model.addAttribute("adminName", principal != null ? principal.getName() : "Quản Trị Viên");
        model.addAttribute("tongNguoiDung", countNguoiDung);
        model.addAttribute("tongGiaSu", countGiaSu);
        model.addAttribute("hoSoChoDuyet", countChoDuyet);
        model.addAttribute("tongLopHoc", countLopHoc);

        // 2. Danh sách dữ liệu thực tế từ Database
        model.addAttribute("danhSachGiaSuChoDuyet", hoSoGiangVienRepository.findByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.CHO_DUYET));
        model.addAttribute("danhSachNguoiDung", nguoiDungRepository.findAll());
        model.addAttribute("danhSachLopHoc", lopHocTrucTuyenRepository.findAll());
        model.addAttribute("danhSachDanhGia", danhGiaNhanXetRepository.findAll());

        return "admin";
    }

    @PostMapping("/duyet-gia-su/{id}")
    public String duyetGiaSu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        hoSoGiangVienRepository.findById(id).ifPresent(hoSo -> {
            hoSo.setTrangThaiDuyet(TrangThaiGiangVien.DA_DUYET);
            hoSoGiangVienRepository.save(hoSo);
        });
        redirectAttributes.addFlashAttribute("message", "Đã phê duyệt hồ sơ gia sư thành công!");
        return "redirect:/admin";
    }

    @PostMapping("/tu-choi-gia-su/{id}")
    public String tuChoiGiaSu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        hoSoGiangVienRepository.findById(id).ifPresent(hoSo -> {
            hoSo.setTrangThaiDuyet(TrangThaiGiangVien.TU_CHOI);
            hoSoGiangVienRepository.save(hoSo);
        });
        redirectAttributes.addFlashAttribute("message", "Đã từ chối hồ sơ gia sư!");
        return "redirect:/admin";
    }

    @PostMapping("/doi-trang-thai-nguoi-dung/{id}")
    public String doiTrangThaiNguoiDung(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        nguoiDungRepository.findById(id).ifPresent(nd -> {
            if (nd.getTrangThai() == TrangThaiNguoiDung.HOAT_DONG) {
                nd.setTrangThai(TrangThaiNguoiDung.TAM_KHOA);
            } else {
                nd.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);
            }
            nguoiDungRepository.save(nd);
        });
        redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái người dùng thành công!");
        return "redirect:/admin";
    }
}

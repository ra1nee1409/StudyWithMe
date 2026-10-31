package com.studywithme.controller.web;

import com.studywithme.dto.response.ThongKeTongQuanResponse;
import com.studywithme.dto.user.CapNhatHoSoRequest;
import com.studywithme.dto.user.DoiMatKhauRequest;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.DanhGiaNhanXetRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LopHocTrucTuyenRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.repository.YeuCauRutTienRepository;
import com.studywithme.service.AdminKhoaHocService;
import com.studywithme.service.AdminNguoiDungService;
import com.studywithme.service.AdminTaiChinhService;
import com.studywithme.service.AdminThongKeService;
import com.studywithme.service.BannerService;
import com.studywithme.service.DanhMucService;
import com.studywithme.service.GiangVienKycService;
import com.studywithme.service.ProfileService;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_QUAN_TRI')")
@RequiredArgsConstructor
public class AdminWebController {

    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final DangKyLopHocRepository dangKyLopHocRepository;
    private final LopHocTrucTuyenRepository lopHocTrucTuyenRepository;
    private final DanhGiaNhanXetRepository danhGiaNhanXetRepository;
    private final AdminThongKeService adminThongKeService;
    private final BannerService bannerService;
    private final DanhMucService danhMucService;
    private final GiangVienKycService giangVienKycService;
    private final AdminKhoaHocService adminKhoaHocService;
    private final AdminTaiChinhService adminTaiChinhService;
    private final AdminNguoiDungService adminNguoiDungService;
    private final ProfileService profileService;
    private final YeuCauRutTienRepository yeuCauRutTienRepository;

    @ModelAttribute
    public void loadAdminCommonData(Model model, Principal principal) {
        model.addAttribute("adminName", principal != null ? principal.getName() : "Quản Trị Viên");
        model.addAttribute("currentUser", principal != null ? nguoiDungRepository.findByEmail(principal.getName()).orElse(null) : null);
        model.addAttribute("tongNguoiDung", nguoiDungRepository.count());
        model.addAttribute("tongGiaSu", hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.DA_DUYET));
        model.addAttribute("hoSoChoDuyet", hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.CHO_DUYET));
        model.addAttribute("khoaHocChoDuyet", khoaHocRepository.countByTrangThai(TrangThaiKhoaHoc.CHO_DUYET));
        model.addAttribute("lenhRutTienChoDuyet", yeuCauRutTienRepository.countByTrangThai(TrangThaiRutTien.CHO_XU_LY));
        model.addAttribute("tongLopHoc", lopHocTrucTuyenRepository.count());
    }

    // ==========================================
    // 1. MỤC TỔNG QUAN (ADMIN DASHBOARD)
    // ==========================================
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        ThongKeTongQuanResponse thongKe = adminThongKeService.layThongKeTongQuan();
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
    // 2. MỤC DUYỆT GIA SƯ (TRANG A4 - TEACHER KYC)
    // ==========================================
    @GetMapping({"/duyet-gia-su", "/tutors"})
    public String duyetGiaSuPage(Model model) {
        model.addAttribute("danhSachChoDuyet", giangVienKycService.layDanhSachHoSo(TrangThaiGiangVien.CHO_DUYET));
        model.addAttribute("danhSachDaDuyet", giangVienKycService.layDanhSachHoSo(TrangThaiGiangVien.DA_DUYET));
        model.addAttribute("danhSachTuChoi", giangVienKycService.layDanhSachHoSo(TrangThaiGiangVien.TU_CHOI));
        model.addAttribute("thongKeKyc", giangVienKycService.layThongKeKyc());
        return "admin/tutors";
    }

    @PostMapping({"/duyet-gia-su/{id}", "/tutors/{id}/approve"})
    public String duyetGiaSuAction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        giangVienKycService.pheDuyetHoSo(id);
        redirectAttributes.addFlashAttribute("message", "Đã phê duyệt hồ sơ gia sư và nâng quyền ROLE_GIANG_VIEN thành công!");
        return "redirect:/admin/duyet-gia-su";
    }

    @PostMapping({"/tu-choi-gia-su/{id}", "/tutors/{id}/reject"})
    public String tuChoiGiaSuAction(@PathVariable Long id,
                                   @RequestParam(name = "lyDoTuChoi", required = false) String lyDoTuChoi,
                                   RedirectAttributes redirectAttributes) {
        giangVienKycService.tuChoiHoSo(id, lyDoTuChoi);
        redirectAttributes.addFlashAttribute("message", "Đã từ chối hồ sơ gia sư và lưu lý do thành công!");
        return "redirect:/admin/duyet-gia-su";
    }

    // ==========================================
    // 3. MỤC QUẢN LÝ NGƯỜI DÙNG (CHUYÊN BIỆT)
    // ==========================================
    @GetMapping("/nguoi-dung")
    public String nguoiDungPage(Model model) {
        List<NguoiDung> allUsers = adminNguoiDungService.layTatCaNguoiDung();
        long activeCount = allUsers.stream().filter(u -> u.getTrangThai() == TrangThaiNguoiDung.HOAT_DONG).count();
        long lockedCount = allUsers.size() - activeCount;

        model.addAttribute("danhSachNguoiDung", allUsers);
        model.addAttribute("activeUsersCount", activeCount);
        model.addAttribute("lockedUsersCount", lockedCount);
        return "admin/users";
    }

    @PostMapping("/doi-trang-thai-nguoi-dung/{id}")
    public String doiTrangThaiNguoiDungAction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminNguoiDungService.doiTrangThaiNguoiDung(id);
            redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/nguoi-dung";
    }

    @PostMapping("/xoa-nguoi-dung/{id}")
    public String xoaNguoiDungAction(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String currentAdminEmail = principal != null ? principal.getName() : null;
            adminNguoiDungService.xoaNguoiDungVinhVien(id, currentAdminEmail);
            redirectAttributes.addFlashAttribute("message", "Đã xóa vĩnh viễn tài khoản người dùng và toàn bộ dữ liệu liên quan khỏi cơ sở dữ liệu!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa tài khoản: " + e.getMessage());
        }
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

    // ==========================================
    // 7. MỤC KIỂM DUYỆT KHÓA HỌC (TRANG A5)
    // ==========================================
    @GetMapping({"/khoa-hoc/kiem-duyet", "/courses", "/courses/moderation"})
    public String kiemDuyetKhoaHocPage(Model model) {
        model.addAttribute("danhSachChoDuyet", adminKhoaHocService.layDanhSachKhoaHoc(TrangThaiKhoaHoc.CHO_DUYET));
        model.addAttribute("danhSachDaXuatBan", adminKhoaHocService.layDanhSachKhoaHoc(TrangThaiKhoaHoc.DA_XUAT_BAN));
        model.addAttribute("danhSachTuChoi", adminKhoaHocService.layDanhSachKhoaHoc(TrangThaiKhoaHoc.TU_CHOI));
        model.addAttribute("thongKeKiemDuyet", adminKhoaHocService.layThongKe());
        return "admin/course-moderation";
    }

    @PostMapping({"/khoa-hoc/{id}/phe-duyet", "/courses/{id}/approve"})
    public String pheDuyetKhoaHocAction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminKhoaHocService.pheDuyetKhoaHoc(id);
        redirectAttributes.addFlashAttribute("message", "Đã phê duyệt và xuất bản khóa học thành công!");
        return "redirect:/admin/khoa-hoc/kiem-duyet";
    }

    @PostMapping({"/khoa-hoc/{id}/tu-choi", "/courses/{id}/reject"})
    public String tuChoiKhoaHocAction(@PathVariable Long id,
                                      @RequestParam(name = "lyDoTuChoi", required = false) String lyDoTuChoi,
                                      RedirectAttributes redirectAttributes) {
        adminKhoaHocService.tuChoiKhoaHoc(id, lyDoTuChoi);
        redirectAttributes.addFlashAttribute("message", "Đã từ chối khóa học và lưu lý do thành công!");
        return "redirect:/admin/khoa-hoc/kiem-duyet";
    }

    // ==========================================
    // 8. MỤC QUẢN LÝ TÀI CHÍNH & RÚT TIỀN (TRANG A7)
    // ==========================================
    @GetMapping({"/tai-chinh/rut-tien", "/payouts"})
    public String payoutsPage(Model model) {
        model.addAttribute("danhSachChoXuLy", adminTaiChinhService.layDanhSachYeuCauRutTien(TrangThaiRutTien.CHO_XU_LY));
        model.addAttribute("danhSachDaChuyen", adminTaiChinhService.layDanhSachYeuCauRutTien(TrangThaiRutTien.DA_CHUYEN_TIEN));
        model.addAttribute("danhSachTuChoi", adminTaiChinhService.layDanhSachYeuCauRutTien(TrangThaiRutTien.TU_CHOI));
        model.addAttribute("thongKeRutTien", adminTaiChinhService.layThongKeRutTien());
        return "admin/payouts";
    }

    @PostMapping({"/tai-chinh/rut-tien/{id}/xac-nhan", "/payouts/{id}/approve"})
    public String xacNhanChuyenTienAction(@PathVariable Long id,
                                          @RequestParam(name = "ghiChuAdmin", required = false) String ghiChuAdmin,
                                          RedirectAttributes redirectAttributes) {
        adminTaiChinhService.xacNhanChuyenTien(id, ghiChuAdmin);
        redirectAttributes.addFlashAttribute("message", "Đã xác nhận giải ngân chuyển tiền thành công!");
        return "redirect:/admin/tai-chinh/rut-tien";
    }

    @PostMapping({"/tai-chinh/rut-tien/{id}/tu-choi", "/payouts/{id}/reject"})
    public String tuChoiRutTienAction(@PathVariable Long id,
                                      @RequestParam(name = "lyDoTuChoi") String lyDoTuChoi,
                                      RedirectAttributes redirectAttributes) {
        adminTaiChinhService.tuChoiRutTien(id, lyDoTuChoi);
        redirectAttributes.addFlashAttribute("message", "Đã từ chối lệnh rút tiền và hoàn tiền về ví giảng viên thành công!");
        return "redirect:/admin/tai-chinh/rut-tien";
    }

    // ==========================================
    // 9. QUẢN LÝ DÒNG TIỀN & GIAO DỊCH (TIỀN RA / TIỀN VÀO)
    // ==========================================
    @GetMapping({"/tai-chinh/dong-tien", "/dong-tien", "/transactions", "/cash-flow"})
    public String dongTienPage(@RequestParam(name = "loai", required = false) String loai,
                               @RequestParam(name = "phanLoai", required = false) String phanLoai,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               Model model) {
        model.addAttribute("thongKeDongTien", adminTaiChinhService.layThongKeDongTien());
        model.addAttribute("danhSachDongTien", adminTaiChinhService.layDanhSachDongTien(loai, phanLoai, keyword));
        model.addAttribute("currentLoai", loai != null ? loai : "TAT_CA");
        model.addAttribute("currentPhanLoai", phanLoai != null ? phanLoai : "TAT_CA");
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("activeTab", "transactions");
        return "admin/transactions";
    }

    // ==========================================
    // 10. HỒ SƠ QUẢN TRỊ VIÊN & ĐỔI MẬT KHẨU
    // ==========================================
    @GetMapping({"/profile", "/ho-so"})
    public String adminProfilePage(Model model, Principal principal) {
        model.addAttribute("activeTab", "profile");
        return "admin/profile";
    }

    @PostMapping({"/profile/cap-nhat", "/ho-so/cap-nhat"})
    public String capNhatHoSoAdmin(CapNhatHoSoRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            profileService.capNhatThongTin(principal.getName(), request);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin quản trị viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể cập nhật hồ sơ: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }

    @PostMapping({"/profile/doi-mat-khau", "/ho-so/doi-mat-khau"})
    public String doiMatKhauAdmin(DoiMatKhauRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            profileService.doiMatKhau(principal.getName(), request);
            redirectAttributes.addFlashAttribute("passwordSuccessMessage", "Đổi mật khẩu quản trị viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", e.getMessage());
        }
        return "redirect:/admin/profile";
    }
}



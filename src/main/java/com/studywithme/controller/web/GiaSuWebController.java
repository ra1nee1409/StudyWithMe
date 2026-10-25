package com.studywithme.controller.web;

import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.LopHocTrucTuyen;
import com.studywithme.service.GiaSuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/gia-su")
public class GiaSuWebController {

    private final GiaSuService giaSuService;

    public GiaSuWebController(GiaSuService giaSuService) {
        this.giaSuService = giaSuService;
    }

    /**
     * Trang H4: Danh sách & Khám phá Gia sư (Tutor Directory)
     */
    @GetMapping({"", "/"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String chungChi,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "moi-nhat") String sort,
            Model model) {

        Sort sortObj = Sort.by(Sort.Direction.DESC, "ngayTao");
        Pageable pageable = PageRequest.of(Math.max(0, page), size, sortObj);

        Page<HoSoGiangVien> tutors = giaSuService.locGiaSu(keyword, chungChi, pageable);

        model.addAttribute("tutors", tutors);
        model.addAttribute("totalTutors", tutors.getTotalElements());
        model.addAttribute("selectedKeyword", keyword != null ? keyword : "");
        model.addAttribute("selectedChungChi", chungChi != null ? chungChi : "");
        model.addAttribute("selectedSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tutors.getTotalPages());
        model.addAttribute("activePage", "tutors");

        return "tutors/index";
    }

    /**
     * Trang H5: Chi tiết Hồ sơ Gia sư (Tutor Public Profile)
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        HoSoGiangVien tutor = giaSuService.layChiTietGiaSu(id);
        List<LopHocTrucTuyen> onlineClasses = giaSuService.layLopHocCuaGiaSu(id);
        List<KhoaHoc> courses = giaSuService.layKhoaHocCuaGiaSu(id);
        List<HoSoGiangVien> similarTutors = giaSuService.layGiaSuTuongTu(id);

        model.addAttribute("tutor", tutor);
        model.addAttribute("onlineClasses", onlineClasses);
        model.addAttribute("courses", courses);
        model.addAttribute("similarTutors", similarTutors);
        model.addAttribute("activePage", "tutors");

        return "tutors/detail";
    }
}

package com.studywithme;

import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.GiangVienKycService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class BecomeTutorIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private GiangVienKycService giangVienKycService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("1. Truy cập /tro-thanh-giang-vien khi chưa đăng nhập sẽ chuyển hướng sang /login kèm redirect")
    void testChuaDangNhap_ChuyenHuongLogin() throws Exception {
        mockMvc.perform(get("/tro-thanh-giang-vien"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirect=/tro-thanh-giang-vien"));
    }

    @Test
    @WithMockUser(username = "hocvien_test_apply@gmail.com", authorities = {"ROLE_HOC_VIEN"})
    @DisplayName("2. Học viên đăng nhập truy cập /tro-thanh-giang-vien hiển thị giao diện nộp hồ sơ CHUA_NOP")
    void testHocVienDangNhap_HienThiTrangNopHoSo() throws Exception {
        NguoiDung user = NguoiDung.builder()
                .email("hocvien_test_apply@gmail.com")
                .hoTen("Học Viên Ứng Tuyển")
                .matKhau("123456")
                .soDienThoai("0988112233")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build();
        nguoiDungRepository.save(user);

        mockMvc.perform(get("/tro-thanh-giang-vien"))
                .andExpect(status().isOk())
                .andExpect(view().name("tutors/apply"))
                .andExpect(model().attribute("trangThaiKyc", "CHUA_NOP"))
                .andExpect(model().attributeExists("currentUser", "requestDto"));
    }

    @Test
    @WithMockUser(username = "hocvien_nop_cv@gmail.com", authorities = {"ROLE_HOC_VIEN"})
    @DisplayName("3. Học viên nộp đơn đăng ký làm giảng viên thành công và chuyển trạng thái sang CHO_DUYET")
    void testNopHoSoGiangVienThanhCong() throws Exception {
        NguoiDung user = NguoiDung.builder()
                .email("hocvien_nop_cv@gmail.com")
                .hoTen("Học Viên Nộp CV")
                .matKhau("123456")
                .soDienThoai("0988445566")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build();
        nguoiDungRepository.save(user);

        MockMultipartFile fileChungChi = new MockMultipartFile(
                "fileChungChi",
                "cv-test.pdf",
                "application/pdf",
                "Mock CV content".getBytes()
        );

        mockMvc.perform(multipart("/tro-thanh-giang-vien")
                        .file(fileChungChi)
                        .param("tieuDeChuyenMon", "Thạc Sĩ Toán Học Ứng Dụng")
                        .param("gioiThieuBanThan", "Có 5 năm kinh nghiệm luyện thi đại học môn Toán điểm 9+.")
                        .param("linkVideoDayThu", "https://youtube.com/watch?v=demo123")
                        .param("tenNganHang", "MB Bank")
                        .param("soTaiKhoanNganHang", "0988445566")
                        .param("tenChuTaiKhoan", "HOC VIEN NOP CV")
                        .param("tenChungChi", "Bằng Thạc Sĩ Sư Phạm Toán")
                        .param("noiCap", "Đại học Sư phạm Hà Nội")
                        .param("diemSoHoacXepLoai", "Xuất sắc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tro-thanh-giang-vien"))
                .andExpect(flash().attributeExists("successMessage"));

        // Kiểm tra cơ sở dữ liệu đã tạo hồ sơ CHO_DUYET
        HoSoGiangVien hoSo = hoSoGiangVienRepository.findByNguoiDungId(user.getId())
                .orElseThrow(() -> new AssertionError("Hồ sơ giảng viên chưa được tạo trong DB!"));

        assertEquals(TrangThaiGiangVien.CHO_DUYET, hoSo.getTrangThaiDuyet());
        assertEquals("Thạc Sĩ Toán Học Ứng Dụng", hoSo.getTieuDeChuyenMon());

        // Mô phỏng Admin duyệt hồ sơ
        giangVienKycService.pheDuyetHoSo(hoSo.getId());

        HoSoGiangVien hoSoDaDuyet = hoSoGiangVienRepository.findById(hoSo.getId()).orElseThrow();
        assertEquals(TrangThaiGiangVien.DA_DUYET, hoSoDaDuyet.getTrangThaiDuyet());

        NguoiDung userSauDuyet = nguoiDungRepository.findById(user.getId()).orElseThrow();
        boolean hasGiangVienRole = userSauDuyet.getDanhSachVaiTro().stream()
                .anyMatch(vt -> vt.getTenVaiTro() == VaiTroName.ROLE_GIANG_VIEN);
        assertTrue(hasGiangVienRole, "Người dùng phải được cấp ROLE_GIANG_VIEN sau khi admin phê duyệt!");
    }
}


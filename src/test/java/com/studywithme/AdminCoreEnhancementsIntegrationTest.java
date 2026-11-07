package com.studywithme;

import com.studywithme.dto.response.ThongKeTongQuanResponse;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import com.studywithme.service.AdminThongKeService;
import com.studywithme.service.CauHinhHeThongService;
import com.studywithme.service.GiangVienKycService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class AdminCoreEnhancementsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AdminThongKeService adminThongKeService;

    @Autowired
    private CauHinhHeThongService cauHinhHeThongService;

    @Autowired
    private GiangVienKycService giangVienKycService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    // =========================================================================
    // 1. TEST NÂNG CẤP DASHBOARD ADMIN: CA HỌC 1-1 & TOP GIA SƯ
    // =========================================================================

    @Test
    @DisplayName("Dashboard: Service trả về đầy đủ chỉ số ca học 1-1, gia sư và danh sách top gia sư")
    void testDashboardServiceThongKeCaHoc1k1VaTopGiaSu() {
        ThongKeTongQuanResponse stats = adminThongKeService.layThongKeTongQuan();

        assertNotNull(stats, "Thống kê tổng quan không được null");
        assertNotNull(stats.getTongCaHoc1k1(), "Tổng ca học 1-1 không được null");
        assertNotNull(stats.getTongGiaSuDaDuyet(), "Tổng gia sư đã duyệt không được null");
        assertNotNull(stats.getTopGiaSu(), "Top gia sư không được null");
        assertNotNull(stats.getCaHocGanDay(), "Danh sách ca học gần đây không được null");
    }

    @Test
    @WithMockUser(username = "admin@studywithme.vn", authorities = {"ROLE_QUAN_TRI"})
    @DisplayName("Dashboard: Web Controller GET /admin/dashboard hiển thị view và các thuộc tính ca học 1-1")
    void testDashboardWebController() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("tongCaHoc1k1"))
                .andExpect(model().attributeExists("tongGiaSuDaDuyet"))
                .andExpect(model().attributeExists("topGiaSu"))
                .andExpect(model().attributeExists("caHocGanDay"));
    }

    // =========================================================================
    // 2. TEST CẤU HÌNH % PHÍ NỀN TẢNG (COMMISSION SETTING)
    // =========================================================================

    @Test
    @DisplayName("Cấu hình % phí sàn: Cập nhật và tính toán hệ số hoa hồng chính xác")
    void testCauHinhHoaHongService() {
        // Cập nhật tỷ lệ hợp lệ 15%
        cauHinhHeThongService.capNhatTyLeHoaHongSan(BigDecimal.valueOf(15.0));

        BigDecimal tyLe = cauHinhHeThongService.layTyLeHoaHongSan();
        BigDecimal heSoSan = cauHinhHeThongService.layHeSoHoaHongSan();
        BigDecimal heSoGv = cauHinhHeThongService.layHeSoGiangVienNhan();

        assertEquals(0, BigDecimal.valueOf(15.0).compareTo(tyLe), "Tỷ lệ hoa hồng phải là 15%");
        assertEquals(0, new BigDecimal("0.1500").compareTo(heSoSan), "Hệ số sàn phải là 0.1500");
        assertEquals(0, new BigDecimal("0.8500").compareTo(heSoGv), "Hệ số giảng viên nhận phải là 0.8500");

        // Kiểm tra validate: Tỷ lệ < 0% phải ném ngoại lệ
        assertThrows(IllegalArgumentException.class, () -> {
            cauHinhHeThongService.capNhatTyLeHoaHongSan(BigDecimal.valueOf(-5.0));
        });

        // Kiểm tra validate: Tỷ lệ > 50% phải ném ngoại lệ
        assertThrows(IllegalArgumentException.class, () -> {
            cauHinhHeThongService.capNhatTyLeHoaHongSan(BigDecimal.valueOf(55.0));
        });
    }

    @Test
    @WithMockUser(username = "admin@studywithme.vn", authorities = {"ROLE_QUAN_TRI"})
    @DisplayName("Cấu hình % phí sàn: Web Controller POST cập nhật thành công và chuyển hướng")
    void testCapNhatHoaHongWebController() throws Exception {
        mockMvc.perform(post("/admin/tai-chinh/cau-hinh-hoa-hong")
                        .param("tyLeHoaHong", "25.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tai-chinh/dong-tien"))
                .andExpect(flash().attributeExists("message"));

        BigDecimal tyLeHienTai = cauHinhHeThongService.layTyLeHoaHongSan();
        assertEquals(0, BigDecimal.valueOf(25.0).compareTo(tyLeHienTai), "Tỷ lệ hoa hồng sau khi post phải là 25%");
    }

    // =========================================================================
    // 3. TEST ĐÌNH CHỈ QUYỀN DẠY (REVOKE TUTOR)
    // =========================================================================

    @Test
    @DisplayName("Đình chỉ gia sư: Chuyển hồ sơ sang TU_CHOI và tước bỏ quyền ROLE_GIANG_VIEN")
    void testDinhChiQuyenGiangVienService() {
        // Chuẩn bị vai trò
        VaiTro roleHocVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).build()));
        VaiTro roleGiangVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_GIANG_VIEN).build()));

        // Tạo tài khoản người dùng có 2 vai trò
        String randomEmail = "tutor_revoke_" + UUID.randomUUID().toString().substring(0, 8) + "@studywithme.vn";
        NguoiDung tutor = NguoiDung.builder()
                .email(randomEmail)
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Gia Sư Bị Đình Chỉ")
                .soDienThoai("0987654321")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        tutor.getDanhSachVaiTro().add(roleHocVien);
        tutor.getDanhSachVaiTro().add(roleGiangVien);
        tutor = nguoiDungRepository.save(tutor);

        // Tạo hồ sơ giảng viên đã được duyệt
        HoSoGiangVien hoSo = HoSoGiangVien.builder()
                .nguoiDung(tutor)
                .tieuDeChuyenMon("Toán Cao Cấp")
                .gioiThieuBanThan("Gia sư toán kinh nghiệm")
                .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                .build();
        hoSo = hoSoGiangVienRepository.save(hoSo);

        // Thực hiện đình chỉ quyền dạy
        String lyDo = "Gia sư không vào lớp 3 buổi liên tiếp";
        giangVienKycService.dinhChiQuyenGiangVien(hoSo.getId(), lyDo);

        // Kiểm tra hồ sơ
        HoSoGiangVien hoSoSau = hoSoGiangVienRepository.findById(hoSo.getId()).orElseThrow();
        assertEquals(TrangThaiGiangVien.TU_CHOI, hoSoSau.getTrangThaiDuyet(), "Trạng thái hồ sơ phải chuyển sang TU_CHOI");
        assertTrue(hoSoSau.getLyDoTuChoi().contains(lyDo), "Lý do từ chối phải chứa lý do đình chỉ");

        // Kiểm tra quyền của người dùng: mất ROLE_GIANG_VIEN, vẫn còn ROLE_HOC_VIEN
        NguoiDung tutorSau = nguoiDungRepository.findById(tutor.getId()).orElseThrow();
        boolean coQuyenGiangVien = tutorSau.getDanhSachVaiTro().stream()
                .anyMatch(v -> v.getTenVaiTro() == VaiTroName.ROLE_GIANG_VIEN);
        boolean coQuyenHocVien = tutorSau.getDanhSachVaiTro().stream()
                .anyMatch(v -> v.getTenVaiTro() == VaiTroName.ROLE_HOC_VIEN);

        assertFalse(coQuyenGiangVien, "Người dùng không được còn ROLE_GIANG_VIEN sau khi bị đình chỉ");
        assertTrue(coQuyenHocVien, "Người dùng vẫn phải giữ ROLE_HOC_VIEN để sinh hoạt như học viên bình thường");
    }

    @Test
    @WithMockUser(username = "admin@studywithme.vn", authorities = {"ROLE_QUAN_TRI"})
    @DisplayName("Đình chỉ gia sư: Web Controller POST /admin/dinh-chi-gia-su/{id} chuyển hướng và cập nhật")
    void testDinhChiGiaSuWebController() throws Exception {
        // Tạo dữ liệu test
        VaiTro roleHocVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).build()));
        VaiTro roleGiangVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_GIANG_VIEN).build()));

        NguoiDung tutor = NguoiDung.builder()
                .email("tutor_web_" + UUID.randomUUID().toString().substring(0, 8) + "@studywithme.vn")
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Gia Sư Web Controller Test")
                .soDienThoai("0988888888")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        tutor.getDanhSachVaiTro().add(roleHocVien);
        tutor.getDanhSachVaiTro().add(roleGiangVien);
        tutor = nguoiDungRepository.save(tutor);

        HoSoGiangVien hoSo = HoSoGiangVien.builder()
                .nguoiDung(tutor)
                .tieuDeChuyenMon("Tiếng Anh IELTS")
                .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                .build();
        hoSo = hoSoGiangVienRepository.save(hoSo);

        // Gọi POST controller
        mockMvc.perform(post("/admin/dinh-chi-gia-su/" + hoSo.getId())
                        .param("lyDoDinhChi", "Học viên phản ánh thái độ thiếu chuyên nghiệp"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/duyet-gia-su"))
                .andExpect(flash().attributeExists("message"));

        HoSoGiangVien hoSoSau = hoSoGiangVienRepository.findById(hoSo.getId()).orElseThrow();
        assertEquals(TrangThaiGiangVien.TU_CHOI, hoSoSau.getTrangThaiDuyet());
    }

    @Test
    @WithMockUser(username = "admin@studywithme.vn", authorities = {"ROLE_QUAN_TRI"})
    @DisplayName("Đình chỉ gia sư: API Controller POST /api/admin/giang-vien/{id}/dinh-chi trả về 200 OK")
    void testDinhChiGiaSuApiController() throws Exception {
        VaiTro roleGiangVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_GIANG_VIEN).build()));

        NguoiDung tutor = NguoiDung.builder()
                .email("tutor_api_" + UUID.randomUUID().toString().substring(0, 8) + "@studywithme.vn")
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Gia Sư API Test")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        tutor.getDanhSachVaiTro().add(roleGiangVien);
        tutor = nguoiDungRepository.save(tutor);

        HoSoGiangVien hoSo = HoSoGiangVien.builder()
                .nguoiDung(tutor)
                .tieuDeChuyenMon("Lập trình Java")
                .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                .build();
        hoSo = hoSoGiangVienRepository.save(hoSo);

        String requestJson = "{\"lyDo\":\"Đình chỉ qua API Admin\"}";

        mockMvc.perform(post("/api/admin/giang-vien/" + hoSo.getId() + "/dinh-chi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThaiDuyet").value("TU_CHOI"));
    }

    // =========================================================================
    // 4. TEST PHÂN QUYỀN: ADMIN KHÔNG ĐƯỢC PHÉP TRUY CẬP CỔNG GIẢNG VIÊN
    // =========================================================================

    @Test
    @WithMockUser(username = "admin@studywithme.vn", authorities = {"ROLE_QUAN_TRI"})
    @DisplayName("Phân quyền: Admin (ROLE_QUAN_TRI) bị từ chối truy cập (403 Forbidden) khi vào cổng web giảng viên /tutor/**")
    void testAdminKhongDuocVaoCongGiangVienWeb() throws Exception {
        mockMvc.perform(get("/tutor/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@studywithme.vn", authorities = {"ROLE_QUAN_TRI"})
    @DisplayName("Phân quyền: Admin (ROLE_QUAN_TRI) bị từ chối truy cập (403 Forbidden) khi gọi API giảng viên /api/giang-vien/**")
    void testAdminKhongDuocGoiApiGiangVien() throws Exception {
        mockMvc.perform(get("/api/giang-vien/khoa-hoc/danh-sach"))
                .andExpect(status().isForbidden());
    }
}

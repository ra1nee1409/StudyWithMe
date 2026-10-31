package com.studywithme;

import com.jayway.jsonpath.JsonPath;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.YeuCauRutTien;
import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LichSuViTienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import com.studywithme.repository.YeuCauRutTienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class PayoutApprovalIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private YeuCauRutTienRepository yeuCauRutTienRepository;

    @Autowired
    private LichSuViTienRepository lichSuViTienRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    private MockMvc mockMvc;
    private YeuCauRutTien testPendingPayout;
    private HoSoGiangVien testGiangVien;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        // Đảm bảo mật khẩu admin hợp lệ
        this.nguoiDungRepository.findByEmail("admin@studywithme.vn").ifPresent(admin -> {
            admin.setMatKhau(this.passwordEncoder.encode("Admin@123456"));
            admin.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);
            this.nguoiDungRepository.save(admin);
        });

        // Đảm bảo có tài khoản học viên mẫu cho layStudentToken()
        this.nguoiDungRepository.findByEmail("student@studywithme.vn").orElseGet(() -> {
            VaiTro roleHocVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN).orElseGet(() ->
                    vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).moTa("Học viên").build())
            );
            return nguoiDungRepository.save(NguoiDung.builder()
                    .email("student@studywithme.vn")
                    .matKhau(this.passwordEncoder.encode("Student@123456"))
                    .hoTen("Học viên Test")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(Set.of(roleHocVien))
                    .build());
        });

        // Tạo hồ sơ giảng viên & lệnh rút tiền mẫu cho test
        NguoiDung tutorUser = this.nguoiDungRepository.findByEmail("tutor@studywithme.vn").orElseGet(() -> {
            VaiTro roleGiangVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN).orElseGet(() ->
                    vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_GIANG_VIEN).moTa("Giảng viên").build())
            );
            return nguoiDungRepository.save(NguoiDung.builder()
                    .email("tutor@studywithme.vn")
                    .matKhau(this.passwordEncoder.encode("dummy_test_pass"))
                    .hoTen("Giảng viên Test Rút Tiền")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(Set.of(roleGiangVien))
                    .build());
        });

        testGiangVien = this.hoSoGiangVienRepository.findByNguoiDungId(tutorUser.getId()).orElseGet(() ->
                hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                        .nguoiDung(tutorUser)
                        .tieuDeChuyenMon("Giảng viên Test")
                        .soDuVi(new BigDecimal("5000000"))
                        .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                        .build())
        );

        testPendingPayout = this.yeuCauRutTienRepository.save(YeuCauRutTien.builder()
                .giangVien(testGiangVien)
                .soTienRut(new BigDecimal("1000000"))
                .tenNganHang("Vietcombank")
                .soTaiKhoan("0123456789")
                .tenChuTaiKhoan("HOANG VAN NAM")
                .trangThai(TrangThaiRutTien.CHO_XU_LY)
                .build());
    }

    private String layAdminToken() throws Exception {
        String loginJson = "{\"email\":\"admin@studywithme.vn\",\"matKhau\":\"Admin@123456\"}";
        MvcResult result = mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return JsonPath.read(responseBody, "$.duLieu.accessToken");
    }

    private String layStudentToken() throws Exception {
        String loginJson = "{\"email\":\"student@studywithme.vn\",\"matKhau\":\"Student@123456\"}";
        MvcResult result = mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return JsonPath.read(responseBody, "$.duLieu.accessToken");
    }

    @Test
    @DisplayName("1. Lấy danh sách lệnh rút tiền - Admin xác thực thành công")
    void testLayDanhSachYeuCauRutTien() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/tai-chinh/yeu-cau-rut-tien")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("2. Lấy thống kê rút tiền KPI - Admin xác thực thành công")
    void testThongKeRutTien() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/tai-chinh/thong-ke")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.soLenhChoXuLy", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.duLieu.tongTienChoXuLy", notNullValue()));
    }

    @Test
    @DisplayName("3. Xác nhận chuyển tiền thành công - Trạng thái chuyển DA_CHUYEN_TIEN & ghi sổ cái")
    void testXacNhanChuyenTienThanhCong() throws Exception {
        assertNotNull(testPendingPayout, "Cần có lệnh rút tiền mẫu");
        String token = layAdminToken();
        String reqJson = "{\"maGiaoDichNganHang\":\"FT999888\",\"ghiChu\":\"Chuyển khoản thành công\"}";

        mockMvc.perform(post("/api/admin/tai-chinh/yeu-cau-rut-tien/" + testPendingPayout.getId() + "/xac-nhan")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_CHUYEN_TIEN"))
                .andExpect(jsonPath("$.duLieu.ghiChuAdmin", containsString("FT999888")));

        // Kiểm tra database
        YeuCauRutTien updated = yeuCauRutTienRepository.findById(testPendingPayout.getId()).orElseThrow();
        assertEquals(TrangThaiRutTien.DA_CHUYEN_TIEN, updated.getTrangThai());
        assertNotNull(updated.getNgayXuLy());

        // Kiểm tra sổ cái LichSuViTien
        boolean existsLedger = lichSuViTienRepository.findByGiangVienIdOrderByNgayTaoDesc(testGiangVien.getId())
                .stream().anyMatch(ls -> ls.getLoaiGiaoDich() == LoaiGiaoDichVi.RUT_TIEN_VE_NGAN_HANG);
        assertTrue(existsLedger);
    }

    @Test
    @DisplayName("4. Từ chối lệnh rút tiền - Trạng thái TU_CHOI, hoàn tiền vào ví & ghi sổ cái HOAN_TIEN")
    void testTuChoiRutTienVaHoanTienVi() throws Exception {
        assertNotNull(testPendingPayout, "Cần có lệnh rút tiền mẫu");
        String token = layAdminToken();
        BigDecimal soDuBanDau = testGiangVien.getSoDuVi();

        String reqJson = "{\"lyDoTuChoi\":\"Số tài khoản ngân hàng không hợp lệ\"}";

        mockMvc.perform(post("/api/admin/tai-chinh/yeu-cau-rut-tien/" + testPendingPayout.getId() + "/tu-choi")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("TU_CHOI"))
                .andExpect(jsonPath("$.duLieu.ghiChuAdmin").value("Số tài khoản ngân hàng không hợp lệ"));

        // Kiểm tra database lệnh rút tiền
        YeuCauRutTien updated = yeuCauRutTienRepository.findById(testPendingPayout.getId()).orElseThrow();
        assertEquals(TrangThaiRutTien.TU_CHOI, updated.getTrangThai());
        assertNotNull(updated.getNgayXuLy());

        // Kiểm tra số dư ví giảng viên được hoàn lại 1,000,000 đ
        HoSoGiangVien updatedGv = hoSoGiangVienRepository.findById(testGiangVien.getId()).orElseThrow();
        assertEquals(0, soDuBanDau.add(new BigDecimal("1000000")).compareTo(updatedGv.getSoDuVi()));

        // Kiểm tra sổ cái LichSuViTien được ghi nhận HOAN_TIEN
        boolean existsRefund = lichSuViTienRepository.findByGiangVienIdOrderByNgayTaoDesc(testGiangVien.getId())
                .stream().anyMatch(ls -> ls.getLoaiGiaoDich() == LoaiGiaoDichVi.HOAN_TIEN);
        assertTrue(existsRefund);
    }

    @Test
    @DisplayName("5. Chặn truy cập khi chưa xác thực JWT - Trả về 401 Unauthorized")
    void testChanTruyCapChuaXacThuc() throws Exception {
        mockMvc.perform(get("/api/admin/tai-chinh/yeu-cau-rut-tien"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("6. Chặn truy cập khi không có quyền Quản trị (ROLE_HOC_VIEN) - Trả về 403 Forbidden")
    void testChanTruyCapHocVien() throws Exception {
        String studentToken = layStudentToken();
        mockMvc.perform(get("/api/admin/tai-chinh/yeu-cau-rut-tien")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }
}

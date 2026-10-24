package com.studywithme;

import com.jayway.jsonpath.JsonPath;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class TeacherKycIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        this.nguoiDungRepository.findByEmail("admin@studywithme.vn").ifPresent(admin -> {
            admin.setMatKhau(this.passwordEncoder.encode("Admin@123456"));
            admin.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);
            this.nguoiDungRepository.save(admin);
        });
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

    @Test
    @DisplayName("1. Lấy danh sách hồ sơ gia sư chờ duyệt qua REST API")
    void testLayDanhSachChoDuyet() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/giang-vien/yeu-cau-duyet?trangThai=CHO_DUYET")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu", notNullValue()));
    }

    @Test
    @DisplayName("2. Lấy thống kê hồ sơ gia sư KYC")
    void testLayThongKeKyc() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/giang-vien/thong-ke")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.tongCong", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.duLieu.daDuyet", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("3. Phê duyệt hồ sơ gia sư và cấp quyền ROLE_GIANG_VIEN")
    void testPheDuyetHoSoGiangVien() throws Exception {
        String token = layAdminToken();

        // Tìm một hồ sơ đang CHO_DUYET
        List<HoSoGiangVien> listChoDuyet = hoSoGiangVienRepository.findByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.CHO_DUYET);
        if (!listChoDuyet.isEmpty()) {
            HoSoGiangVien target = listChoDuyet.get(0);
            Long tutorId = target.getId();

            mockMvc.perform(post("/api/admin/giang-vien/" + tutorId + "/phe-duyet")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu.trangThaiDuyet").value("DA_DUYET"));

            HoSoGiangVien updated = hoSoGiangVienRepository.findById(tutorId).orElseThrow();
            assertEquals(TrangThaiGiangVien.DA_DUYET, updated.getTrangThaiDuyet());

            // Kiểm tra người dùng đã có quyền ROLE_GIANG_VIEN
            var user = nguoiDungRepository.findByEmail(updated.getNguoiDung().getEmail()).orElseThrow();
            boolean hasRole = user.getDanhSachVaiTro().stream()
                    .anyMatch(r -> "ROLE_GIANG_VIEN".equals(r.getTenVaiTro().name()));
            assertTrue(hasRole, "Tài khoản người dùng phải được cấp quyền ROLE_GIANG_VIEN sau khi duyệt");
        }
    }

    @Test
    @DisplayName("4. Từ chối hồ sơ gia sư kèm lưu lý do")
    void testTuChoiHoSoGiangVien() throws Exception {
        String token = layAdminToken();

        List<HoSoGiangVien> all = hoSoGiangVienRepository.findAll();
        if (!all.isEmpty()) {
            HoSoGiangVien target = all.get(all.size() - 1);
            Long tutorId = target.getId();

            String lyDoJson = "{\"lyDo\":\"Bằng cấp sư phạm chưa đạt chuẩn kiểm định theo yêu cầu sàn.\"}";

            mockMvc.perform(post("/api/admin/giang-vien/" + tutorId + "/tu-choi")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lyDoJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu.trangThaiDuyet").value("TU_CHOI"))
                    .andExpect(jsonPath("$.duLieu.lyDoTuChoi").value("Bằng cấp sư phạm chưa đạt chuẩn kiểm định theo yêu cầu sàn."));
        }
    }
}

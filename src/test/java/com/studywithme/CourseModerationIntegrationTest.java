package com.studywithme;

import com.jayway.jsonpath.JsonPath;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.repository.KhoaHocRepository;
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
class CourseModerationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

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
    @DisplayName("1. Lấy thống kê kiểm duyệt khóa học")
    void testLayThongKeKiemDuyet() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/khoa-hoc/thong-ke")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.tongCong", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.duLieu.daXuatBan", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("2. Lấy danh sách khóa học chờ duyệt qua REST API")
    void testLayDanhSachChoDuyet() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/khoa-hoc/danh-sach?trangThai=CHO_DUYET")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu", notNullValue()));
    }

    @Test
    @DisplayName("3. Xem chi tiết đề cương & bài học để kiểm duyệt")
    void testLayChiTietKhoaHoc() throws Exception {
        String token = layAdminToken();
        List<KhoaHoc> list = khoaHocRepository.findAll();
        assertTrue(!list.isEmpty(), "Cần có ít nhất 1 khóa học mẫu");
        Long courseId = list.get(0).getId();

        mockMvc.perform(get("/api/admin/khoa-hoc/" + courseId + "/chi-tiet")
                        .header("Authorization", "Bearer " + token))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.id").value(courseId))
                .andExpect(jsonPath("$.duLieu.tieuDe").isNotEmpty())
                .andExpect(jsonPath("$.duLieu.danhSachChuongHoc", notNullValue()));
    }

    @Test
    @DisplayName("4. Phê duyệt khóa học chuyển sang DA_XUAT_BAN")
    void testPheDuyetKhoaHoc() throws Exception {
        String token = layAdminToken();
        List<KhoaHoc> list = khoaHocRepository.findByTrangThaiOrderByNgayTaoDesc(TrangThaiKhoaHoc.CHO_DUYET);
        if (list.isEmpty()) {
            list = khoaHocRepository.findAll();
        }
        Long courseId = list.get(0).getId();

        mockMvc.perform(post("/api/admin/khoa-hoc/" + courseId + "/phe-duyet")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_XUAT_BAN"));

        KhoaHoc updated = khoaHocRepository.findById(courseId).orElseThrow();
        assertEquals(TrangThaiKhoaHoc.DA_XUAT_BAN, updated.getTrangThai());
    }

    @Test
    @DisplayName("5. Từ chối khóa học kèm lý do lưu vào CSDL")
    void testTuChoiKhoaHoc() throws Exception {
        String token = layAdminToken();
        List<KhoaHoc> list = khoaHocRepository.findAll();
        Long courseId = list.get(list.size() - 1).getId();

        String rejectJson = "{\"lyDo\":\"Video chất lượng 360p mờ, âm thanh micro có tiếng ồn.\"}";

        mockMvc.perform(post("/api/admin/khoa-hoc/" + courseId + "/tu-choi")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rejectJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("TU_CHOI"));

        KhoaHoc updated = khoaHocRepository.findById(courseId).orElseThrow();
        assertEquals(TrangThaiKhoaHoc.TU_CHOI, updated.getTrangThai());
        assertEquals("Video chất lượng 360p mờ, âm thanh micro có tiếng ồn.", updated.getLyDoTuChoi());
    }

    @Test
    @DisplayName("6. Chặn người dùng chưa xác thực truy cập API kiểm duyệt (401/403)")
    void testChanTruyCapChuaXacThuc() throws Exception {
        mockMvc.perform(get("/api/admin/khoa-hoc/thong-ke"))
                .andExpect(status().isUnauthorized());
    }
}

package com.studywithme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studywithme.dto.request.DangKyRequest;
import com.studywithme.dto.request.DangNhapRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthValidationLimitsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("1. Đăng ký thành công với dữ liệu chuẩn độ dài")
    void testDangKyThanhCong_ChuanDoDai() throws Exception {
        DangKyRequest req = DangKyRequest.builder()
                .hoTen("Trần Minh Quân")
                .email("quan.tm@test.vn")
                .matKhau("Quan@123456")
                .soDienThoai("0988776655")
                .dangKyLamGiangVien(false)
                .build();

        mockMvc.perform(post("/api/auth/dang-ky")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true));
    }

    @Test
    @DisplayName("2. Đăng ký thất bại khi họ tên quá 50 ký tự")
    void testDangKyThatBai_HoTenQuaDai() throws Exception {
        String longName = "Nguyễn Hoàng Minh Đức Triều Gia Bảo Long Vũ Khang Huy Thịnh"; // > 50 ký tự
        DangKyRequest req = DangKyRequest.builder()
                .hoTen(longName)
                .email("valid@test.vn")
                .matKhau("123456")
                .soDienThoai("0912345678")
                .build();

        mockMvc.perform(post("/api/auth/dang-ky")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("50 ký tự")));
    }

    @Test
    @DisplayName("3. Đăng ký thất bại khi email quá 50 ký tự")
    void testDangKyThatBai_EmailQuaDai() throws Exception {
        String longEmail = "nguyen.van.a.hoc.vien.chuyen.can.thpt.quoc.gia@domain.com"; // 57 ký tự
        DangKyRequest req = DangKyRequest.builder()
                .hoTen("Nguyễn Văn An")
                .email(longEmail)
                .matKhau("123456")
                .build();

        mockMvc.perform(post("/api/auth/dang-ky")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.duLieu.email", containsString("50 ký tự")));
    }

    @Test
    @DisplayName("4. Đăng ký thất bại khi mật khẩu quá 32 ký tự")
    void testDangKyThatBai_MatKhauQuaDai() throws Exception {
        String longPass = "p".repeat(33); // 33 ký tự
        DangKyRequest req = DangKyRequest.builder()
                .hoTen("Nguyễn Văn An")
                .email("an.nv@test.vn")
                .matKhau(longPass)
                .build();

        mockMvc.perform(post("/api/auth/dang-ky")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("32 ký tự")));
    }

    @Test
    @DisplayName("5. Đăng ký thất bại khi mật khẩu quá ngắn (< 6 ký tự)")
    void testDangKyThatBai_MatKhauQuaNgan() throws Exception {
        DangKyRequest req = DangKyRequest.builder()
                .hoTen("Nguyễn Văn An")
                .email("an.nv@test.vn")
                .matKhau("12345") // 5 ký tự
                .build();

        mockMvc.perform(post("/api/auth/dang-ky")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false))
                .andExpect(jsonPath("$.thongBao", containsString("6")));
    }

    @Test
    @DisplayName("6. Đăng ký thất bại khi số điện thoại không đúng chuẩn 10-11 số")
    void testDangKyThatBai_SoDienThoaiKhongHopLe() throws Exception {
        DangKyRequest req = DangKyRequest.builder()
                .hoTen("Nguyễn Văn An")
                .email("an.nv@test.vn")
                .matKhau("123456")
                .soDienThoai("09123456789999") // 14 số
                .build();

        mockMvc.perform(post("/api/auth/dang-ky")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thanhCong").value(false));
    }

    @Test
    @DisplayName("7. Đăng nhập thất bại khi email vượt quá 50 ký tự")
    void testDangNhapThatBai_EmailQuaDai() throws Exception {
        String longEmail = "very_long_test_email_address_that_exceeds_the_limit@example.org";
        DangNhapRequest req = DangNhapRequest.builder()
                .email(longEmail)
                .matKhau("123456")
                .build();

        mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thongBao", containsString("50 ký tự")));
    }

    @Test
    @DisplayName("8. Đăng nhập thất bại khi mật khẩu vượt quá 32 ký tự")
    void testDangNhapThatBai_MatKhauQuaDai() throws Exception {
        DangNhapRequest req = DangNhapRequest.builder()
                .email("admin@studywithme.vn")
                .matKhau("m".repeat(35))
                .build();

        mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thongBao", containsString("32 ký tự")));
    }
}


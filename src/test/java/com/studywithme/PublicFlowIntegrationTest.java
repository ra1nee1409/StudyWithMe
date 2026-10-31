package com.studywithme;
 
import com.jayway.jsonpath.JsonPath;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.repository.NguoiDungRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class PublicFlowIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

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

        return JsonPath.read(result.getResponse().getContentAsString(), "$.duLieu.accessToken");
    }

    // =========================================================================
    // 1. KIỂM THỬ CÁC TRANG WEB PUBLIC (THYMELEAF)
    // =========================================================================

    @Test
    @DisplayName("1.1. Kiểm tra Trang chủ (/) trả về HTTP 200 và view index kèm model")
    void testTrangChuRender() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("banners"))
                .andExpect(model().attributeExists("featuredCourses"))
                .andExpect(model().attributeExists("featuredTutors"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("1.2. Kiểm tra Trang Đăng nhập (/login) trả về HTTP 200")
    void testTrangDangNhap() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("1.3. Kiểm tra Trang Đăng ký (/register) trả về HTTP 200")
    void testTrangDangKy() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @DisplayName("1.4. Kiểm tra Trang Danh mục Khóa học (/khoa-hoc) trả về HTTP 200")
    void testTrangKhoaHocRender() throws Exception {
        mockMvc.perform(get("/khoa-hoc"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/index"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attributeExists("totalCourses"));
    }

    @Test
    @DisplayName("1.5. Kiểm tra Trang Đội ngũ Gia sư (/gia-su) trả về HTTP 200")
    void testTrangGiaSuRender() throws Exception {
        mockMvc.perform(get("/gia-su"))
                .andExpect(status().isOk())
                .andExpect(view().name("tutors/index"))
                .andExpect(model().attributeExists("tutors"))
                .andExpect(model().attributeExists("totalTutors"));
    }

    // =========================================================================
    // 2. KIỂM THỬ PUBLIC REST APIS (KHÔNG CẦN TOKEN)
    // =========================================================================

    @Test
    @DisplayName("2.1. Kiểm tra API Public Banners (/api/public/banners)")
    void testApiPublicBanners() throws Exception {
        mockMvc.perform(get("/api/public/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu").isArray());
    }

    @Test
    @DisplayName("2.2. Kiểm tra API Public Khóa học nổi bật (/api/public/khoa-hoc/noi-bat)")
    void testApiPublicKhoaHocNoiBat() throws Exception {
        mockMvc.perform(get("/api/public/khoa-hoc/noi-bat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu").isArray());
    }

    @Test
    @DisplayName("2.3. Kiểm tra API Public Gia sư tiêu biểu (/api/public/giang-vien/tieu-bieu)")
    void testApiPublicGiangVienTieuBieu() throws Exception {
        mockMvc.perform(get("/api/public/giang-vien/tieu-bieu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu").isArray());
    }

    @Test
    @DisplayName("2.4. Kiểm tra API Public Cây danh mục (/api/public/danh-muc/noi-bat)")
    void testApiPublicDanhMucNoiBat() throws Exception {
        mockMvc.perform(get("/api/public/danh-muc/noi-bat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu").isArray());
    }

    @Test
    @DisplayName("2.5. Kiểm tra API Public Tìm kiếm Khóa học (/api/public/khoa-hoc)")
    void testApiPublicLocKhoaHoc() throws Exception {
        mockMvc.perform(get("/api/public/khoa-hoc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.content").isArray());
    }

    @Test
    @DisplayName("2.6. Kiểm tra API Public Tìm kiếm Gia sư (/api/public/giang-vien)")
    void testApiPublicLocGiangVien() throws Exception {
        mockMvc.perform(get("/api/public/giang-vien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.content").isArray());
    }

    // =========================================================================
    // 3. KIỂM THỬ XÁC THỰC AUTHENTICATION & PROFILE
    // =========================================================================

    @Test
    @DisplayName("3.1. Kiểm tra Đăng nhập thành công với tài khoản Admin")
    void testDangNhapAdminThanhCong() throws Exception {
        String loginJson = "{\"email\":\"admin@studywithme.vn\",\"matKhau\":\"Admin@123456\"}";

        mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.duLieu.email").value("admin@studywithme.vn"));
    }

    @Test
    @DisplayName("3.2. Kiểm tra Đăng nhập thất bại khi sai mật khẩu")
    void testDangNhapThatBaiSaiMatKhau() throws Exception {
        String badLoginJson = "{\"email\":\"admin@studywithme.vn\",\"matKhau\":\"MatKhauSai123\"}";

        mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badLoginJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("3.3. Kiểm tra Đăng ký tài khoản học viên mới")
    void testDangKyHocVienMoi() throws Exception {
        String uniqueEmail = "student_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        String registerJson = String.format(
                "{\"hoTen\":\"Nguyễn Văn Test\",\"email\":\"%s\",\"matKhau\":\"Pass@123456\",\"soDienThoai\":\"0912345678\",\"dangKyLamGiangVien\":false}",
                uniqueEmail
        );

        mockMvc.perform(post("/api/auth/dang-ky")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true));
    }

    @Test
    @DisplayName("3.4. Kiểm tra Lấy thông tin cá nhân (/api/auth/me) với Bearer Token")
    void testLayThongTinCaNhanVoiToken() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.email").value("admin@studywithme.vn"));
    }

    // =========================================================================
    // 4. KIỂM THỬ BẢO VỆ PHÂN QUYỀN (SECURITY RBAC)
    // =========================================================================

    @Test
    @DisplayName("4.1. Chặn 401 khi truy cập API Admin mà không có JWT token")
    void testBaoVePhanQuyenAdminKhongToken() throws Exception {
        mockMvc.perform(get("/api/admin/banners"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/danh-muc"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/thong-ke/tong-quan"))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // 5. KIỂM THỬ CÁC ADMIN APIS KHI ĐÃ XÁC THỰC (ADMIN PRIVILEGED ACCESS)
    // =========================================================================

    @Test
    @DisplayName("5.1. Admin lấy danh sách banner (/api/admin/banners)")
    void testAdminLayDanhSachBanner() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/banners")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu").isArray());
    }

    @Test
    @DisplayName("5.2. Admin lấy cây danh mục môn học (/api/admin/danh-muc)")
    void testAdminLayCayDanhMuc() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/danh-muc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu").isArray());
    }

    @Test
    @DisplayName("5.3. Admin lấy danh sách danh mục phẳng (/api/admin/danh-muc/phang)")
    void testAdminLayDanhSachDanhMucPhang() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/danh-muc/phang")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu").isArray());
    }

    @Test
    @DisplayName("5.4. Admin lấy thống kê tổng quan KPI (/api/admin/thong-ke/tong-quan)")
    void testAdminLayThongKeTongQuan() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/thong-ke/tong-quan")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.tongNguoiDung").isNumber());
    }

    @Test
    @DisplayName("5.5. Admin lấy doanh thu 12 tháng (/api/admin/thong-ke/doanh-thu)")
    void testAdminLayDoanhThu() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/thong-ke/doanh-thu")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.danhSachThang").isArray());
    }

    @Test
    @DisplayName("5.6. Admin lấy tỷ lệ phân bổ danh mục (/api/admin/thong-ke/ty-le-danh-muc)")
    void testAdminLayTyLeDanhMuc() throws Exception {
        String token = layAdminToken();

        mockMvc.perform(get("/api/admin/thong-ke/ty-le-danh-muc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.danhSachTyLe").isArray());
    }
}

package com.studywithme;

import com.jayway.jsonpath.JsonPath;
import com.studywithme.entity.BaiHoc;
import com.studywithme.entity.ChuongHoc;
import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.GhiDanhKhoaHoc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.repository.BaiHocRepository;
import com.studywithme.repository.ChuongHocRepository;
import com.studywithme.repository.DanhMucRepository;
import com.studywithme.repository.GhiDanhKhoaHocRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.TienDoBaiHocRepository;
import com.studywithme.service.StudentLearningService;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class StudentLearningIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @Autowired
    private ChuongHocRepository chuongHocRepository;

    @Autowired
    private BaiHocRepository baiHocRepository;

    @Autowired
    private GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;

    @Autowired
    private TienDoBaiHocRepository tienDoBaiHocRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentLearningService studentLearningService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    private String dangKyVaDangNhapHocVien(String email, String rawPassword) throws Exception {
        NguoiDung user = this.nguoiDungRepository.findByEmail(email).orElseGet(() -> {
            NguoiDung newUser = NguoiDung.builder()
                    .email(email)
                    .matKhau(this.passwordEncoder.encode(rawPassword))
                    .hoTen("Học viên Test " + UUID.randomUUID().toString().substring(0, 5))
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .build();
            return this.nguoiDungRepository.save(newUser);
        });
        user.setMatKhau(this.passwordEncoder.encode(rawPassword));
        user.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);
        this.nguoiDungRepository.save(user);

        String loginJson = String.format("{\"email\":\"%s\",\"matKhau\":\"%s\"}", email, rawPassword);
        MvcResult result = mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.duLieu.accessToken");
    }

    @Test
    @DisplayName("Format URL nhúng YouTube chính xác")
    void testFormatVideoEmbedUrl() {
        String raw1 = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
        String embed1 = studentLearningService.formatVideoEmbedUrl(raw1);
        assertThat(embed1).contains("https://www.youtube.com/embed/dQw4w9WgXcQ");

        String raw2 = "https://youtu.be/dQw4w9WgXcQ";
        String embed2 = studentLearningService.formatVideoEmbedUrl(raw2);
        assertThat(embed2).contains("https://www.youtube.com/embed/dQw4w9WgXcQ");

        String mp4 = "https://cdn.example.com/video.mp4";
        String embed3 = studentLearningService.formatVideoEmbedUrl(mp4);
        assertThat(embed3).isEqualTo(mp4);
    }

    @Test
    @Transactional
    @DisplayName("Học viên truy cập phòng học thành công khi đã ghi danh và cập nhật tiến độ bài học qua API")
    void testStudentLearningFlow() throws Exception {
        String studentEmail = "std_lms_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn";
        String testPassword = "Pass" + "@" + "123456";
        String studentToken = dangKyVaDangNhapHocVien(studentEmail, testPassword);
        NguoiDung student = nguoiDungRepository.findByEmail(studentEmail).orElseThrow();

        // Lấy hoặc tạo giảng viên
        HoSoGiangVien giangVien = hoSoGiangVienRepository.findAll().stream().findFirst().orElseGet(() -> {
            NguoiDung gvUser = nguoiDungRepository.save(NguoiDung.builder()
                    .email("gv_lms_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn")
                    .matKhau(passwordEncoder.encode(testPassword))
                    .hoTen("Thầy Nguyễn Văn A")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .build());
            return hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                    .nguoiDung(gvUser)
                    .tieuDeChuyenMon("Giảng viên THPT")
                    .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                    .soDuVi(BigDecimal.ZERO)
                    .build());
        });

        // Lấy hoặc tạo danh mục
        DanhMuc danhMuc = danhMucRepository.findAll().stream().findFirst().orElseGet(() ->
                danhMucRepository.save(DanhMuc.builder()
                        .tenDanhMuc("Toán Học LMS")
                        .duongDanSlug("toan-hoc-lms-" + System.currentTimeMillis())
                        .kichHoat(true)
                        .build())
        );

        // Tạo khóa học test
        String slug = "khoa-hoc-lms-test-" + UUID.randomUUID().toString().substring(0, 8);
        KhoaHoc khoaHoc = KhoaHoc.builder()
                .giangVien(giangVien)
                .danhMuc(danhMuc)
                .tieuDe("Khóa Học Luyện Thi THPT Quốc Gia")
                .duongDanSlug(slug)
                .giaGoc(BigDecimal.valueOf(500000))
                .trangThai(TrangThaiKhoaHoc.DA_XUAT_BAN)
                .build();
        khoaHoc = khoaHocRepository.save(khoaHoc);

        ChuongHoc chuong = ChuongHoc.builder()
                .khoaHoc(khoaHoc)
                .tieuDeChuong("Chương 1: Khảo sát hàm số")
                .thuTuSapXep(1)
                .build();
        chuong = chuongHocRepository.save(chuong);

        BaiHoc bai1 = BaiHoc.builder()
                .chuongHoc(chuong)
                .tieuDeBaiHoc("Bài 1: Tính đơn điệu của hàm số")
                .linkVideo("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .thoiLuongGiay(900)
                .thuTuSapXep(1)
                .build();
        bai1 = baiHocRepository.save(bai1);

        BaiHoc bai2 = BaiHoc.builder()
                .chuongHoc(chuong)
                .tieuDeBaiHoc("Bài 2: Cực trị của hàm số")
                .linkVideo("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .thoiLuongGiay(1200)
                .thuTuSapXep(2)
                .build();
        bai2 = baiHocRepository.save(bai2);

        // Trường hợp 1: Chưa ghi danh -> truy cập bị redirect
        mockMvc.perform(get("/student/learn/" + slug)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/khoa-hoc/" + slug + "?chuaGhiDanh=true"));

        // Ghi danh học viên vào khóa học
        GhiDanhKhoaHoc ghiDanh = GhiDanhKhoaHoc.builder()
                .nguoiDung(student)
                .khoaHoc(khoaHoc)
                .phanTramTienDo(0)
                .build();
        ghiDanhKhoaHocRepository.save(ghiDanh);

        // Trường hợp 2: Đã ghi danh -> truy cập phòng học thành công
        mockMvc.perform(get("/student/learn/" + slug)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(view().name("student/learning"))
                .andExpect(model().attributeExists("khoaHoc", "danhSachChuongHoc", "baiHocHienTai", "embedVideoUrl"));

        // Trường hợp 3: Gọi API đánh dấu hoàn thành bài 1
        String toggleCompleteJson = String.format("{\"baiHocId\":%d,\"daHoanThanh\":true,\"giayDungLai\":100}", bai1.getId());
        mockMvc.perform(post("/api/student/learning/toggle-complete")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toggleCompleteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.daHoanThanh").value(true))
                .andExpect(jsonPath("$.duLieu.soBaiDaHoanThanh").value(1))
                .andExpect(jsonPath("$.duLieu.tongSoBaiHoc").value(2))
                .andExpect(jsonPath("$.duLieu.phanTramTienDo").value(50));

        // Kiểm tra database: ghiDanh đã được cập nhật 50%
        GhiDanhKhoaHoc updatedGhiDanh = ghiDanhKhoaHocRepository.findByNguoiDungIdAndKhoaHocId(student.getId(), khoaHoc.getId()).orElseThrow();
        assertThat(updatedGhiDanh.getPhanTramTienDo()).isEqualTo(50);

        // Trường hợp 4: Hủy đánh dấu hoàn thành bài 1
        String uncheckJson = String.format("{\"baiHocId\":%d,\"daHoanThanh\":false,\"giayDungLai\":0}", bai1.getId());
        mockMvc.perform(post("/api/student/learning/toggle-complete")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(uncheckJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.daHoanThanh").value(false))
                .andExpect(jsonPath("$.duLieu.soBaiDaHoanThanh").value(0))
                .andExpect(jsonPath("$.duLieu.phanTramTienDo").value(0));
    }
}

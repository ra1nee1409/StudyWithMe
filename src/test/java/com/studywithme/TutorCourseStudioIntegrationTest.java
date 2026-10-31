package com.studywithme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.studywithme.dto.tutor.CapNhatKhoaHocRequest;
import com.studywithme.dto.tutor.TaoBaiHocRequest;
import com.studywithme.dto.tutor.TaoChuongHocRequest;
import com.studywithme.dto.tutor.TaoKhoaHocRequest;
import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.giaovien.TrinhDoKhoaHoc;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.*;
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
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class TutorCourseStudioIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @Autowired
    private ChuongHocRepository chuongHocRepository;

    @Autowired
    private BaiHocRepository baiHocRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    private final String tutorEmail = "tutor_studio_test@studywithme.vn";
    private final String studentEmail = "student_studio_test@studywithme.vn";
    private final String rawPassword = "Password@123";

    private Long testDanhMucId;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        // 1. Đảm bảo vai trò tồn tại
        VaiTro roleGiangVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_GIANG_VIEN).moTa("Gia sư").build()));
        VaiTro roleHocVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).moTa("Học viên").build()));

        // 2. Tạo hoặc reset Giảng viên Test
        NguoiDung tutor = nguoiDungRepository.findByEmail(tutorEmail).orElse(null);
        if (tutor == null) {
            tutor = NguoiDung.builder()
                    .email(tutorEmail)
                    .matKhau(passwordEncoder.encode(rawPassword))
                    .hoTen("Thầy Giáo Studio Test")
                    .soDienThoai("0977889901")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(new HashSet<>(Set.of(roleGiangVien)))
                    .build();
            tutor = nguoiDungRepository.save(tutor);
        } else {
            tutor.setMatKhau(passwordEncoder.encode(rawPassword));
            tutor.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);
            tutor.setDanhSachVaiTro(new HashSet<>(Set.of(roleGiangVien)));
            tutor = nguoiDungRepository.save(tutor);
        }

        // Tạo HoSoGiangVien nếu chưa có
        if (hoSoGiangVienRepository.findByNguoiDungId(tutor.getId()).isEmpty()) {
            hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                    .nguoiDung(tutor)
                    .tieuDeChuyenMon("Chuyên gia Luyện Thi Toán")
                    .gioiThieuBanThan("10 năm kinh nghiệm dạy online")
                    .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                    .build());
        }

        // 3. Tạo hoặc reset Học viên Test
        NguoiDung student = nguoiDungRepository.findByEmail(studentEmail).orElse(null);
        if (student == null) {
            student = NguoiDung.builder()
                    .email(studentEmail)
                    .matKhau(passwordEncoder.encode(rawPassword))
                    .hoTen("Học Sinh Test Studio")
                    .soDienThoai("0977889902")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(new HashSet<>(Set.of(roleHocVien)))
                    .build();
            nguoiDungRepository.save(student);
        } else {
            student.setMatKhau(passwordEncoder.encode(rawPassword));
            student.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);
            student.setDanhSachVaiTro(new HashSet<>(Set.of(roleHocVien)));
            nguoiDungRepository.save(student);
        }

        // 4. Đảm bảo có ít nhất 1 danh mục
        DanhMuc danhMuc = danhMucRepository.findAll().stream().findFirst().orElseGet(() ->
                danhMucRepository.save(DanhMuc.builder()
                        .tenDanhMuc("Toán Học Studio")
                        .duongDanSlug("toan-hoc-studio")
                        .thuTuHienThi(1)
                        .build())
        );
        this.testDanhMucId = danhMuc.getId();
    }

    private String layToken(String email, String password) throws Exception {
        String loginJson = String.format("{\"email\":\"%s\",\"matKhau\":\"%s\"}", email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return JsonPath.read(responseBody, "$.duLieu.accessToken");
    }

    @Test
    @DisplayName("1. Gia sư tạo khóa học mới thành công - Trạng thái BAN_NHAP")
    void testTaoKhoaHocMoi_ThanhCong() throws Exception {
        String token = layToken(tutorEmail, rawPassword);

        TaoKhoaHocRequest req = TaoKhoaHocRequest.builder()
                .tieuDe("Khóa Học Luyện Đề Toán THPT 2026")
                .danhMucId(testDanhMucId)
                .trinhDo(TrinhDoKhoaHoc.MOI_TRINH_DO)
                .giaGoc(new BigDecimal("799000"))
                .giaKhuyenMai(new BigDecimal("499000"))
                .thePhanLoai("Lớp 12")
                .anhDaiDienKhoaHoc("https://example.com/math.jpg")
                .moTaChiTiet("Khóa học đầy đủ kiến thức 12.")
                .build();

        MvcResult result = mockMvc.perform(post("/api/giang-vien/khoa-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.tieuDe").value("Khóa Học Luyện Đề Toán THPT 2026"))
                .andExpect(jsonPath("$.data.trangThai").value("BAN_NHAP"))
                .andExpect(jsonPath("$.data.duongDanSlug", not(emptyOrNullString())))
                .andReturn();

        String resp = result.getResponse().getContentAsString();
        Number id = JsonPath.read(resp, "$.data.id");
        assertNotNull(id);
    }

    @Test
    @DisplayName("2. Thêm chương học và bài học video vào khóa học")
    void testThemChuongHocVaBaiHoc_ThanhCong() throws Exception {
        String token = layToken(tutorEmail, rawPassword);

        // Tạo khóa học trước
        TaoKhoaHocRequest khReq = TaoKhoaHocRequest.builder()
                .tieuDe("Khóa Học Hình Học Không Gian")
                .danhMucId(testDanhMucId)
                .giaGoc(new BigDecimal("500000"))
                .build();

        MvcResult khResult = mockMvc.perform(post("/api/giang-vien/khoa-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(khReq)))
                .andExpect(status().isOk())
                .andReturn();

        Number khId = JsonPath.read(khResult.getResponse().getContentAsString(), "$.data.id");

        // Thêm chương học
        TaoChuongHocRequest chapReq = TaoChuongHocRequest.builder()
                .tieuDeChuong("Chương 1: Khối đa diện và thể tích")
                .thuTuSapXep(1)
                .build();

        MvcResult chapResult = mockMvc.perform(post("/api/giang-vien/khoa-hoc/" + khId + "/chuong-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chapReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.tieuDeChuong").value("Chương 1: Khối đa diện và thể tích"))
                .andReturn();

        Number chapId = JsonPath.read(chapResult.getResponse().getContentAsString(), "$.data.id");

        // Thêm bài học video
        TaoBaiHocRequest lesReq = TaoBaiHocRequest.builder()
                .tieuDeBaiHoc("Bài 1: Khái niệm về khối đa diện")
                .linkVideo("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .thoiLuongGiay(900)
                .thuTuSapXep(1)
                .choPhepHocThu(true)
                .build();

        mockMvc.perform(post("/api/giang-vien/khoa-hoc/chuong-hoc/" + chapId + "/bai-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lesReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.tieuDeBaiHoc").value("Bài 1: Khái niệm về khối đa diện"))
                .andExpect(jsonPath("$.data.choPhepHocThu").value(true));
    }

    @Test
    @DisplayName("3. Gửi duyệt khóa học thất bại nếu chưa có chương hoặc bài học")
    void testGuiDuyetKhoaHoc_ThatBai_KhiChuaCoBaiHoc() throws Exception {
        String token = layToken(tutorEmail, rawPassword);

        // Tạo khóa học rỗng
        TaoKhoaHocRequest khReq = TaoKhoaHocRequest.builder()
                .tieuDe("Khóa Học Rỗng Chưa Soạn Bài")
                .danhMucId(testDanhMucId)
                .giaGoc(new BigDecimal("300000"))
                .build();

        MvcResult khResult = mockMvc.perform(post("/api/giang-vien/khoa-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(khReq)))
                .andExpect(status().isOk())
                .andReturn();

        Number khId = JsonPath.read(khResult.getResponse().getContentAsString(), "$.data.id");

        // Gửi duyệt khóa học rỗng -> mong đợi lỗi 400 Bad Request
        mockMvc.perform(post("/api/giang-vien/khoa-hoc/" + khId + "/gui-duyet")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("ít nhất 1 chương học")));
    }

    @Test
    @DisplayName("4. Gửi duyệt khóa học thành công khi đã đủ điều kiện chương & bài học")
    void testGuiDuyetKhoaHoc_ThanhCong_KhiDuDieuKien() throws Exception {
        String token = layToken(tutorEmail, rawPassword);

        // 1. Tạo khóa học
        TaoKhoaHocRequest khReq = TaoKhoaHocRequest.builder()
                .tieuDe("Khóa Học Hoàn Chỉnh Chuẩn Bị Gửi Duyệt")
                .danhMucId(testDanhMucId)
                .giaGoc(new BigDecimal("450000"))
                .build();

        MvcResult khResult = mockMvc.perform(post("/api/giang-vien/khoa-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(khReq)))
                .andExpect(status().isOk())
                .andReturn();

        Number khId = JsonPath.read(khResult.getResponse().getContentAsString(), "$.data.id");

        // 2. Thêm chương
        TaoChuongHocRequest chapReq = TaoChuongHocRequest.builder()
                .tieuDeChuong("Chương 1: Mở đầu")
                .thuTuSapXep(1)
                .build();

        MvcResult chapResult = mockMvc.perform(post("/api/giang-vien/khoa-hoc/" + khId + "/chuong-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chapReq)))
                .andExpect(status().isOk())
                .andReturn();

        Number chapId = JsonPath.read(chapResult.getResponse().getContentAsString(), "$.data.id");

        // 3. Thêm bài học
        TaoBaiHocRequest lesReq = TaoBaiHocRequest.builder()
                .tieuDeBaiHoc("Bài 1: Giới thiệu khóa học")
                .linkVideo("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .thoiLuongGiay(600)
                .thuTuSapXep(1)
                .choPhepHocThu(true)
                .build();

        mockMvc.perform(post("/api/giang-vien/khoa-hoc/chuong-hoc/" + chapId + "/bai-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lesReq)))
                .andExpect(status().isOk());

        // 4. Gửi duyệt -> Mong đợi 200 OK và trạng thái là CHO_DUYET
        mockMvc.perform(post("/api/giang-vien/khoa-hoc/" + khId + "/gui-duyet")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.trangThai").value("CHO_DUYET"));
    }

    @Test
    @DisplayName("5. Kiểm tra phân quyền: Học viên bị chặn gọi API gia sư (403 Forbidden)")
    void testBaoMat_HocVienKhongTheGoiApiTutor() throws Exception {
        String studentToken = layToken(studentEmail, rawPassword);

        TaoKhoaHocRequest khReq = TaoKhoaHocRequest.builder()
                .tieuDe("Học Viên Cố Tình Tạo Khóa Học")
                .danhMucId(testDanhMucId)
                .giaGoc(new BigDecimal("100000"))
                .build();

        mockMvc.perform(post("/api/giang-vien/khoa-hoc")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(khReq)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("6. Lấy chi tiết khóa học kèm đề cương chương/bài học")
    void testLayChiTietKhoaHoc() throws Exception {
        String token = layToken(tutorEmail, rawPassword);

        TaoKhoaHocRequest khReq = TaoKhoaHocRequest.builder()
                .tieuDe("Khóa Học Xem Chi Tiết")
                .danhMucId(testDanhMucId)
                .giaGoc(new BigDecimal("350000"))
                .build();

        MvcResult khResult = mockMvc.perform(post("/api/giang-vien/khoa-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(khReq)))
                .andExpect(status().isOk())
                .andReturn();

        Number khId = JsonPath.read(khResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/giang-vien/khoa-hoc/" + khId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.tieuDe").value("Khóa Học Xem Chi Tiết"));
    }

    @Test
    @DisplayName("7. Cập nhật và Xóa khóa học bản nháp")
    void testCapNhatVaXoaKhoaHoc() throws Exception {
        String token = layToken(tutorEmail, rawPassword);

        // 1. Tạo khóa học
        TaoKhoaHocRequest createReq = TaoKhoaHocRequest.builder()
                .tieuDe("Khóa Học Chuẩn Bị Cập Nhật")
                .danhMucId(testDanhMucId)
                .giaGoc(new BigDecimal("200000"))
                .build();

        MvcResult khResult = mockMvc.perform(post("/api/giang-vien/khoa-hoc")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isOk())
                .andReturn();

        Number khId = JsonPath.read(khResult.getResponse().getContentAsString(), "$.data.id");

        // 2. Cập nhật khóa học
        CapNhatKhoaHocRequest updateReq = CapNhatKhoaHocRequest.builder()
                .tieuDe("Khóa Học Đã Cập Nhật Tiêu Đề Mới")
                .danhMucId(testDanhMucId)
                .giaGoc(new BigDecimal("299000"))
                .trinhDo(TrinhDoKhoaHoc.NANG_CAO)
                .build();

        mockMvc.perform(put("/api/giang-vien/khoa-hoc/" + khId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.tieuDe").value("Khóa Học Đã Cập Nhật Tiêu Đề Mới"));

        // 3. Xóa khóa học
        mockMvc.perform(delete("/api/giang-vien/khoa-hoc/" + khId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}

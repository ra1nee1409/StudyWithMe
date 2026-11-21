package com.studywithme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studywithme.dto.tutor.DatCaHoc1k1Request;
import com.studywithme.dto.tutor.XacNhanCaHocRequest;
import com.studywithme.entity.*;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.*;
import com.studywithme.service.DatLich1k1Service;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class StudentCaHocAndPaymentIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DatLich1k1Service datLich1k1Service;

    @Autowired
    private DatLich1k1Repository datLich1k1Repository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    private final PasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;
    private NguoiDung studentUser;
    private NguoiDung tutorUser;
    private HoSoGiangVien tutorProfile;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        VaiTro roleStudent = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseGet(() -> vaiTroRepository.save(new VaiTro(null, VaiTroName.ROLE_HOC_VIEN, "Học viên")));

        VaiTro roleTutor = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                .orElseGet(() -> vaiTroRepository.save(new VaiTro(null, VaiTroName.ROLE_GIANG_VIEN, "Giảng viên")));

        String studentEmail = "student_test_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        studentUser = NguoiDung.builder()
                .email(studentEmail)
                .matKhau(passwordEncoder.encode("123456"))
                .hoTen("Học Viên Test 1k1")
                .soDienThoai("0988111222")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        studentUser.getDanhSachVaiTro().add(roleStudent);
        studentUser = nguoiDungRepository.save(studentUser);

        String tutorEmail = "tutor_test_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        tutorUser = NguoiDung.builder()
                .email(tutorEmail)
                .matKhau(passwordEncoder.encode("123456"))
                .hoTen("Gia Sư Test 1k1")
                .soDienThoai("0977333444")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        tutorUser.getDanhSachVaiTro().add(roleTutor);
        tutorUser = nguoiDungRepository.save(tutorUser);

        tutorProfile = HoSoGiangVien.builder()
                .nguoiDung(tutorUser)
                .tieuDeChuyenMon("Thạc sĩ Sư Phạm Toán")
                .gioiThieuBanThan("10 năm kinh nghiệm dạy kèm 1-1")
                .soDuVi(BigDecimal.ZERO)
                .build();
        tutorProfile = hoSoGiangVienRepository.save(tutorProfile);
    }

    @Test
    @DisplayName("WBS 3.1: Học viên xem danh sách ca học kèm 1-1 của mình")
    void test1_layDanhSachCaHocCuaHocVien() throws Exception {
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(2).withHour(19).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(2).withHour(21).withMinute(0))
                .hocPhiCaHoc(BigDecimal.valueOf(250000))
                .ghiChuHocVien("Ôn tập kiến thức lớp 12")
                .build();
        datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);

        mockMvc.perform(get("/api/hoc-vien/dat-lich-1k1")
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu[0].tenGiangVien").value("Gia Sư Test 1k1"))
                .andExpect(jsonPath("$.duLieu[0].hocPhiCaHoc").value(250000))
                .andExpect(jsonPath("$.duLieu[0].trangThai").value("CHO_XAC_NHAN"));
    }

    @Test
    @DisplayName("WBS 3.1: Học viên hủy ca học kèm 1-1 đang chờ duyệt")
    void test2_hocVienHuyCaHoc() throws Exception {
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(3).withHour(14).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(3).withHour(16).withMinute(0))
                .hocPhiCaHoc(BigDecimal.valueOf(200000))
                .ghiChuHocVien("Luyện đề chuyên")
                .build();
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);

        mockMvc.perform(put("/api/hoc-vien/dat-lich-1k1/" + caHoc.getId() + "/huy")
                        .param("lyDoHuy", "Em bận lịch thi học kỳ")
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_HUY"))
                .andExpect(jsonPath("$.duLieu.lyDoHuy").value("Em bận lịch thi học kỳ"));

        DatLich1k1 updated = datLich1k1Repository.findById(caHoc.getId()).orElseThrow();
        assertEquals(TrangThaiDatLich.DA_HUY, updated.getTrangThai());
    }

    @Test
    @DisplayName("WBS 5.1: Học viên tạo liên kết PayOS thanh toán ký quỹ cho ca học")
    void test3_taoLinkThanhToanPayOs() throws Exception {
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(4).withHour(18).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(4).withHour(20).withMinute(0))
                .hocPhiCaHoc(BigDecimal.valueOf(300000))
                .build();
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);

        // Gia sư chấp nhận ca
        datLich1k1Service.giaSuPhanHoiCaHoc(tutorProfile.getId(), caHoc.getId(), new XacNhanCaHocRequest(true, null));

        mockMvc.perform(post("/api/hoc-vien/dat-lich-1k1/" + caHoc.getId() + "/thanh-toan-payos")
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.checkoutUrl").exists());
    }

    @Test
    @DisplayName("WBS 5.1: Học viên thanh toán ký quỹ ngay và gia sư hoàn tất giải ngân ví")
    void test4_thanhToanKyQuyVaGiaiNgan() throws Exception {
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(2).withHour(8).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                .hocPhiCaHoc(BigDecimal.valueOf(400000))
                .build();
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);

        // Gia sư chấp nhận
        datLich1k1Service.giaSuPhanHoiCaHoc(tutorProfile.getId(), caHoc.getId(), new XacNhanCaHocRequest(true, null));

        // Học viên thanh toán ký quỹ ngay
        mockMvc.perform(post("/api/hoc-vien/dat-lich-1k1/" + caHoc.getId() + "/thanh-toan-ky-quy-ngay")
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_THANH_TOAN_KY_QUY"));

        DatLich1k1 escrowed = datLich1k1Repository.findById(caHoc.getId()).orElseThrow();
        assertEquals(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY, escrowed.getTrangThai());

        // Gia sư hoàn tất ca học -> Giải ngân ví gia sư
        DatLich1k1 completed = datLich1k1Service.hoanTatCaHoc(tutorProfile.getId(), caHoc.getId());
        assertEquals(TrangThaiDatLich.HOAN_THANH, completed.getTrangThai());

        HoSoGiangVien updatedTutor = hoSoGiangVienRepository.findById(tutorProfile.getId()).orElseThrow();
        assertTrue(updatedTutor.getSoDuVi().compareTo(BigDecimal.ZERO) > 0, "Ví gia sư phải được cộng tiền sau khi hoàn tất ca học!");
    }

    @Test
    @DisplayName("WBS 3.3: Học viên gửi đánh giá 5 sao cho ca học hoàn tất")
    void test5_danhGiaCaHoc() throws Exception {
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(1).withHour(15).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(1).withHour(17).withMinute(0))
                .hocPhiCaHoc(BigDecimal.valueOf(250000))
                .build();
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);
        datLich1k1Service.giaSuPhanHoiCaHoc(tutorProfile.getId(), caHoc.getId(), new XacNhanCaHocRequest(true, null));
        datLich1k1Service.xacNhanThanhToanKyQuy(caHoc.getId());
        datLich1k1Service.hoanTatCaHoc(tutorProfile.getId(), caHoc.getId());

        mockMvc.perform(post("/api/hoc-vien/dat-lich-1k1/" + caHoc.getId() + "/danh-gia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "soSao", 5,
                                "nhanXet", "Thầy dạy nhiệt tình, chỉ bảo tận tâm!"
                        )))
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.danhGiaSao").value(5))
                .andExpect(jsonPath("$.duLieu.nhanXetHocVien").value("Thầy dạy nhiệt tình, chỉ bảo tận tâm!"));
    }

    @Test
    @DisplayName("WBS 3.1: Giao diện Web /student/ca-hoc tải thành công (HTTP 200)")
    void test6_webTrangCaHocHocVien() throws Exception {
        mockMvc.perform(get("/student/ca-hoc")
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN")))
                .andExpect(status().isOk())
                .andExpect(view().name("student/ca-hoc"))
                .andExpect(model().attributeExists("danhSachCaHoc"))
                .andExpect(model().attributeExists("tongCaHoc"));
    }

    @Test
    @DisplayName("WBS 5.1: PayOS callback xử lý ca học 1-1 và chuyển hướng về /student/ca-hoc")
    void test7_payosCallback1k1() throws Exception {
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(2).withHour(12).withMinute(0))
                .hocPhiCaHoc(BigDecimal.valueOf(350000))
                .build();
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);

        mockMvc.perform(get("/payment/payos-callback")
                        .param("type", "1k1")
                        .param("id", caHoc.getId().toString())
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/ca-hoc"))
                .andExpect(flash().attributeExists("successMessage"));

        DatLich1k1 updated = datLich1k1Repository.findById(caHoc.getId()).orElseThrow();
        assertEquals(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY, updated.getTrangThai());
    }

    @Test
    @DisplayName("WBS 1.2: Học viên hủy ca học sau khi đã ký quỹ (>2 giờ) -> Tự động hoàn 100% học phí vào ví")
    void test8_hocVienHuyCaHocSauKhiKyQuy_HoanTienVaoVi() throws Exception {
        BigDecimal hocPhi = BigDecimal.valueOf(300000);
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(2).withHour(16).withMinute(0))
                .hocPhiCaHoc(hocPhi)
                .build();
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);
        datLich1k1Service.xacNhanThanhToanKyQuy(caHoc.getId());

        BigDecimal soDuBanDau = studentUser.getSoDuVi();

        mockMvc.perform(put("/api/hoc-vien/dat-lich-1k1/" + caHoc.getId() + "/huy")
                        .param("lyDoHuy", "Em có việc gia đình đột xuất")
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_HUY"));

        NguoiDung studentAfter = nguoiDungRepository.findById(studentUser.getId()).orElseThrow();
        assertEquals(soDuBanDau.add(hocPhi), studentAfter.getSoDuVi(), "Số dư ví học viên phải được hoàn lại đúng 100% số tiền ca học!");
    }

    @Test
    @DisplayName("WBS 1.2: Gia sư hủy ca học sau khi học viên đã ký quỹ -> Tự động hoàn tiền vào ví học viên")
    void test9_giaSuHuyCaHocSauKhiKyQuy_HoanTienVaoVi() throws Exception {
        BigDecimal hocPhi = BigDecimal.valueOf(400000);
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(3).withHour(12).withMinute(0))
                .hocPhiCaHoc(hocPhi)
                .build();
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);
        datLich1k1Service.xacNhanThanhToanKyQuy(caHoc.getId());

        BigDecimal soDuBanDau = studentUser.getSoDuVi();

        mockMvc.perform(post("/api/tutor/ca-hoc/" + caHoc.getId() + "/huy")
                        .param("lyDoHuy", "Gia sư bận lịch công tác")
                        .with(user(tutorUser.getEmail()).roles("GIANG_VIEN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_HUY"));

        NguoiDung studentAfter = nguoiDungRepository.findById(studentUser.getId()).orElseThrow();
        assertEquals(soDuBanDau.add(hocPhi), studentAfter.getSoDuVi(), "Ví học viên phải nhận lại tiền khi gia sư hủy ca!");
    }

    @Test
    @DisplayName("WBS 1.2: Học viên thanh toán ký quỹ ngay và tự động trừ số dư ví nếu ví đủ tiền")
    void test10_hocVienThanhToanKyQuyNgay_TruSoDuVi() throws Exception {
        // Cấp 500.000 VNĐ vào ví học viên
        studentUser.setSoDuVi(BigDecimal.valueOf(500000));
        nguoiDungRepository.save(studentUser);

        BigDecimal hocPhi = BigDecimal.valueOf(300000);
        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(LocalDateTime.now().plusDays(1).withHour(19).withMinute(0))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(1).withHour(21).withMinute(0))
                .hocPhiCaHoc(hocPhi)
                .build();
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);
        datLich1k1Service.giaSuPhanHoiCaHoc(tutorProfile.getId(), caHoc.getId(), new XacNhanCaHocRequest(true, null));

        mockMvc.perform(post("/api/hoc-vien/dat-lich-1k1/" + caHoc.getId() + "/thanh-toan-ky-quy-ngay")
                        .with(user(studentUser.getEmail()).roles("HOC_VIEN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.trangThai").value("DA_THANH_TOAN_KY_QUY"));

        NguoiDung studentAfter = nguoiDungRepository.findById(studentUser.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(200000), studentAfter.getSoDuVi(), "Số dư ví học viên phải trừ đúng 300.000 VNĐ (500.000 - 300.000 = 200.000)!");
    }
}

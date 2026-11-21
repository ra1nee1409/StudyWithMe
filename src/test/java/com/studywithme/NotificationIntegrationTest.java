package com.studywithme;

import com.studywithme.dto.notification.ThongBaoDto;
import com.studywithme.dto.tutor.DatCaHoc1k1Request;
import com.studywithme.dto.tutor.XacNhanCaHocRequest;
import com.studywithme.entity.DatLich1k1;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.shared.LoaiThongBao;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.ThongBaoRepository;
import com.studywithme.repository.VaiTroRepository;
import com.studywithme.service.DatLich1k1Service;
import com.studywithme.service.ThongBaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GÓI WBS 7.0: KIỂM THỬ TÍCH HỢP HỆ THỐNG THÔNG BÁO TỨC THỜI (IN-APP
 * NOTIFICATIONS)
 */
@SpringBootTest
@Transactional
class NotificationIntegrationTest {

        @Autowired
        private WebApplicationContext context;

        @Autowired
        private ThongBaoService thongBaoService;

        @Autowired
        private ThongBaoRepository thongBaoRepository;

        @Autowired
        private NguoiDungRepository nguoiDungRepository;

        @Autowired
        private HoSoGiangVienRepository hoSoGiangVienRepository;

        @Autowired
        private DatLich1k1Service datLich1k1Service;

        @Autowired
        private VaiTroRepository vaiTroRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private MockMvc mockMvc;

        private NguoiDung testStudent;
        private NguoiDung testTutorUser;
        private HoSoGiangVien testTutorProfile;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                VaiTro roleHv = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                                .orElseGet(() -> vaiTroRepository
                                                .save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).build()));

                VaiTro roleGv = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                                .orElseGet(() -> vaiTroRepository
                                                .save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_GIANG_VIEN).build()));

                String uid = UUID.randomUUID().toString().substring(0, 8);

                testStudent = nguoiDungRepository.save(NguoiDung.builder()
                                .email("notif.student." + uid + "@studywithme.test")
                                .matKhau(passwordEncoder.encode("Pass@123456"))
                                .hoTen("Học Viên Nhận Thông Báo")
                                .soDienThoai("0971234567")
                                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                                .danhSachVaiTro(new HashSet<>(Collections.singletonList(roleHv)))
                                .build());

                testTutorUser = nguoiDungRepository.save(NguoiDung.builder()
                                .email("notif.tutor." + uid + "@studywithme.test")
                                .matKhau(passwordEncoder.encode("Pass@123456"))
                                .hoTen("Gia Sư Nhận Thông Báo")
                                .soDienThoai("0979876543")
                                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                                .danhSachVaiTro(new HashSet<>(Collections.singletonList(roleGv)))
                                .build());

                testTutorProfile = hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                                .nguoiDung(testTutorUser)
                                .tieuDeChuyenMon("Thạc sĩ Sư Phạm Toán")
                                .gioiThieuBanThan("5 năm kinh nghiệm dạy kèm 1-1")
                                .soDuVi(BigDecimal.ZERO)
                                .build());
        }

        @Test
        @DisplayName("WBS 7.1 & 7.3: Tạo và truy vấn thông báo In-app qua Service")
        void testCreateAndRetrieveNotifications() {
                // Tạo 2 thông báo cho học viên
                ThongBaoDto tb1 = thongBaoService.taoThongBao(
                                testStudent.getId(),
                                "Chào mừng thành viên mới",
                                "Cảm ơn bạn đã tham gia nền tảng StudyWithMe!",
                                LoaiThongBao.HE_THONG,
                                "/student/dashboard");
                assertNotNull(tb1.getId());
                assertEquals("Chào mừng thành viên mới", tb1.getTieuDe());
                assertFalse(tb1.getDaDoc());

                ThongBaoDto tb2 = thongBaoService.taoThongBao(
                                testStudent.getId(),
                                "Gia sư đã duyệt ca học 1-1",
                                "Ca học Toán của bạn đã được duyệt. Vui lòng thanh toán ký quỹ.",
                                LoaiThongBao.DUYET_CA_HOC,
                                "/student/ca-hoc");
                assertNotNull(tb2.getId());

                // Kiểm tra danh sách thông báo
                List<ThongBaoDto> list = thongBaoService.layDanhSachThongBao(testStudent.getId(), 10);
                assertEquals(2, list.size());
                assertEquals(2, thongBaoService.demSoChuaDoc(testStudent.getId()));
        }

        @Test
        @DisplayName("WBS 7.2: Gọi REST API kiểm tra đếm chưa đọc, đánh dấu đã đọc và đánh dấu tất cả đã đọc")
        void testNotificationApiEndpoints() throws Exception {
                // Tạo 3 thông báo cho học viên
                thongBaoService.taoThongBao(testStudent.getId(), "TB 1", "Nội dung 1", LoaiThongBao.HE_THONG, "/");
                ThongBaoDto tb2 = thongBaoService.taoThongBao(testStudent.getId(), "TB 2", "Nội dung 2",
                                LoaiThongBao.CA_HOC_MOI, "/");
                thongBaoService.taoThongBao(testStudent.getId(), "TB 3", "Nội dung 3", LoaiThongBao.KY_QUY_THANH_CONG,
                                "/");

                // 1. GET /api/notifications/unread-count -> 3
                mockMvc.perform(get("/api/notifications/unread-count")
                                .with(user(testStudent.getEmail()).roles("HOC_VIEN")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.thanhCong").value(true))
                                .andExpect(jsonPath("$.duLieu.count").value(3));

                // 2. GET /api/notifications?limit=5 -> size 3
                mockMvc.perform(get("/api/notifications")
                                .param("limit", "5")
                                .with(user(testStudent.getEmail()).roles("HOC_VIEN")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.thanhCong").value(true))
                                .andExpect(jsonPath("$.duLieu.length()").value(3));

                // 3. POST /api/notifications/{id}/read -> Đánh dấu 1 cái đã đọc
                mockMvc.perform(post("/api/notifications/" + tb2.getId() + "/read")
                                .with(csrf())
                                .with(user(testStudent.getEmail()).roles("HOC_VIEN")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.thanhCong").value(true))
                                .andExpect(jsonPath("$.duLieu").value(true));

                assertEquals(2, thongBaoService.demSoChuaDoc(testStudent.getId()));

                // 4. POST /api/notifications/read-all -> Đánh dấu tất cả đã đọc
                mockMvc.perform(post("/api/notifications/read-all")
                                .with(csrf())
                                .with(user(testStudent.getEmail()).roles("HOC_VIEN")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.thanhCong").value(true));

                assertEquals(0, thongBaoService.demSoChuaDoc(testStudent.getId()));
        }

        @Test
        @DisplayName("WBS 7.3: Luồng nghiệp vụ ca học 1-1 tự động kích hoạt thông báo In-app cho gia sư và học viên")
        void testBookingAndApprovalBusinessNotifications() {
                // 1. Học viên đặt ca học kèm 1-1
                DatCaHoc1k1Request datReq = DatCaHoc1k1Request.builder()
                                .giangVienId(testTutorProfile.getId())
                                .thoiGianBatDau(LocalDateTime.now().plusDays(2).withHour(19).withMinute(0))
                                .thoiGianKetThuc(LocalDateTime.now().plusDays(2).withHour(20).withMinute(30))
                                .hocPhiCaHoc(new BigDecimal("250000"))
                                .ghiChuHocVien("Em muốn học kèm Casio nâng cao")
                                .build();

                DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(testStudent.getId(), datReq);
                assertNotNull(caHoc);

                // Gia sư phải nhận được thông báo CA_HOC_MOI
                List<ThongBaoDto> tutorNotifs = thongBaoService.layDanhSachThongBao(testTutorUser.getId(), 5);
                assertFalse(tutorNotifs.isEmpty(), "Gia sư phải có thông báo mới khi học viên đặt ca");
                assertEquals(LoaiThongBao.CA_HOC_MOI, tutorNotifs.get(0).getLoaiThongBao());
                assertTrue(tutorNotifs.get(0).getNoiDung().contains(caHoc.getMaDatLich()));

                // 2. Gia sư duyệt ca học
                XacNhanCaHocRequest xnReq = XacNhanCaHocRequest.builder()
                                .chapNhan(true)
                                .build();
                datLich1k1Service.giaSuPhanHoiCaHoc(testTutorProfile.getId(), caHoc.getId(), xnReq);

                // Học viên phải nhận được thông báo DUYET_CA_HOC
                List<ThongBaoDto> studentNotifs = thongBaoService.layDanhSachThongBao(testStudent.getId(), 5);
                assertFalse(studentNotifs.isEmpty(), "Học viên phải có thông báo khi gia sư chấp nhận ca học");
                assertEquals(LoaiThongBao.DUYET_CA_HOC, studentNotifs.get(0).getLoaiThongBao());
        }
}

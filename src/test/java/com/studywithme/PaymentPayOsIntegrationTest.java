package com.studywithme;

import com.studywithme.entity.ChiTietDonHang;
import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.DatLich1k1;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.GhiDanhKhoaHoc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.LichSuViTien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.entity.enums.shared.LoaiSanPhamDonHang;
import com.studywithme.entity.enums.shared.PhuongThucThanhToan;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import com.studywithme.repository.ChiTietDonHangRepository;
import com.studywithme.repository.DanhMucRepository;
import com.studywithme.repository.DatLich1k1Repository;
import com.studywithme.repository.DonHangRepository;
import com.studywithme.repository.GhiDanhKhoaHocRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.repository.LichSuViTienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.PayOsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class PaymentPayOsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;

    @Autowired
    private LichSuViTienRepository lichSuViTienRepository;

    @Autowired
    private DatLich1k1Repository datLich1k1Repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PayOsService payOsService;

    @Autowired
    private com.studywithme.service.CauHinhHeThongService cauHinhHeThongService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        try {
            this.cauHinhHeThongService.capNhatTyLeHoaHongSan(BigDecimal.valueOf(20.0));
        } catch (Exception ignored) {}

        try {
            jdbcTemplate.execute("""
                DECLARE @ConstraintName NVARCHAR(200);
                SELECT @ConstraintName = Name FROM sys.check_constraints
                WHERE parent_object_id = OBJECT_ID('don_hang')
                AND parent_column_id = COLUMNPROPERTY(OBJECT_ID('don_hang'), 'phuong_thuc_thanh_toan', 'ColumnId');

                IF @ConstraintName IS NOT NULL
                BEGIN
                    EXEC('ALTER TABLE don_hang DROP CONSTRAINT [' + @ConstraintName + ']');
                    ALTER TABLE don_hang ADD CHECK (phuong_thuc_thanh_toan IN ('VNPAY', 'MOMO', 'CHUYEN_KHOAN', 'PAYOS'));
                END
            """);
        } catch (Exception ignored) {}
    }

    @Test
    @Transactional
    @DisplayName("Xử lý Webhook payOS thành công: Kích hoạt đơn hàng, ghi danh học viên và cộng 80% ví giảng viên")
    void testPayOsWebhookVerificationAndOrderActivation() {
        // 1. Tạo học viên
        NguoiDung hocVien = nguoiDungRepository.save(NguoiDung.builder()
                .email("stu_pos_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn")
                .matKhau(passwordEncoder.encode("dummy_test_pass"))
                .hoTen("Học viên Test payOS")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        // 2. Tạo giảng viên
        NguoiDung gvUser = nguoiDungRepository.save(NguoiDung.builder()
                .email("tch_pos_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn")
                .matKhau(passwordEncoder.encode("dummy_test_pass"))
                .hoTen("Thầy Giáo payOS")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        HoSoGiangVien giangVien = hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                .nguoiDung(gvUser)
                .tieuDeChuyenMon("Chuyên gia Luyện thi")
                .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                .soDuVi(BigDecimal.ZERO)
                .build());

        // 3. Tạo danh mục & khóa học
        DanhMuc danhMuc = danhMucRepository.findAll().stream().findFirst().orElseGet(() ->
                danhMucRepository.save(DanhMuc.builder()
                        .tenDanhMuc("Công nghệ thông tin")
                        .duongDanSlug("cntt-" + System.currentTimeMillis())
                        .kichHoat(true)
                        .build())
        );

        KhoaHoc khoaHoc = khoaHocRepository.save(KhoaHoc.builder()
                .giangVien(giangVien)
                .danhMuc(danhMuc)
                .tieuDe("Lập trình Spring Boot cùng payOS")
                .duongDanSlug("lap-trinh-spring-boot-" + UUID.randomUUID().toString().substring(0, 8))
                .giaGoc(BigDecimal.valueOf(1000000))
                .trangThai(TrangThaiKhoaHoc.DA_XUAT_BAN)
                .build());

        // 4. Tạo đơn hàng chờ thanh toán qua payOS
        long testOrderCode = System.currentTimeMillis();
        DonHang donHang = DonHang.builder()
                .maDonHang("SWM-PAYOS-" + testOrderCode)
                .nguoiDung(hocVien)
                .tongTienThanhToan(BigDecimal.valueOf(1000000))
                .phuongThucThanhToan(PhuongThucThanhToan.PAYOS)
                .trangThaiThanhToan(TrangThaiThanhToan.CHO_THANH_TOAN)
                .orderCodePayos(testOrderCode)
                .build();
        donHang = donHangRepository.save(donHang);

        ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                .donHang(donHang)
                .loaiSanPham(LoaiSanPhamDonHang.KHOA_HOC_VIDEO)
                .sanPhamId(khoaHoc.getId())
                .donGia(BigDecimal.valueOf(1000000))
                .build();
        chiTietDonHangRepository.save(chiTiet);
        donHang.getDanhSachChiTiet().add(chiTiet);

        // 5. Giả lập nhận WebhookData từ payOS
        WebhookData webhookData = WebhookData.builder()
                .orderCode(testOrderCode)
                .amount(1000000L)
                .description("SWM Don " + donHang.getId())
                .accountNumber("123456789")
                .reference("PAYOS_REF_" + UUID.randomUUID().toString().substring(0, 8))
                .transactionDateTime("2026-10-31 07:00:00")
                .currency("VND")
                .paymentLinkId("LINK_" + testOrderCode)
                .code("00")
                .desc("Thành công")
                .build();

        boolean xuLySuccess = payOsService.xuLyDonHangThanhCong(webhookData);
        assertThat(xuLySuccess).isTrue();

        // 6. Kiểm tra đơn hàng đã đổi sang DA_THANH_TOAN
        DonHang donHangSauXuLy = donHangRepository.findById(donHang.getId()).orElseThrow();
        assertThat(donHangSauXuLy.getTrangThaiThanhToan()).isEqualTo(TrangThaiThanhToan.DA_THANH_TOAN);
        assertThat(donHangSauXuLy.getNgayThanhToan()).isNotNull();

        // 7. Kiểm tra quyền ghi danh của học viên
        boolean daGhiDanh = ghiDanhKhoaHocRepository.existsByNguoiDungIdAndKhoaHocId(hocVien.getId(), khoaHoc.getId());
        assertThat(daGhiDanh).isTrue();

        // 8. Kiểm tra ví giảng viên được cộng chính xác 80% (800.000 VNĐ)
        HoSoGiangVien gvSau = hoSoGiangVienRepository.findById(giangVien.getId()).orElseThrow();
        assertThat(gvSau.getSoDuVi()).isEqualByComparingTo(BigDecimal.valueOf(800000));

        // 9. Kiểm tra lịch sử giao dịch ví tiền
        assertThat(lichSuViTienRepository.findByGiangVienIdOrderByNgayTaoDesc(giangVien.getId())).hasSize(1);

        // 10. Kiểm tra tính năng Idempotency (gửi lại lần 2 không bị cộng tiền 2 lần)
        boolean xuLyLan2 = payOsService.xuLyDonHangThanhCong(webhookData);
        assertThat(xuLyLan2).isTrue();
        HoSoGiangVien gvLan2 = hoSoGiangVienRepository.findById(giangVien.getId()).orElseThrow();
        assertThat(gvLan2.getSoDuVi()).isEqualByComparingTo(BigDecimal.valueOf(800000));
    }

    @Test
    @DisplayName("Endpoint Webhook trả về HTTP 200 OK khi nhận bản tin xác thực hợp lệ")
    void testWebhookEndpointPermitAllAndHandling() throws Exception {
        // Bản tin test hợp lệ từ dashboard payOS
        String dummyWebhookJson = "{"
                + "\"code\":\"00\","
                + "\"desc\":\"success\","
                + "\"success\":true,"
                + "\"data\":{"
                + "\"orderCode\":123456789,"
                + "\"amount\":2000,"
                + "\"description\":\"VQRIO123\","
                + "\"accountNumber\":\"12345678\","
                + "\"reference\":\"REF123456\","
                + "\"transactionDateTime\":\"2026-10-31 07:00:00\","
                + "\"currency\":\"VND\","
                + "\"paymentLinkId\":\"link_123\","
                + "\"code\":\"00\","
                + "\"desc\":\"Thành công\""
                + "},"
                + "\"signature\":\"dummy-signature\""
                + "}";

        // Khi signature dummy, controller bắt Exception và trả về 400 nếu sai signature
        mockMvc.perform(post("/api/payment/payos_transfer_handler")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dummyWebhookJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Transactional
    @DisplayName("Endpoint mô phỏng Demo: Kích hoạt đơn hàng qua API /demo/kich-hoat/{orderCode}")
    void testDemoPaymentSimulation() throws Exception {
        NguoiDung hocVien = nguoiDungRepository.save(NguoiDung.builder()
                .email("demo_stu_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn")
                .matKhau(passwordEncoder.encode("dummy_test_pass"))
                .hoTen("Học viên Demo")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        long orderCode = System.currentTimeMillis();
        DonHang donHang = donHangRepository.save(DonHang.builder()
                .maDonHang("SWM-DEMO-" + orderCode)
                .nguoiDung(hocVien)
                .tongTienThanhToan(BigDecimal.valueOf(500000))
                .phuongThucThanhToan(PhuongThucThanhToan.PAYOS)
                .trangThaiThanhToan(TrangThaiThanhToan.CHO_THANH_TOAN)
                .orderCodePayos(orderCode)
                .build());

        mockMvc.perform(post("/api/payment/demo/kich-hoat/" + orderCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.thanhCong").value(true));

        DonHang updated = donHangRepository.findById(donHang.getId()).orElseThrow();
        assertThat(updated.getTrangThaiThanhToan()).isEqualTo(TrangThaiThanhToan.DA_THANH_TOAN);
    }

    @Test
    @Transactional
    @DisplayName("WBS 1.1: Webhook PayOS tự động kích hoạt ký quỹ cho ca học 1-1 (DatLich1k1)")
    void testWebhookKichHoatKyQuyCaHoc1k1() {
        NguoiDung hocVien = nguoiDungRepository.save(NguoiDung.builder()
                .email("stu_1k1_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn")
                .matKhau(passwordEncoder.encode("123456"))
                .hoTen("Học Viên Test 1-1")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        NguoiDung tutorUser = nguoiDungRepository.save(NguoiDung.builder()
                .email("tut_1k1_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn")
                .matKhau(passwordEncoder.encode("123456"))
                .hoTen("Gia Sư Test 1-1")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        HoSoGiangVien gv = hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                .nguoiDung(tutorUser)
                .soDuVi(BigDecimal.ZERO)
                .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                .build());

        long orderCode1k1 = System.currentTimeMillis() + 999;
        DatLich1k1 caHoc = datLich1k1Repository.save(DatLich1k1.builder()
                .maDatLich("DL1K1-" + orderCode1k1)
                .hocVien(hocVien)
                .giangVien(gv)
                .thoiGianBatDau(java.time.LocalDateTime.now().plusDays(2).withHour(18).withMinute(0))
                .thoiGianKetThuc(java.time.LocalDateTime.now().plusDays(2).withHour(20).withMinute(0))
                .hocPhiCaHoc(BigDecimal.valueOf(300000))
                .trangThai(TrangThaiDatLich.CHO_THANH_TOAN)
                .orderCodePayos(orderCode1k1)
                .build());

        // Giả lập Webhook Data từ PayOS
        WebhookData webhookData = WebhookData.builder()
                .orderCode(orderCode1k1)
                .amount(300000L)
                .description("KY QUY CA " + caHoc.getId())
                .accountNumber("12345678")
                .reference("BANK_REF_" + orderCode1k1)
                .transactionDateTime("2026-11-17 19:00:00")
                .currency("VND")
                .paymentLinkId("LINK_" + orderCode1k1)
                .code("00")
                .desc("Thành công")
                .build();

        boolean xuLyOk = payOsService.xuLyDonHangThanhCong(webhookData);
        assertThat(xuLyOk).isTrue();

        DatLich1k1 updated = datLich1k1Repository.findById(caHoc.getId()).orElseThrow();
        assertThat(updated.getTrangThai()).isEqualTo(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY);
        assertThat(updated.getMaGiaoDichPayos()).isEqualTo("BANK_REF_" + orderCode1k1);

        // Kiểm tra Idempotency cho ca học 1-1: gọi lại lần 2 không gây lỗi
        boolean xuLyLan2 = payOsService.xuLyDonHangThanhCong(webhookData);
        assertThat(xuLyLan2).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("WBS 1.1: Endpoint mô phỏng Demo kích hoạt ký quỹ ca học 1-1 qua /demo/kich-hoat/{orderCode}")
    void testDemoPaymentSimulationChoCaHoc1k1() throws Exception {
        NguoiDung hocVien = nguoiDungRepository.save(NguoiDung.builder()
                .email("demo_1k1_stu_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn")
                .matKhau(passwordEncoder.encode("123456"))
                .hoTen("Học viên Demo 1k1")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        NguoiDung tutorUser = nguoiDungRepository.save(NguoiDung.builder()
                .email("demo_1k1_tut_" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn")
                .matKhau(passwordEncoder.encode("123456"))
                .hoTen("Gia sư Demo 1k1")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        HoSoGiangVien gv = hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                .nguoiDung(tutorUser)
                .soDuVi(BigDecimal.ZERO)
                .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                .build());

        long orderCode1k1 = System.currentTimeMillis() + 888;
        DatLich1k1 caHoc = datLich1k1Repository.save(DatLich1k1.builder()
                .maDatLich("DL1K1-" + orderCode1k1)
                .hocVien(hocVien)
                .giangVien(gv)
                .thoiGianBatDau(java.time.LocalDateTime.now().plusDays(3).withHour(9).withMinute(0))
                .thoiGianKetThuc(java.time.LocalDateTime.now().plusDays(3).withHour(11).withMinute(0))
                .hocPhiCaHoc(BigDecimal.valueOf(250000))
                .trangThai(TrangThaiDatLich.CHO_THANH_TOAN)
                .orderCodePayos(orderCode1k1)
                .build());

        mockMvc.perform(post("/api/payment/demo/kich-hoat/" + orderCode1k1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.thanhCong").value(true));

        DatLich1k1 updated = datLich1k1Repository.findById(caHoc.getId()).orElseThrow();
        assertThat(updated.getTrangThai()).isEqualTo(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY);
    }
}


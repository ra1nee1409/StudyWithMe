package com.studywithme;

import com.studywithme.config.VnpayConfig;
import com.studywithme.dto.cart.CheckoutRequest;
import com.studywithme.dto.cart.PaymentResultDto;
import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import com.studywithme.repository.*;
import com.studywithme.service.PaymentService;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@Transactional
class PaymentVnPayIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VnpayConfig vnpayConfig;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;

    @Autowired
    private LichSuViTienRepository lichSuViTienRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private NguoiDung testHocVien;
    private HoSoGiangVien testGiangVien;
    private KhoaHoc testKhoaHoc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        // 1. Tạo học viên test
        this.testHocVien = this.nguoiDungRepository.save(
                NguoiDung.builder()
                        .email("student.test." + System.currentTimeMillis() + "@studywithme.vn")
                        .matKhau(this.passwordEncoder.encode("Student@123456"))
                        .hoTen("Học Viên Test VNPAY")
                        .soDienThoai("0988776655")
                        .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                        .build()
        );

        // 2. Tạo giảng viên test
        NguoiDung userGv = this.nguoiDungRepository.save(
                NguoiDung.builder()
                        .email("teacher.test." + System.currentTimeMillis() + "@studywithme.vn")
                        .matKhau(this.passwordEncoder.encode("Teacher@123456"))
                        .hoTen("Thầy Giáo Test VNPAY")
                        .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                        .build()
        );

        this.testGiangVien = this.hoSoGiangVienRepository.save(
                HoSoGiangVien.builder()
                        .nguoiDung(userGv)
                        .tieuDeChuyenMon("Chuyên gia Luyện thi Đại học")
                        .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                        .soDuVi(BigDecimal.ZERO)
                        .build()
        );

        // 3. Lấy hoặc tạo danh mục
        DanhMuc danhMuc = this.danhMucRepository.findAll().stream().findFirst().orElseGet(() ->
                this.danhMucRepository.save(
                        DanhMuc.builder()
                                .tenDanhMuc("Toán Học THPT")
                                .duongDanSlug("toan-hoc-thpt-" + System.currentTimeMillis())
                                .kichHoat(true)
                                .build()
                )
        );

        // 4. Tạo khóa học test
        this.testKhoaHoc = this.khoaHocRepository.save(
                KhoaHoc.builder()
                        .giangVien(this.testGiangVien)
                        .danhMuc(danhMuc)
                        .tieuDe("Khóa Học Toán Lớp 12 Test VNPAY")
                        .duongDanSlug("toan-12-test-vnpay-" + System.currentTimeMillis())
                        .giaGoc(new BigDecimal("500000"))
                        .giaKhuyenMai(new BigDecimal("500000"))
                        .trangThai(TrangThaiKhoaHoc.DA_XUAT_BAN)
                        .tongSoHocVien(0)
                        .build()
        );
    }

    @Test
    @DisplayName("1. Tạo đơn hàng và sinh URL thanh toán VNPAY Sandbox có chữ ký bảo mật SHA512")
    void testTaoDonHangVaSinhUrlVnPay() {
        CheckoutRequest request = new CheckoutRequest();
        request.setCourseIds(Collections.singletonList(this.testKhoaHoc.getId()));
        request.setPaymentMethod("VNPAY");

        Map<String, String> result = this.paymentService.taoDonHangVaLayUrlVnPay(request, this.testHocVien, "127.0.0.1");

        assertNotNull(result);
        String maDonHang = result.get("maDonHang");
        String paymentUrl = result.get("paymentUrl");

        assertNotNull(maDonHang);
        assertTrue(maDonHang.startsWith("SWM-"));
        assertNotNull(paymentUrl);
        assertTrue(paymentUrl.startsWith(this.vnpayConfig.getVnpUrl()));
        assertTrue(paymentUrl.contains("vnp_TmnCode=" + this.vnpayConfig.getVnpTmnCode()));
        assertTrue(paymentUrl.contains("vnp_Amount=50000000")); // Nhân 100 theo quy định VNPAY
        assertTrue(paymentUrl.contains("vnp_SecureHash="));

        // Kiểm tra đơn hàng được lưu trong DB với trạng thái CHO_THANH_TOAN
        DonHang donHang = this.donHangRepository.findByMaDonHang(maDonHang).orElse(null);
        assertNotNull(donHang);
        assertEquals(TrangThaiThanhToan.CHO_THANH_TOAN, donHang.getTrangThaiThanhToan());
        List<ChiTietDonHang> chiTietList = this.chiTietDonHangRepository.findByDonHangId(donHang.getId());
        assertEquals(1, chiTietList.size());
    }

    @Test
    @DisplayName("2. Xử lý VNPAY Callback thành công (00): Kích hoạt khóa học và chia 80% doanh thu vào ví giảng viên")
    void testXuLyCallbackVnPayThanhCong() {
        CheckoutRequest request = new CheckoutRequest();
        request.setCourseIds(Collections.singletonList(this.testKhoaHoc.getId()));
        Map<String, String> taoDonResult = this.paymentService.taoDonHangVaLayUrlVnPay(request, this.testHocVien, "127.0.0.1");
        String maDonHang = taoDonResult.get("maDonHang");

        // Giả lập params VNPAY gửi về sau khi thanh toán thành công
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("vnp_TmnCode", this.vnpayConfig.getVnpTmnCode());
        queryParams.put("vnp_Amount", "50000000");
        queryParams.put("vnp_BankCode", "NCB");
        queryParams.put("vnp_BankTranNo", "VNP14321980");
        queryParams.put("vnp_CardType", "ATM");
        queryParams.put("vnp_OrderInfo", "Thanh toan don hang " + maDonHang);
        queryParams.put("vnp_PayDate", "20261025163000");
        queryParams.put("vnp_ResponseCode", "00");
        queryParams.put("vnp_TransactionNo", "14568923");
        queryParams.put("vnp_TransactionStatus", "00");
        queryParams.put("vnp_TxnRef", maDonHang);

        // Sinh chữ ký bảo mật hợp lệ cho callback test
        String rawHash = buildRawHash(queryParams);
        String secureHash = VnpayConfig.hmacSHA512(this.vnpayConfig.getVnpHashSecret(), rawHash);
        queryParams.put("vnp_SecureHash", secureHash);

        // Gọi xử lý callback
        PaymentResultDto callbackResult = this.paymentService.xuLyVnPayCallback(queryParams);

        assertNotNull(callbackResult);
        assertTrue(callbackResult.isThanhCong());
        assertEquals(maDonHang, callbackResult.getMaDonHang());
        assertEquals("14568923", callbackResult.getMaGiaoDichVnpay());
        assertEquals(new BigDecimal("500000"), callbackResult.getSoTien());

        // Kiểm tra đơn hàng trong DB chuyển sang DA_THANH_TOAN
        DonHang donHang = this.donHangRepository.findByMaDonHang(maDonHang).orElse(null);
        assertNotNull(donHang);
        assertEquals(TrangThaiThanhToan.DA_THANH_TOAN, donHang.getTrangThaiThanhToan());
        assertNotNull(donHang.getNgayThanhToan());

        // Kiểm tra học viên đã được ghi danh
        boolean daGhiDanh = this.ghiDanhKhoaHocRepository.existsByNguoiDungIdAndKhoaHocId(this.testHocVien.getId(), this.testKhoaHoc.getId());
        assertTrue(daGhiDanh, "Học viên phải được ghi danh vào khóa học");

        // Kiểm tra khóa học tăng số lượng học viên
        KhoaHoc khSauThanhToan = this.khoaHocRepository.findById(this.testKhoaHoc.getId()).orElse(null);
        assertNotNull(khSauThanhToan);
        assertEquals(1, khSauThanhToan.getTongSoHocVien());

        // Kiểm tra giảng viên được cộng 80% doanh thu (400,000 đ)
        HoSoGiangVien gvSauThanhToan = this.hoSoGiangVienRepository.findById(this.testGiangVien.getId()).orElse(null);
        assertNotNull(gvSauThanhToan);
        assertEquals(new BigDecimal("400000.00"), gvSauThanhToan.getSoDuVi().setScale(2));

        // Kiểm tra sổ cái ví tiền (LichSuViTien)
        List<LichSuViTien> lichSu = this.lichSuViTienRepository.findByGiangVienIdOrderByNgayTaoDesc(this.testGiangVien.getId());
        assertFalse(lichSu.isEmpty());
        LichSuViTien ls = lichSu.get(0);
        assertEquals(LoaiGiaoDichVi.TIEN_BAN_KHOA_HOC, ls.getLoaiGiaoDich());
        assertEquals(new BigDecimal("400000.00"), ls.getSoTienBienDong().setScale(2));
    }

    @Test
    @DisplayName("3. Xử lý VNPAY Callback thất bại khi khách hủy giao dịch (24)")
    void testXuLyCallbackVnPayHuyGiaoDich() {
        CheckoutRequest request = new CheckoutRequest();
        request.setCourseIds(Collections.singletonList(this.testKhoaHoc.getId()));
        Map<String, String> taoDonResult = this.paymentService.taoDonHangVaLayUrlVnPay(request, this.testHocVien, "127.0.0.1");
        String maDonHang = taoDonResult.get("maDonHang");

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("vnp_TmnCode", this.vnpayConfig.getVnpTmnCode());
        queryParams.put("vnp_Amount", "50000000");
        queryParams.put("vnp_BankCode", "NCB");
        queryParams.put("vnp_OrderInfo", "Thanh toan don hang " + maDonHang);
        queryParams.put("vnp_ResponseCode", "24"); // Khách hủy giao dịch
        queryParams.put("vnp_TxnRef", maDonHang);

        String rawHash = buildRawHash(queryParams);
        String secureHash = VnpayConfig.hmacSHA512(this.vnpayConfig.getVnpHashSecret(), rawHash);
        queryParams.put("vnp_SecureHash", secureHash);

        PaymentResultDto callbackResult = this.paymentService.xuLyVnPayCallback(queryParams);

        assertNotNull(callbackResult);
        assertFalse(callbackResult.isThanhCong());
        assertEquals("24", callbackResult.getMaLoi());

        // Kiểm tra trạng thái đơn hàng là THAT_BAI
        DonHang donHang = this.donHangRepository.findByMaDonHang(maDonHang).orElse(null);
        assertNotNull(donHang);
        assertEquals(TrangThaiThanhToan.THAT_BAI, donHang.getTrangThaiThanhToan());

        // Đảm bảo không ghi danh học viên
        boolean daGhiDanh = this.ghiDanhKhoaHocRepository.existsByNguoiDungIdAndKhoaHocId(this.testHocVien.getId(), this.testKhoaHoc.getId());
        assertFalse(daGhiDanh);
    }

    @Test
    @DisplayName("4. MockMvc kiểm tra truy cập trang Giỏ hàng (/cart)")
    void testTruyCapTrangGioHang() throws Exception {
        this.mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/index"));
    }

    @Test
    @DisplayName("5. MockMvc kiểm tra truy cập trang Thanh toán (/checkout)")
    void testTruyCapTrangCheckout() throws Exception {
        this.mockMvc.perform(get("/checkout").param("courseId", this.testKhoaHoc.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/index"));
    }

    private String buildRawHash(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty() && !"vnp_SecureHash".equals(fieldName) && !"vnp_SecureHashType".equals(fieldName)) {
                sb.append(fieldName);
                sb.append('=');
                sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    sb.append('&');
                }
            }
        }
        return sb.toString();
    }
}

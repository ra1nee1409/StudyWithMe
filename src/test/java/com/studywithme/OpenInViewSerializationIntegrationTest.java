package com.studywithme;

import com.jayway.jsonpath.JsonPath;
import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.giaovien.LoaiChungChi;
import com.studywithme.entity.enums.giaovien.TrangThaiLopHoc;
import com.studywithme.entity.enums.hocvien.LoaiYeuThich;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.entity.enums.hocvien.TrangThaiThamGiaLop;
import com.studywithme.entity.enums.shared.LoaiSanPhamDonHang;
import com.studywithme.entity.enums.shared.PhuongThucThanhToan;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
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
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử không bọc @Transactional để phản ánh đúng hành vi thật khi spring.jpa.open-in-view=false,
 * đảm bảo không còn lỗi LazyInitializationException khi render Thymeleaf hoặc serialize JSON
 * trên dữ liệu thật (khóa học, gia sư, chứng chỉ, lớp trực tuyến, đánh giá, đơn hàng).
 */
@SpringBootTest
class OpenInViewSerializationIntegrationTest {

    @Autowired private WebApplicationContext context;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private VaiTroRepository vaiTroRepository;
    @Autowired private NguoiDungRepository nguoiDungRepository;
    @Autowired private HoSoGiangVienRepository hoSoGiangVienRepository;
    @Autowired private ChungChiGiangVienRepository chungChiGiangVienRepository;
    @Autowired private DanhMucRepository danhMucRepository;
    @Autowired private KhoaHocRepository khoaHocRepository;
    @Autowired private ChuongHocRepository chuongHocRepository;
    @Autowired private BaiHocRepository baiHocRepository;
    @Autowired private LopHocTrucTuyenRepository lopHocTrucTuyenRepository;
    @Autowired private DangKyLopHocRepository dangKyLopHocRepository;
    @Autowired private DanhGiaNhanXetRepository danhGiaNhanXetRepository;
    @Autowired private DonHangRepository donHangRepository;
    @Autowired private ChiTietDonHangRepository chiTietDonHangRepository;
    @Autowired private GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;
    @Autowired private DatLich1k1Repository datLich1k1Repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    private String login(String email, String rawPassword) throws Exception {
        String body = String.format("{\"email\":\"%s\",\"matKhau\":\"%s\"}", email, rawPassword);
        MvcResult res = mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(res.getResponse().getContentAsString(), "$.duLieu.accessToken");
    }

    @Test
    @DisplayName("Open-in-view=false: Toàn bộ Public/Student/Tutor/Admin Web & REST API không bị LazyInitializationException với dữ liệu thật")
    void testAllEndpointsWithRealDataAndOpenInViewFalse() throws Exception {
        String tag = UUID.randomUUID().toString().substring(0, 8);
        String rawPass = "Test@123456";

        VaiTro roleAdmin = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_QUAN_TRI).orElseThrow();
        VaiTro roleTutor = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN).orElseThrow();
        VaiTro roleStudent = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN).orElseThrow();

        NguoiDung adminUser = null;
        NguoiDung tutorUser = null;
        NguoiDung otherTutorUser = null;
        NguoiDung studentUser = null;
        HoSoGiangVien tutorProfile = null;
        HoSoGiangVien otherTutorProfile = null;
        KhoaHoc course = null;
        KhoaHoc freeCourse = null;
        LopHocTrucTuyen liveClass = null;
        DatLich1k1 booking = null;
        DanhGiaNhanXet review = null;
        DonHang order = null;
        GhiDanhKhoaHoc enrollment = null;
        DangKyLopHoc classReg = null;
        DanhMuc rootCat = null;
        DanhMuc childCat = null;

        try {
            rootCat = danhMucRepository.save(DanhMuc.builder()
                    .tenDanhMuc("Nhóm OIV " + tag)
                    .duongDanSlug("nhom-oiv-" + tag)
                    .kichHoat(true)
                    .thuTuHienThi(99)
                    .build());

            childCat = danhMucRepository.save(DanhMuc.builder()
                    .tenDanhMuc("Môn OIV " + tag)
                    .duongDanSlug("mon-oiv-" + tag)
                    .danhMucCha(rootCat)
                    .kichHoat(true)
                    .thuTuHienThi(1)
                    .build());

            adminUser = nguoiDungRepository.save(NguoiDung.builder()
                    .email("oiv_admin_" + tag + "@test.vn")
                    .matKhau(passwordEncoder.encode(rawPass))
                    .hoTen("Admin OIV " + tag)
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(new HashSet<>(Set.of(roleAdmin)))
                    .build());

            tutorUser = nguoiDungRepository.save(NguoiDung.builder()
                    .email("oiv_tutor_" + tag + "@test.vn")
                    .matKhau(passwordEncoder.encode(rawPass))
                    .hoTen("Gia Sư OIV " + tag)
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(new HashSet<>(Set.of(roleTutor, roleStudent)))
                    .build());

            studentUser = nguoiDungRepository.save(NguoiDung.builder()
                    .email("oiv_student_" + tag + "@test.vn")
                    .matKhau(passwordEncoder.encode(rawPass))
                    .hoTen("Học Viên OIV " + tag)
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(new HashSet<>(Set.of(roleStudent)))
                    .build());

            tutorProfile = hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                    .nguoiDung(tutorUser)
                    .tieuDeChuyenMon("Chuyên gia Toán OIV " + tag)
                    .gioiThieuBanThan("Kinh nghiệm 5 năm luyện thi")
                    .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                    .soDuVi(BigDecimal.valueOf(1000000))
                    .build());

            otherTutorUser = nguoiDungRepository.save(NguoiDung.builder()
                    .email("oiv_tutor2_" + tag + "@test.vn")
                    .matKhau(passwordEncoder.encode(rawPass))
                    .hoTen("Gia Sư Khác OIV " + tag)
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(new HashSet<>(Set.of(roleTutor)))
                    .build());

            otherTutorProfile = hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                    .nguoiDung(otherTutorUser)
                    .tieuDeChuyenMon("Gia sư Lý OIV " + tag)
                    .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                    .build());

            chungChiGiangVienRepository.save(ChungChiGiangVien.builder()
                    .giangVien(tutorProfile)
                    .loaiChungChi(LoaiChungChi.IELTS)
                    .tenChungChi("IELTS 8.0 Academic")
                    .linkAnhChungChi("https://example.com/ielts.jpg")
                    .build());

            String slug = "khoa-hoc-oiv-" + tag;
            course = khoaHocRepository.save(KhoaHoc.builder()
                    .giangVien(tutorProfile)
                    .danhMuc(childCat)
                    .tieuDe("Khóa Học OIV " + tag)
                    .duongDanSlug(slug)
                    .giaGoc(BigDecimal.valueOf(450000))
                    .trangThai(TrangThaiKhoaHoc.DA_XUAT_BAN)
                    .build());

            ChuongHoc chuong = chuongHocRepository.save(ChuongHoc.builder()
                    .khoaHoc(course)
                    .tieuDeChuong("Chương 1 OIV")
                    .thuTuSapXep(1)
                    .build());

            baiHocRepository.save(BaiHoc.builder()
                    .chuongHoc(chuong)
                    .tieuDeBaiHoc("Bài 1 OIV")
                    .linkVideo("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                    .thoiLuongGiay(600)
                    .choPhepHocThu(true)
                    .thuTuSapXep(1)
                    .build());

            liveClass = lopHocTrucTuyenRepository.save(LopHocTrucTuyen.builder()
                    .giangVien(tutorProfile)
                    .danhMuc(childCat)
                    .tieuDeLop("Lớp Kèm OIV " + tag)
                    .moTaLop("Lớp học kèm trực tuyến")
                    .siSoToiDa(5)
                    .hocPhiMoiHocVien(BigDecimal.valueOf(800000))
                    .lichHocTrongTuan("T2 - T4 (19:30)")
                    .trangThai(TrangThaiLopHoc.DANG_MO_DANG_KY)
                    .build());

            order = donHangRepository.save(DonHang.builder()
                    .maDonHang("SWM-OIV-" + tag)
                    .nguoiDung(studentUser)
                    .tongTienThanhToan(BigDecimal.valueOf(450000))
                    .phuongThucThanhToan(PhuongThucThanhToan.PAYOS)
                    .trangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN)
                    .build());

            chiTietDonHangRepository.save(ChiTietDonHang.builder()
                    .donHang(order)
                    .loaiSanPham(LoaiSanPhamDonHang.KHOA_HOC_VIDEO)
                    .sanPhamId(course.getId())
                    .donGia(BigDecimal.valueOf(450000))
                    .build());

            enrollment = ghiDanhKhoaHocRepository.save(GhiDanhKhoaHoc.builder()
                    .nguoiDung(studentUser)
                    .khoaHoc(course)
                    .donHang(order)
                    .phanTramTienDo(0)
                    .build());

            classReg = dangKyLopHocRepository.save(DangKyLopHoc.builder()
                    .lopHoc(liveClass)
                    .hocVien(studentUser)
                    .donHang(order)
                    .trangThaiThamGia(TrangThaiThamGiaLop.DA_XAC_NHAN)
                    .build());

            review = danhGiaNhanXetRepository.save(DanhGiaNhanXet.builder()
                    .nguoiDung(studentUser)
                    .loaiDanhGia(LoaiYeuThich.KHOA_HOC)
                    .mucTieuId(course.getId())
                    .soSaoDanhGia(5)
                    .noiDungNhanXet("Rất dễ hiểu!")
                    .duocHienThi(true)
                    .build());

            String freeSlug = "khoa-hoc-mien-phi-" + tag;
            freeCourse = khoaHocRepository.save(KhoaHoc.builder()
                    .giangVien(tutorProfile)
                    .danhMuc(childCat)
                    .tieuDe("Khóa Miễn Phí OIV " + tag)
                    .duongDanSlug(freeSlug)
                    .giaGoc(BigDecimal.ZERO)
                    .giaKhuyenMai(BigDecimal.ZERO)
                    .trangThai(TrangThaiKhoaHoc.DA_XUAT_BAN)
                    .build());

            booking = datLich1k1Repository.save(DatLich1k1.builder()
                    .maDatLich("DL-OIV-" + tag)
                    .hocVien(studentUser)
                    .giangVien(tutorProfile)
                    .monHoc(childCat)
                    .thoiGianBatDau(LocalDateTime.now().plusDays(2))
                    .thoiGianKetThuc(LocalDateTime.now().plusDays(2).plusHours(2))
                    .hocPhiCaHoc(BigDecimal.valueOf(200000))
                    .tyLeHoaHongSan(BigDecimal.valueOf(20))
                    .phiSanKhauTru(BigDecimal.valueOf(40000))
                    .thucNhanGiaSu(BigDecimal.valueOf(160000))
                    .trangThai(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY)
                    .build());

            String adminToken = login(adminUser.getEmail(), rawPass);
            String tutorToken = login(tutorUser.getEmail(), rawPass);
            String otherTutorToken = login(otherTutorUser.getEmail(), rawPass);
            String studentToken = login(studentUser.getEmail(), rawPass);

            // 1. Public Web Pages (kiểm tra render trọn vẹn tới </html>, không nổ LazyInitializationException)
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/khoa-hoc"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/khoa-hoc/" + slug))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/gia-su"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/gia-su/" + tutorProfile.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            // 2. Public REST APIs (serialize entity JSON thật ngoài Hibernate Session)
            mockMvc.perform(get("/api/public/khoa-hoc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu.content").isNotEmpty());

            mockMvc.perform(get("/api/public/khoa-hoc/" + slug))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu.danhSachChuongHoc[0].danhSachBaiHoc[0].tieuDeBaiHoc").value("Bài 1 OIV"));

            mockMvc.perform(get("/api/public/giang-vien"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu.content").isNotEmpty());

            mockMvc.perform(get("/api/public/giang-vien/" + tutorProfile.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu.nguoiDung.hoTen").value("Gia Sư OIV " + tag))
                    .andExpect(jsonPath("$.duLieu.nguoiDung.matKhau").doesNotExist());

            mockMvc.perform(get("/api/public/giang-vien/" + tutorProfile.getId() + "/lop-hoc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu[0].tieuDeLop").value("Lớp Kèm OIV " + tag));

            mockMvc.perform(get("/api/public/giang-vien/" + tutorProfile.getId() + "/khoa-hoc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu[*].duongDanSlug", hasItems(slug, freeSlug)));

            // 3. Student Web & API với dữ liệu thật (đơn hàng, ghi danh, lớp học)
            mockMvc.perform(get("/student/dashboard").header("Authorization", "Bearer " + studentToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/student/courses").header("Authorization", "Bearer " + studentToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/student/classes").header("Authorization", "Bearer " + studentToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/student/orders").header("Authorization", "Bearer " + studentToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/api/hoc-vien/khoa-hoc").header("Authorization", "Bearer " + studentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.duLieu[0].duongDanSlug").value(slug));

            mockMvc.perform(get("/api/hoc-vien/hoc-tap/" + slug).header("Authorization", "Bearer " + studentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.duLieu.tongSoBaiHoc").value(1));

            mockMvc.perform(get("/api/hoc-vien/don-hang").header("Authorization", "Bearer " + studentToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.duLieu[0].maDonHang").value("SWM-OIV-" + tag))
                    .andExpect(jsonPath("$.duLieu[0].chiTiet[0].sanPhamId").value(course.getId()));

            // 4. Tutor Web & API với dữ liệu thật (lớp học có học viên, khóa học)
            mockMvc.perform(get("/tutor/dashboard").header("Authorization", "Bearer " + tutorToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/tutor/classes").header("Authorization", "Bearer " + tutorToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/tutor/schedule").header("Authorization", "Bearer " + tutorToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/api/giang-vien/khoa-hoc").header("Authorization", "Bearer " + tutorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.duLieu[*].duongDanSlug", hasItems(slug, freeSlug)));

            // 5. Admin Web với lớp học & đánh giá thật
            mockMvc.perform(get("/admin/lop-hoc").header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            // 6. Regression checks: public aliases, sort/filter, free course pricing, ApiResponse.success, 1k1 open-in-view, ownership 403, invalid param 400
            mockMvc.perform(get("/dang-nhap")).andExpect(status().isOk());
            mockMvc.perform(get("/dang-ky")).andExpect(status().isOk());
            mockMvc.perform(get("/gia-su?sort=ten-az&grade=Toan"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("</html>")));

            mockMvc.perform(get("/khoa-hoc/" + freeSlug))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Miễn phí")))
                    .andExpect(content().string(not(containsString(">0 đ<"))));

            mockMvc.perform(get("/api/tutor/ca-hoc/" + booking.getId())
                            .header("Authorization", "Bearer " + tutorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.duLieu.maDatLich").value("DL-OIV-" + tag));

            mockMvc.perform(put("/api/tutor/ca-hoc/" + booking.getId() + "/link-meet")
                            .header("Authorization", "Bearer " + tutorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"linkPhongHoc\":\"https://meet.google.com/oiv-test-room\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.duLieu.tenHocVien").value("Học Viên OIV " + tag))
                    .andExpect(jsonPath("$.duLieu.linkPhongHoc").value("https://meet.google.com/oiv-test-room"));

            mockMvc.perform(get("/api/tutor/ca-hoc/" + booking.getId())
                            .header("Authorization", "Bearer " + otherTutorToken))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/khoa-hoc/" + course.getId() + "/chi-tiet")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.duLieu.danhSachChuongHoc").isArray());

            mockMvc.perform(get("/api/admin/danh-muc/not-a-number")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        } finally {
            // Dọn sạch dữ liệu test theo đúng thứ tự khóa ngoại
            if (booking != null) datLich1k1Repository.deleteById(booking.getId());
            if (review != null) danhGiaNhanXetRepository.deleteById(review.getId());
            if (classReg != null) dangKyLopHocRepository.deleteById(classReg.getId());
            if (enrollment != null) ghiDanhKhoaHocRepository.deleteById(enrollment.getId());
            if (order != null) donHangRepository.deleteById(order.getId());
            if (liveClass != null) lopHocTrucTuyenRepository.deleteById(liveClass.getId());
            if (freeCourse != null) khoaHocRepository.deleteById(freeCourse.getId());
            if (course != null) khoaHocRepository.deleteById(course.getId());
            if (otherTutorProfile != null) hoSoGiangVienRepository.deleteById(otherTutorProfile.getId());
            if (tutorProfile != null) hoSoGiangVienRepository.deleteById(tutorProfile.getId());
            if (studentUser != null) nguoiDungRepository.deleteById(studentUser.getId());
            if (otherTutorUser != null) nguoiDungRepository.deleteById(otherTutorUser.getId());
            if (tutorUser != null) nguoiDungRepository.deleteById(tutorUser.getId());
            if (adminUser != null) nguoiDungRepository.deleteById(adminUser.getId());
            if (childCat != null) danhMucRepository.deleteById(childCat.getId());
            if (rootCat != null) danhMucRepository.deleteById(rootCat.getId());
        }
    }
}

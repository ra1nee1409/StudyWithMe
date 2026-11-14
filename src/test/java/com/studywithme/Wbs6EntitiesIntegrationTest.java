package com.studywithme;

import com.studywithme.entity.*;
import com.studywithme.entity.enums.giaovien.LoaiLichDay;
import com.studywithme.entity.enums.giaovien.ThuTrongTuan;
import com.studywithme.entity.enums.giaovien.TrangThaiLichDay;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.entity.enums.shared.LoaiTinNhan;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class Wbs6EntitiesIntegrationTest {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private LichDayGiaSuRepository lichDayGiaSuRepository;

    @Autowired
    private DatLich1k1Repository datLich1k1Repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private NguoiDung tutorUser;
    private HoSoGiangVien tutorProfile;
    private NguoiDung studentUser;
    private DanhMuc subject;

    @BeforeEach
    void setUp() {
        VaiTro roleGv = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_GIANG_VIEN).build()));

        VaiTro roleHv = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).build()));

        String uid = UUID.randomUUID().toString().substring(0, 8);

        tutorUser = NguoiDung.builder()
                .email("tutor_wbs6_" + uid + "@studywithme.vn")
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Gia Sư WBS6 Test")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        tutorUser.getDanhSachVaiTro().add(roleGv);
        tutorUser = nguoiDungRepository.save(tutorUser);

        tutorProfile = HoSoGiangVien.builder()
                .nguoiDung(tutorUser)
                .tieuDeChuyenMon("Gia sư Toán cấp 3 & Lập trình")
                .soDuVi(BigDecimal.ZERO)
                .build();
        tutorProfile = hoSoGiangVienRepository.save(tutorProfile);

        studentUser = NguoiDung.builder()
                .email("student_wbs6_" + uid + "@studywithme.vn")
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Học Viên WBS6 Test")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        studentUser.getDanhSachVaiTro().add(roleHv);
        studentUser = nguoiDungRepository.save(studentUser);

        subject = danhMucRepository.findAll().stream().findFirst()
                .orElseGet(() -> danhMucRepository.save(DanhMuc.builder()
                        .tenDanhMuc("Toán Học 1-1")
                        .duongDanSlug("toan-hoc-1-1-" + uid)
                        .build()));
    }

    // =========================================================================
    // 1. TEST THỰC THỂ LỊCH DẠY GIA SƯ (LichDayGiaSu)
    // =========================================================================

    @Test
    @DisplayName("WBS 6.1: Khởi tạo, lưu và truy vấn slot lịch dạy rảnh (Weekly Slots) của gia sư")
    void testLichDayGiaSuPersistence() {
        // Tạo slot cố định thứ 2 từ 19:00 - 20:30
        LichDayGiaSu slot1 = LichDayGiaSu.builder()
                .giangVien(tutorProfile)
                .thuTrongTuan(ThuTrongTuan.THU_HAI)
                .gioBatDau(LocalTime.of(19, 0))
                .gioKetThuc(LocalTime.of(20, 30))
                .loaiLich(LoaiLichDay.CO_DINH)
                .trangThai(TrangThaiLichDay.CON_TRONG)
                .hocPhiMoiGio(BigDecimal.valueOf(150000))
                .ghiChu("Dạy đại số hoặc hình học 12")
                .build();
        slot1 = lichDayGiaSuRepository.save(slot1);

        // Tạo slot thứ 4 từ 20:00 - 21:30
        LichDayGiaSu slot2 = LichDayGiaSu.builder()
                .giangVien(tutorProfile)
                .thuTrongTuan(ThuTrongTuan.THU_TU)
                .gioBatDau(LocalTime.of(20, 0))
                .gioKetThuc(LocalTime.of(21, 30))
                .loaiLich(LoaiLichDay.CO_DINH)
                .trangThai(TrangThaiLichDay.CON_TRONG)
                .hocPhiMoiGio(BigDecimal.valueOf(150000))
                .build();
        lichDayGiaSuRepository.save(slot2);

        // Kiểm tra truy vấn
        List<LichDayGiaSu> slotsOfTutor = lichDayGiaSuRepository.findByGiangVienIdOrderByGioBatDauAsc(tutorProfile.getId());
        assertEquals(2, slotsOfTutor.size(), "Gia sư phải có 2 slot rảnh trong tuần");

        List<LichDayGiaSu> thuHaiSlots = lichDayGiaSuRepository.findByGiangVienIdAndThuTrongTuanOrderByGioBatDauAsc(
                tutorProfile.getId(), ThuTrongTuan.THU_HAI);
        assertEquals(1, thuHaiSlots.size());
        assertEquals(LocalTime.of(19, 0), thuHaiSlots.get(0).getGioBatDau());

        long countTrong = lichDayGiaSuRepository.countByGiangVienIdAndTrangThai(tutorProfile.getId(), TrangThaiLichDay.CON_TRONG);
        assertEquals(2, countTrong);

        // Cập nhật trạng thái slot sang ĐÃ ĐẶT
        slot1.setTrangThai(TrangThaiLichDay.DA_DAT);
        lichDayGiaSuRepository.save(slot1);

        long countSauDat = lichDayGiaSuRepository.countByGiangVienIdAndTrangThai(tutorProfile.getId(), TrangThaiLichDay.CON_TRONG);
        assertEquals(1, countSauDat, "Chỉ còn 1 slot trống");
    }

    // =========================================================================
    // 2. TEST THỰC THỂ ĐẶT LỊCH CA HỌC 1-1 (DatLich1k1)
    // =========================================================================

    @Test
    @DisplayName("WBS 6.2: Khởi tạo ca học 1-1, tính toán khấu trừ hoa hồng sàn và vòng đời ca học")
    void testDatLich1k1LifecycleAndFinancialCalculation() {
        BigDecimal hocPhiCaHoc = BigDecimal.valueOf(300000); // 300,000 VND
        BigDecimal tyLeSan = BigDecimal.valueOf(20.0);       // 20%
        BigDecimal phiSan = hocPhiCaHoc.multiply(tyLeSan).divide(BigDecimal.valueOf(100)); // 60,000 VND
        BigDecimal thucNhan = hocPhiCaHoc.subtract(phiSan);                                // 240,000 VND

        LocalDateTime now = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0);
        LocalDateTime end = now.plusHours(2);

        String maDatLich = "DL-" + System.currentTimeMillis();

        DatLich1k1 booking = DatLich1k1.builder()
                .maDatLich(maDatLich)
                .hocVien(studentUser)
                .giangVien(tutorProfile)
                .monHoc(subject)
                .thoiGianBatDau(now)
                .thoiGianKetThuc(end)
                .hocPhiCaHoc(hocPhiCaHoc)
                .tyLeHoaHongSan(tyLeSan)
                .phiSanKhauTru(phiSan)
                .thucNhanGiaSu(thucNhan)
                .trangThai(TrangThaiDatLich.CHO_XAC_NHAN)
                .ghiChuHocVien("Em muốn ôn tập phần khảo sát hàm số")
                .linkPhongHoc("https://meet.google.com/abc-defg-hij")
                .build();

        DatLich1k1 saved = datLich1k1Repository.save(booking);

        assertNotNull(saved.getId());
        assertEquals(maDatLich, saved.getMaDatLich());
        assertEquals(0, BigDecimal.valueOf(60000).compareTo(saved.getPhiSanKhauTru()), "Phí sàn trích trừ phải là 60.000 đ");
        assertEquals(0, BigDecimal.valueOf(240000).compareTo(saved.getThucNhanGiaSu()), "Thực nhận gia sư phải là 240.000 đ");

        // Kiểm tra trùng lịch dạy (Conflict check)
        boolean hasConflict = datLich1k1Repository.existsByGiangVienIdAndThoiGianBatDauLessThanAndThoiGianKetThucGreaterThanAndTrangThaiNot(
                tutorProfile.getId(),
                now.plusMinutes(90),
                now.plusMinutes(30),
                TrangThaiDatLich.DA_HUY
        );
        assertTrue(hasConflict, "Phải phát hiện trùng lịch với ca học đang tồn tại");

        // Kiểm tra vòng đời trạng thái: Gia sư duyệt -> Đã thanh toán ký quỹ -> Hoàn thành
        saved.setTrangThai(TrangThaiDatLich.CHO_THANH_TOAN);
        saved = datLich1k1Repository.save(saved);
        assertEquals(TrangThaiDatLich.CHO_THANH_TOAN, saved.getTrangThai());

        saved.setTrangThai(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY);
        saved = datLich1k1Repository.save(saved);
        assertEquals(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY, saved.getTrangThai());

        saved.setTrangThai(TrangThaiDatLich.HOAN_THANH);
        saved = datLich1k1Repository.save(saved);
        assertEquals(TrangThaiDatLich.HOAN_THANH, saved.getTrangThai());
    }
}

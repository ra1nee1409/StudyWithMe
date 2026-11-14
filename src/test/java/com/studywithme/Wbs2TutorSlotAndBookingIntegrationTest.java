package com.studywithme;

import com.studywithme.dto.tutor.*;
import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import com.studywithme.entity.enums.giaovien.LoaiLichDay;
import com.studywithme.entity.enums.giaovien.ThuTrongTuan;
import com.studywithme.entity.enums.giaovien.TrangThaiLichDay;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.*;
import com.studywithme.service.DatLich1k1Service;
import com.studywithme.service.LichDayGiaSuService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class Wbs2TutorSlotAndBookingIntegrationTest {

    @Autowired
    private LichDayGiaSuService lichDayGiaSuService;

    @Autowired
    private DatLich1k1Service datLich1k1Service;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private LichSuViTienRepository lichSuViTienRepository;

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
                .email("tutor_wbs2_" + uid + "@studywithme.vn")
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Gia Sư WBS2 Test")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        tutorUser.getDanhSachVaiTro().add(roleGv);
        tutorUser = nguoiDungRepository.save(tutorUser);

        tutorProfile = HoSoGiangVien.builder()
                .nguoiDung(tutorUser)
                .tieuDeChuyenMon("Gia sư Chuyên Toán & Lý 1-1")
                .soDuVi(BigDecimal.ZERO)
                .build();
        tutorProfile = hoSoGiangVienRepository.save(tutorProfile);

        studentUser = NguoiDung.builder()
                .email("student_wbs2_" + uid + "@studywithme.vn")
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Học Viên WBS2 Test")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>())
                .build();
        studentUser.getDanhSachVaiTro().add(roleHv);
        studentUser = nguoiDungRepository.save(studentUser);

        subject = danhMucRepository.findAll().stream().findFirst()
                .orElseGet(() -> danhMucRepository.save(DanhMuc.builder()
                        .tenDanhMuc("Vật Lý 12 Luyện Thi")
                        .duongDanSlug("vat-ly-12-luyen-thi-" + uid)
                        .build()));
    }

    // =========================================================================
    // 1. TEST WBS 2.2: BỘ CHỌN LỊCH TUẦN RẢNH (WEEKLY SLOT PICKER)
    // =========================================================================

    @Test
    @DisplayName("WBS 2.2: Gia sư tạo slot rảnh tuần thành công")
    void testTaoSlotLichDayThanhCong() {
        LichDaySlotRequest req = LichDaySlotRequest.builder()
                .thuTrongTuan(ThuTrongTuan.THU_HAI)
                .gioBatDau(LocalTime.of(19, 0))
                .gioKetThuc(LocalTime.of(20, 30))
                .loaiLich(LoaiLichDay.CO_DINH)
                .hocPhiMoiGio(BigDecimal.valueOf(180000))
                .ghiChu("Chuyên sâu hình học không gian")
                .build();

        LichDayGiaSu slot = lichDayGiaSuService.taoHoacCapNhatSlot(tutorProfile.getId(), req);
        assertNotNull(slot.getId());
        assertEquals(ThuTrongTuan.THU_HAI, slot.getThuTrongTuan());
        assertEquals(LocalTime.of(19, 0), slot.getGioBatDau());
        assertEquals(LocalTime.of(20, 30), slot.getGioKetThuc());
        assertEquals(TrangThaiLichDay.CON_TRONG, slot.getTrangThai());

        List<LichDaySlotDto> list = lichDayGiaSuService.layDanhSachLichDay(tutorProfile.getId());
        assertEquals(1, list.size());
        assertEquals("Thứ 2", list.get(0).getThuTrongTuanMoTa());
    }

    @Test
    @DisplayName("WBS 2.2: Bắt lỗi khung giờ không hợp lệ (giờ bắt đầu sau giờ kết thúc)")
    void testGioBatDauSauGioKetThucThrowsException() {
        LichDaySlotRequest invalidReq = LichDaySlotRequest.builder()
                .thuTrongTuan(ThuTrongTuan.THU_BA)
                .gioBatDau(LocalTime.of(21, 0))
                .gioKetThuc(LocalTime.of(19, 0)) // Không hợp lệ
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                lichDayGiaSuService.taoHoacCapNhatSlot(tutorProfile.getId(), invalidReq));
        assertTrue(ex.getMessage().contains("phải sớm hơn giờ kết thúc"));
    }

    @Test
    @DisplayName("WBS 2.2: Bắt lỗi khung giờ trùng lặp (Overlap check) trên cùng một thứ")
    void testTrungLapKhungGioThrowsException() {
        // Tạo slot 1: 18:00 - 19:30
        LichDaySlotRequest slot1 = LichDaySlotRequest.builder()
                .thuTrongTuan(ThuTrongTuan.THU_TU)
                .gioBatDau(LocalTime.of(18, 0))
                .gioKetThuc(LocalTime.of(19, 30))
                .build();
        lichDayGiaSuService.taoHoacCapNhatSlot(tutorProfile.getId(), slot1);

        // Tạo slot 2 trùng lặp: 19:00 - 20:30 (bị đè từ 19:00 đến 19:30)
        LichDaySlotRequest slot2 = LichDaySlotRequest.builder()
                .thuTrongTuan(ThuTrongTuan.THU_TU)
                .gioBatDau(LocalTime.of(19, 0))
                .gioKetThuc(LocalTime.of(20, 30))
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                lichDayGiaSuService.taoHoacCapNhatSlot(tutorProfile.getId(), slot2));
        assertTrue(ex.getMessage().contains("trùng lặp với slot rảnh đã có"));

        // Slot 3 liền kề hợp lệ: 19:30 - 21:00 (không bị trùng)
        LichDaySlotRequest slot3 = LichDaySlotRequest.builder()
                .thuTrongTuan(ThuTrongTuan.THU_TU)
                .gioBatDau(LocalTime.of(19, 30))
                .gioKetThuc(LocalTime.of(21, 0))
                .build();
        assertDoesNotThrow(() -> lichDayGiaSuService.taoHoacCapNhatSlot(tutorProfile.getId(), slot3));
    }

    @Test
    @DisplayName("WBS 2.2: Tạm khóa / Mở lại slot và Xóa slot")
    void testToggleVaXoaSlot() {
        LichDaySlotRequest req = LichDaySlotRequest.builder()
                .thuTrongTuan(ThuTrongTuan.THU_SAU)
                .gioBatDau(LocalTime.of(8, 0))
                .gioKetThuc(LocalTime.of(10, 0))
                .build();
        LichDayGiaSu slot = lichDayGiaSuService.taoHoacCapNhatSlot(tutorProfile.getId(), req);

        // Toggle sang TAM_KHOA
        LichDayGiaSu locked = lichDayGiaSuService.toggleTrangThaiSlot(tutorProfile.getId(), slot.getId());
        assertEquals(TrangThaiLichDay.TAM_KHOA, locked.getTrangThai());

        // Toggle lại sang CON_TRONG
        LichDayGiaSu unlocked = lichDayGiaSuService.toggleTrangThaiSlot(tutorProfile.getId(), slot.getId());
        assertEquals(TrangThaiLichDay.CON_TRONG, unlocked.getTrangThai());

        // Xóa slot
        lichDayGiaSuService.xoaSlot(tutorProfile.getId(), slot.getId());
        List<LichDaySlotDto> list = lichDayGiaSuService.layDanhSachLichDay(tutorProfile.getId());
        assertTrue(list.isEmpty());
    }

    // =========================================================================
    // 2. TEST WBS 2.3 & 2.4: ĐẶT LỊCH 1-1, DUYỆT, GẮN LINK MEET VÀ GIẢI NGÂN
    // =========================================================================

    @Test
    @DisplayName("WBS 2.3 & 2.4: Quy trình đặt ca học kèm 1-1, tính phí sàn, gắn Meet và giải ngân khi hoàn tất")
    void testQuyTrinhDatCaHoc1k1ToanDien() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(2).withHour(19).withMinute(0);
        LocalDateTime endTime = startTime.plusHours(2);

        DatCaHoc1k1Request datReq = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .monHocId(subject.getId())
                .thoiGianBatDau(startTime)
                .thoiGianKetThuc(endTime)
                .hocPhiCaHoc(BigDecimal.valueOf(300000))
                .ghiChuHocVien("Cần ôn tập gấp phương trình lượng giác nâng cao")
                .build();

        // 1. Học viên gửi yêu cầu đặt ca học
        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), datReq);
        assertNotNull(caHoc.getId());
        assertTrue(caHoc.getMaDatLich().startsWith("DL1K1-"));
        assertEquals(TrangThaiDatLich.CHO_XAC_NHAN, caHoc.getTrangThai());

        // Kiểm tra tính toán tài chính ký quỹ (Phí sàn + Thực nhận = Tổng học phí)
        assertEquals(0, BigDecimal.valueOf(300000).compareTo(caHoc.getHocPhiCaHoc()));
        assertEquals(0, caHoc.getHocPhiCaHoc().compareTo(caHoc.getPhiSanKhauTru().add(caHoc.getThucNhanGiaSu())));
        assertTrue(caHoc.getPhiSanKhauTru().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(caHoc.getThucNhanGiaSu().compareTo(BigDecimal.ZERO) > 0);
        BigDecimal thucNhanExpected = caHoc.getThucNhanGiaSu();

        // 2. Kiểm tra chống đặt trùng ca học
        DatCaHoc1k1Request trungReq = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(startTime.plusMinutes(30))
                .thoiGianKetThuc(endTime.plusMinutes(30))
                .hocPhiCaHoc(BigDecimal.valueOf(200000))
                .build();
        assertThrows(IllegalArgumentException.class, () ->
                datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), trungReq));

        // 3. Gia sư chấp nhận ca học -> Chuyển sang CHO_THANH_TOAN
        XacNhanCaHocRequest acceptReq = XacNhanCaHocRequest.builder().chapNhan(true).build();
        DatLich1k1 accepted = datLich1k1Service.giaSuPhanHoiCaHoc(tutorProfile.getId(), caHoc.getId(), acceptReq);
        assertEquals(TrangThaiDatLich.CHO_THANH_TOAN, accepted.getTrangThai());

        // 4. Học viên thanh toán ký quỹ qua PayOS -> Sàn giữ tiền (DA_THANH_TOAN_KY_QUY)
        DatLich1k1 escrowed = datLich1k1Service.xacNhanThanhToanKyQuy(caHoc.getId());
        assertEquals(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY, escrowed.getTrangThai());

        // 5. Gia sư gắn đường link phòng học Google Meet
        CapNhatLinkMeetRequest meetReq = CapNhatLinkMeetRequest.builder()
                .linkPhongHoc("https://meet.google.com/swm-tutor-test")
                .build();
        DatLich1k1 withMeet = datLich1k1Service.capNhatLinkMeet(tutorProfile.getId(), caHoc.getId(), meetReq);
        assertEquals("https://meet.google.com/swm-tutor-test", withMeet.getLinkPhongHoc());

        // 6. Buổi học kết thúc -> Gia sư bấm hoàn tất ca học -> Tiền ký quỹ được giải phóng vào ví gia sư
        BigDecimal viTruoc = tutorProfile.getSoDuVi();
        DatLich1k1 completed = datLich1k1Service.hoanTatCaHoc(tutorProfile.getId(), caHoc.getId());
        assertEquals(TrangThaiDatLich.HOAN_THANH, completed.getTrangThai());

        // Kiểm tra số dư ví gia sư tăng đúng bằng thực nhận
        HoSoGiangVien gvSau = hoSoGiangVienRepository.findById(tutorProfile.getId()).orElseThrow();
        assertEquals(0, viTruoc.add(thucNhanExpected).compareTo(gvSau.getSoDuVi()));

        // Kiểm tra lịch sử giao dịch ví (LichSuViTien) ghi nhận chính xác loại TIEN_DAY_KEM_1K1
        List<LichSuViTien> lsList = lichSuViTienRepository.findByGiangVienIdOrderByNgayTaoDesc(tutorProfile.getId());
        assertFalse(lsList.isEmpty());
        LichSuViTien ls = lsList.get(0);
        assertEquals(LoaiGiaoDichVi.TIEN_DAY_KEM_1K1, ls.getLoaiGiaoDich());
        assertEquals(0, thucNhanExpected.compareTo(ls.getSoTienBienDong()));
        assertTrue(ls.getMoTaGiaoDich().contains(completed.getMaDatLich()));
    }

    @Test
    @DisplayName("WBS 2.3: Gia sư từ chối ca học và lưu lý do từ chối")
    void testGiaSuTuChoiCaHoc() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(3).withHour(14).withMinute(0);
        LocalDateTime endTime = startTime.plusHours(1);

        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(startTime)
                .thoiGianKetThuc(endTime)
                .hocPhiCaHoc(BigDecimal.valueOf(150000))
                .build();

        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);

        // Gia sư từ chối
        XacNhanCaHocRequest rejectReq = XacNhanCaHocRequest.builder()
                .chapNhan(false)
                .lyDoTuChoi("Bận lịch công tác đột xuất")
                .build();

        DatLich1k1 rejected = datLich1k1Service.giaSuPhanHoiCaHoc(tutorProfile.getId(), caHoc.getId(), rejectReq);
        assertEquals(TrangThaiDatLich.DA_HUY, rejected.getTrangThai());
        assertEquals("Bận lịch công tác đột xuất", rejected.getLyDoHuy());
    }

    @Test
    @DisplayName("WBS 2.3: Học viên tự hủy ca học khi đang ở trạng thái chờ duyệt")
    void testHocVienHuyCaHoc() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(4).withHour(10).withMinute(0);
        LocalDateTime endTime = startTime.plusHours(1);

        DatCaHoc1k1Request req = DatCaHoc1k1Request.builder()
                .giangVienId(tutorProfile.getId())
                .thoiGianBatDau(startTime)
                .thoiGianKetThuc(endTime)
                .hocPhiCaHoc(BigDecimal.valueOf(150000))
                .build();

        DatLich1k1 caHoc = datLich1k1Service.taoYeuCauDatLich(studentUser.getId(), req);

        // Học viên hủy ca học
        DatLich1k1 canceled = datLich1k1Service.hocVienHuyCaHoc(studentUser.getId(), caHoc.getId(), "Em đổi lịch học trên trường");
        assertEquals(TrangThaiDatLich.DA_HUY, canceled.getTrangThai());
        assertEquals("Em đổi lịch học trên trường", canceled.getLyDoHuy());
    }
}

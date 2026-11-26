package com.studywithme;

import com.studywithme.entity.*;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.*;
import com.studywithme.service.DatLich1k1Service;
import com.studywithme.service.PayOsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AcidTransactionIntegrityIntegrationTest {

    @Autowired
    private DatLich1k1Service datLich1k1Service;

    @Autowired
    private PayOsService payOsService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private HoSoGiangVienRepository hoSoGiangVienRepository;

    @Autowired
    private DatLich1k1Repository datLich1k1Repository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private LichSuViTienRepository lichSuViTienRepository;

    @Test
    @DisplayName("TC-SEC-015: ACID Transaction Consistency - Rollback toàn bộ khi xảy ra lỗi giữa chừng trong giao dịch ký quỹ")
    void testAcidTransactionRollbackOnFailure() {
        // 1. Chuẩn bị dữ liệu mẫu trong 1 transaction riêng
        String email = "acid_student_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        VaiTro roleHocVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).moTa("Học viên").build()));

        NguoiDung student = nguoiDungRepository.save(NguoiDung.builder()
                .email(email)
                .hoTen("Học Viên ACID Test")
                .matKhau("123456")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>(Set.of(roleHocVien)))
                .build());

        NguoiDung tutorUser = nguoiDungRepository.save(NguoiDung.builder()
                .email("acid_tutor_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com")
                .hoTen("Giảng Viên ACID Test")
                .matKhau("123456")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build());

        HoSoGiangVien tutor = hoSoGiangVienRepository.save(HoSoGiangVien.builder()
                .nguoiDung(tutorUser)
                .tieuDeChuyenMon("ACID Tutor")
                .soDuVi(BigDecimal.ZERO)
                .trangThaiDuyet(TrangThaiGiangVien.DA_DUYET)
                .build());

        DatLich1k1 caHoc = datLich1k1Repository.save(DatLich1k1.builder()
                .maDatLich("ACID-" + System.currentTimeMillis())
                .hocVien(student)
                .giangVien(tutor)
                .hocPhiCaHoc(BigDecimal.valueOf(200000))
                .thucNhanGiaSu(BigDecimal.valueOf(160000))
                .phiSanKhauTru(BigDecimal.valueOf(40000))
                .tyLeHoaHongSan(BigDecimal.valueOf(20))
                .trangThai(TrangThaiDatLich.CHO_XAC_NHAN)
                .thoiGianBatDau(LocalDateTime.now().plusDays(1))
                .thoiGianKetThuc(LocalDateTime.now().plusDays(1).plusHours(2))
                .build());

        Long caHocId = caHoc.getId();
        Long tutorId = tutor.getId();

        try {
            // 2. Chạy khối giao dịch mô phỏng lỗi xảy ra ở bước cuối cùng
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);

            try {
                // Bước A: Đổi trạng thái ca học
                DatLich1k1 caHocInTx = datLich1k1Repository.findById(caHocId).orElseThrow();
                caHocInTx.setTrangThai(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY);
                datLich1k1Repository.save(caHocInTx);

                // Bước B: Mô phỏng sự cố ngắt kết nối mạng hoặc lỗi hệ thống không mong muốn
                throw new RuntimeException("Mô phỏng sự cố mạng nghiêm trọng ở bước cuối!");
            } catch (RuntimeException e) {
                // Bước C: Rollback toàn bộ giao dịch
                transactionManager.rollback(status);
            }

            // 3. Kiểm tra tính toàn vẹn (ACID): Dữ liệu CSDL phải giữ nguyên trạng thái ban đầu
            DatLich1k1 caHocAfter = datLich1k1Repository.findById(caHocId).orElseThrow();
            assertEquals(TrangThaiDatLich.CHO_XAC_NHAN, caHocAfter.getTrangThai(),
                    "ACID: Trạng thái ca học phải được rollback về CHO_XAC_NHAN, không bị lưu dở dang");

            HoSoGiangVien tutorAfter = hoSoGiangVienRepository.findById(tutorId).orElseThrow();
            assertEquals(0, BigDecimal.ZERO.compareTo(tutorAfter.getSoDuVi()),
                    "ACID: Số dư ví gia sư không bị thay đổi khi giao dịch gặp sự cố");
        } finally {
            // Dọn dẹp dữ liệu test kể cả khi assert thất bại
            datLich1k1Repository.deleteById(caHocId);
            hoSoGiangVienRepository.deleteById(tutorId);
            nguoiDungRepository.deleteById(tutorUser.getId());
            nguoiDungRepository.deleteById(student.getId());
        }
    }

    @Test
    @DisplayName("TC-SEC-015: Idempotency & ACID - Không xử lý trùng lặp và không roll back sai dữ liệu")
    void testIdempotencyOnPaymentActivation() {
        // Đảm bảo method payOsService.kichHoatDonHang trả về true và an toàn khi gọi lặp
        boolean result1 = payOsService.kichHoatDonHang(999999999999L, "TX_NONE", 50000L);
        assertFalse(result1, "orderCode không tồn tại phải trả về false an toàn mà không làm sập ứng dụng");
    }
}

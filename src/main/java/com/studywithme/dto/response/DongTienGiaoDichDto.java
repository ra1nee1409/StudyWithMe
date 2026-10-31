package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DongTienGiaoDichDto {
    private String maGiaoDich;           // Mã đơn hàng SWM-... hoặc Mã rút tiền
    private String loaiDongTien;         // TIEN_VAO (+), TIEN_RA (-)
    private String phanLoaiDichVu;       // KHOA_HOC, GIA_SU, RUT_TIEN, HOAN_TIEN
    private String tenDichVu;            // Tên khóa học / Lớp gia sư / Rút tiền
    private String nguoiThucHien;        // Họ tên học viên hoặc giảng viên
    private String emailNguoiThucHien;   // Email
    private String vaiTro;               // HOC_VIEN hoặc GIANG_VIEN
    private BigDecimal tongTien;         // Tổng số tiền giao dịch
    private BigDecimal phiSan;           // 20% phí nền tảng
    private BigDecimal tienGiangVien;    // 80% giảng viên nhận
    private String phuongThuc;           // PAYOS, VNPAY, NGAN_HANG...
    private String trangThai;            // DA_THANH_TOAN, CHO_THANH_TOAN, THAT_BAI...
    private LocalDateTime thoiGian;      // Thời gian phát sinh
    private String moTa;                 // Ghi chú / Diễn giải
}


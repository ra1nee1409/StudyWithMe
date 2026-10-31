package com.studywithme.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultDto {
    private boolean thanhCong;
    private String maDonHang;
    private String maGiaoDich;
    private BigDecimal soTien;
    private String nganHang;
    private LocalDateTime thoiGianThanhToan;
    private String thongDiep;
    private String maLoi;
    private List<String> danhSachKhoaHoc;

    public String getMaGiaoDichVnpay() {
        return maGiaoDich;
    }

    public void setMaGiaoDichVnpay(String maGiaoDichVnpay) {
        this.maGiaoDich = maGiaoDichVnpay;
    }
}

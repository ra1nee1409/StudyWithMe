package com.studywithme.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultDto {
    private boolean thanhCong;
    private String maDonHang;
    private String maGiaoDichVnpay;
    private BigDecimal soTien;
    private String nganHang;
    private LocalDateTime thoiGianThanhToan;
    private String thongDiep;
    private String maLoi;
    private List<String> danhSachKhoaHoc;

    public boolean isThanhCong() {
        return thanhCong;
    }

    public void setThanhCong(boolean thanhCong) {
        this.thanhCong = thanhCong;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public String getMaGiaoDichVnpay() {
        return maGiaoDichVnpay;
    }

    public void setMaGiaoDichVnpay(String maGiaoDichVnpay) {
        this.maGiaoDichVnpay = maGiaoDichVnpay;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getNganHang() {
        return nganHang;
    }

    public void setNganHang(String nganHang) {
        this.nganHang = nganHang;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public String getThongDiep() {
        return thongDiep;
    }

    public void setThongDiep(String thongDiep) {
        this.thongDiep = thongDiep;
    }

    public String getMaLoi() {
        return maLoi;
    }

    public void setMaLoi(String maLoi) {
        this.maLoi = maLoi;
    }

    public List<String> getDanhSachKhoaHoc() {
        return danhSachKhoaHoc;
    }

    public void setDanhSachKhoaHoc(List<String> danhSachKhoaHoc) {
        this.danhSachKhoaHoc = danhSachKhoaHoc;
    }

    public static PaymentResultDtoBuilder builder() {
        return new PaymentResultDtoBuilder();
    }

    public static class PaymentResultDtoBuilder {
        private boolean thanhCong;
        private String maDonHang;
        private String maGiaoDichVnpay;
        private BigDecimal soTien;
        private String nganHang;
        private LocalDateTime thoiGianThanhToan;
        private String thongDiep;
        private String maLoi;
        private List<String> danhSachKhoaHoc = new ArrayList<>();

        public PaymentResultDtoBuilder thanhCong(boolean thanhCong) {
            this.thanhCong = thanhCong;
            return this;
        }

        public PaymentResultDtoBuilder maDonHang(String maDonHang) {
            this.maDonHang = maDonHang;
            return this;
        }

        public PaymentResultDtoBuilder maGiaoDichVnpay(String maGiaoDichVnpay) {
            this.maGiaoDichVnpay = maGiaoDichVnpay;
            return this;
        }

        public PaymentResultDtoBuilder soTien(BigDecimal soTien) {
            this.soTien = soTien;
            return this;
        }

        public PaymentResultDtoBuilder nganHang(String nganHang) {
            this.nganHang = nganHang;
            return this;
        }

        public PaymentResultDtoBuilder thoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
            this.thoiGianThanhToan = thoiGianThanhToan;
            return this;
        }

        public PaymentResultDtoBuilder thongDiep(String thongDiep) {
            this.thongDiep = thongDiep;
            return this;
        }

        public PaymentResultDtoBuilder maLoi(String maLoi) {
            this.maLoi = maLoi;
            return this;
        }

        public PaymentResultDtoBuilder danhSachKhoaHoc(List<String> danhSachKhoaHoc) {
            this.danhSachKhoaHoc = danhSachKhoaHoc;
            return this;
        }

        public PaymentResultDto build() {
            return new PaymentResultDto(thanhCong, maDonHang, maGiaoDichVnpay, soTien, nganHang, thoiGianThanhToan, thongDiep, maLoi, danhSachKhoaHoc);
        }
    }
}

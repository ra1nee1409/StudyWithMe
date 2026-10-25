package com.studywithme.entity;

import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_cau_rut_tien")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauRutTien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @Column(name = "so_tien_rut", nullable = false, precision = 15, scale = 2)
    private BigDecimal soTienRut;

    @Column(name = "ten_ngan_hang", nullable = false, length = 100)
    private String tenNganHang;

    @Column(name = "so_tai_khoan", nullable = false, length = 50)
    private String soTaiKhoan;

    @Column(name = "ten_chu_tai_khoan", nullable = false, length = 100)
    private String tenChuTaiKhoan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiRutTien trangThai = TrangThaiRutTien.CHO_XU_LY;

    @Column(name = "ghi_chu_admin", columnDefinition = "TEXT")
    private String ghiChuAdmin;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_xu_ly")
    private LocalDateTime ngayXuLy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HoSoGiangVien getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(HoSoGiangVien giangVien) {
        this.giangVien = giangVien;
    }

    public BigDecimal getSoTienRut() {
        return soTienRut;
    }

    public void setSoTienRut(BigDecimal soTienRut) {
        this.soTienRut = soTienRut;
    }

    public String getTenNganHang() {
        return tenNganHang;
    }

    public void setTenNganHang(String tenNganHang) {
        this.tenNganHang = tenNganHang;
    }

    public String getSoTaiKhoan() {
        return soTaiKhoan;
    }

    public void setSoTaiKhoan(String soTaiKhoan) {
        this.soTaiKhoan = soTaiKhoan;
    }

    public String getTenChuTaiKhoan() {
        return tenChuTaiKhoan;
    }

    public void setTenChuTaiKhoan(String tenChuTaiKhoan) {
        this.tenChuTaiKhoan = tenChuTaiKhoan;
    }

    public TrangThaiRutTien getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiRutTien trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChuAdmin() {
        return ghiChuAdmin;
    }

    public void setGhiChuAdmin(String ghiChuAdmin) {
        this.ghiChuAdmin = ghiChuAdmin;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayXuLy() {
        return ngayXuLy;
    }

    public void setNgayXuLy(LocalDateTime ngayXuLy) {
        this.ngayXuLy = ngayXuLy;
    }

    public static YeuCauRutTienBuilder builder() {
        return new YeuCauRutTienBuilder();
    }

    public static class YeuCauRutTienBuilder {
        private Long id;
        private HoSoGiangVien giangVien;
        private BigDecimal soTienRut;
        private String tenNganHang;
        private String soTaiKhoan;
        private String tenChuTaiKhoan;
        private TrangThaiRutTien trangThai = TrangThaiRutTien.CHO_XU_LY;
        private String ghiChuAdmin;
        private LocalDateTime ngayTao;
        private LocalDateTime ngayXuLy;

        public YeuCauRutTienBuilder id(Long id) { this.id = id; return this; }
        public YeuCauRutTienBuilder giangVien(HoSoGiangVien giangVien) { this.giangVien = giangVien; return this; }
        public YeuCauRutTienBuilder soTienRut(BigDecimal soTienRut) { this.soTienRut = soTienRut; return this; }
        public YeuCauRutTienBuilder tenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; return this; }
        public YeuCauRutTienBuilder soTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; return this; }
        public YeuCauRutTienBuilder tenChuTaiKhoan(String tenChuTaiKhoan) { this.tenChuTaiKhoan = tenChuTaiKhoan; return this; }
        public YeuCauRutTienBuilder trangThai(TrangThaiRutTien trangThai) { this.trangThai = trangThai; return this; }
        public YeuCauRutTienBuilder ghiChuAdmin(String ghiChuAdmin) { this.ghiChuAdmin = ghiChuAdmin; return this; }
        public YeuCauRutTienBuilder ngayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; return this; }
        public YeuCauRutTienBuilder ngayXuLy(LocalDateTime ngayXuLy) { this.ngayXuLy = ngayXuLy; return this; }

        public YeuCauRutTien build() {
            YeuCauRutTien y = new YeuCauRutTien();
            y.setId(id);
            y.setGiangVien(giangVien);
            y.setSoTienRut(soTienRut);
            y.setTenNganHang(tenNganHang);
            y.setSoTaiKhoan(soTaiKhoan);
            y.setTenChuTaiKhoan(tenChuTaiKhoan);
            y.setTrangThai(trangThai != null ? trangThai : TrangThaiRutTien.CHO_XU_LY);
            y.setGhiChuAdmin(ghiChuAdmin);
            y.setNgayTao(ngayTao);
            y.setNgayXuLy(ngayXuLy);
            return y;
        }
    }
}

package com.studywithme.entity;

import com.studywithme.entity.enums.TrangThaiRutTien;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_cau_rut_tien")
@Getter
@Setter
@Builder
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

    public YeuCauRutTien() {
    }

    public YeuCauRutTien(Long id, HoSoGiangVien giangVien, BigDecimal soTienRut, String tenNganHang,
                         String soTaiKhoan, String tenChuTaiKhoan, TrangThaiRutTien trangThai,
                         String ghiChuAdmin, LocalDateTime ngayTao, LocalDateTime ngayXuLy) {
        this.id = id;
        this.giangVien = giangVien;
        this.soTienRut = soTienRut;
        this.tenNganHang = tenNganHang;
        this.soTaiKhoan = soTaiKhoan;
        this.tenChuTaiKhoan = tenChuTaiKhoan;
        this.trangThai = trangThai != null ? trangThai : TrangThaiRutTien.CHO_XU_LY;
        this.ghiChuAdmin = ghiChuAdmin;
        this.ngayTao = ngayTao;
        this.ngayXuLy = ngayXuLy;
    }
}

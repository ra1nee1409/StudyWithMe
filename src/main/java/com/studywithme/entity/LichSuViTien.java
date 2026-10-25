package com.studywithme.entity;

import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
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
@Table(name = "lich_su_vi_tien")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichSuViTien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @Column(name = "so_tien_bien_dong", nullable = false, precision = 15, scale = 2)
    private BigDecimal soTienBienDong;

    @Column(name = "so_du_truoc", nullable = false, precision = 15, scale = 2)
    private BigDecimal soDuTruoc;

    @Column(name = "so_du_sau", nullable = false, precision = 15, scale = 2)
    private BigDecimal soDuSau;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giao_dich", nullable = false, length = 40)
    private LoaiGiaoDichVi loaiGiaoDich;

    @Column(name = "ma_tham_chieu")
    private Long maThamChieu;

    @Column(name = "mo_ta_giao_dich", length = 255)
    private String moTaGiaoDich;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

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

    public BigDecimal getSoTienBienDong() {
        return soTienBienDong;
    }

    public void setSoTienBienDong(BigDecimal soTienBienDong) {
        this.soTienBienDong = soTienBienDong;
    }

    public BigDecimal getSoDuTruoc() {
        return soDuTruoc;
    }

    public void setSoDuTruoc(BigDecimal soDuTruoc) {
        this.soDuTruoc = soDuTruoc;
    }

    public BigDecimal getSoDuSau() {
        return soDuSau;
    }

    public void setSoDuSau(BigDecimal soDuSau) {
        this.soDuSau = soDuSau;
    }

    public LoaiGiaoDichVi getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(LoaiGiaoDichVi loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public Long getMaThamChieu() {
        return maThamChieu;
    }

    public void setMaThamChieu(Long maThamChieu) {
        this.maThamChieu = maThamChieu;
    }

    public String getMoTaGiaoDich() {
        return moTaGiaoDich;
    }

    public void setMoTaGiaoDich(String moTaGiaoDich) {
        this.moTaGiaoDich = moTaGiaoDich;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}

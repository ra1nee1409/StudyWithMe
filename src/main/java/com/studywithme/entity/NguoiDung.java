package com.studywithme.entity;

import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nguoi_dung", uniqueConstraints = {
    @UniqueConstraint(name = "uq_nguoi_dung_email", columnNames = "email")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung {

    @PrePersist
    @PreUpdate
    public void chuanHoaEmail() {
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "mat_khau", nullable = false, length = 100)
    private String matKhau;

    @Column(name = "ho_ten", nullable = false, length = 50)
    private String hoTen;

    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Column(name = "anh_dai_dien", length = 500)
    private String anhDaiDien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiNguoiDung trangThai = TrangThaiNguoiDung.HOAT_DONG;

    @Column(name = "so_du_vi", precision = 15, scale = 2)
    @Builder.Default
    private java.math.BigDecimal soDuVi = java.math.BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "nguoi_dung_vai_tro",
        joinColumns = @JoinColumn(name = "nguoi_dung_id"),
        inverseJoinColumns = @JoinColumn(name = "vai_tro_id")
    )
    @Builder.Default
    private Set<VaiTro> danhSachVaiTro = new HashSet<>();

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public TrangThaiNguoiDung getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiNguoiDung trangThai) {
        this.trangThai = trangThai;
    }

    public Set<VaiTro> getDanhSachVaiTro() {
        return danhSachVaiTro;
    }

    public void setDanhSachVaiTro(Set<VaiTro> danhSachVaiTro) {
        this.danhSachVaiTro = danhSachVaiTro;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public java.math.BigDecimal getSoDuVi() {
        return soDuVi != null ? soDuVi : java.math.BigDecimal.ZERO;
    }

    public void setSoDuVi(java.math.BigDecimal soDuVi) {
        this.soDuVi = soDuVi != null ? soDuVi : java.math.BigDecimal.ZERO;
    }

    public void addVaiTro(VaiTro vaiTro) {
        if (this.danhSachVaiTro == null) {
            this.danhSachVaiTro = new java.util.HashSet<>();
        } else if (!(this.danhSachVaiTro instanceof java.util.HashSet)) {
            this.danhSachVaiTro = new java.util.HashSet<>(this.danhSachVaiTro);
        }
        this.danhSachVaiTro.add(vaiTro);
    }
}

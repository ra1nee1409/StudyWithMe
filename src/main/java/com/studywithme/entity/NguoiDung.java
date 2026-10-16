package com.studywithme.entity;

import com.studywithme.entity.enums.TrangThaiNguoiDung;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nguoi_dung")
@Getter
@Setter
@Builder
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "anh_dai_dien", length = 500)
    private String anhDaiDien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiNguoiDung trangThai = TrangThaiNguoiDung.HOAT_DONG;

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

    public NguoiDung() {
    }

    public NguoiDung(Long id, String email, String matKhau, String hoTen, String soDienThoai,
                     String anhDaiDien, TrangThaiNguoiDung trangThai, Set<VaiTro> danhSachVaiTro,
                     LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.id = id;
        this.email = email;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.anhDaiDien = anhDaiDien;
        this.trangThai = trangThai != null ? trangThai : TrangThaiNguoiDung.HOAT_DONG;
        this.danhSachVaiTro = danhSachVaiTro != null ? danhSachVaiTro : new HashSet<>();
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }
}

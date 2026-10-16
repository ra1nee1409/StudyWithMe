package com.studywithme.entity;

import com.studywithme.entity.enums.LoaiSanPhamDonHang;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_don_hang")
@Getter
@Setter
@Builder
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id", nullable = false)
    private DonHang donHang;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_san_pham", nullable = false, length = 30)
    private LoaiSanPhamDonHang loaiSanPham; // KHOA_HOC_VIDEO hoặc LOP_HOC_GIA_SU

    @Column(name = "san_pham_id", nullable = false)
    private Long sanPhamId;

    @Column(name = "don_gia", nullable = false, precision = 15, scale = 2)
    private BigDecimal donGia;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(Long id, DonHang donHang, LoaiSanPhamDonHang loaiSanPham, Long sanPhamId, BigDecimal donGia) {
        this.id = id;
        this.donHang = donHang;
        this.loaiSanPham = loaiSanPham;
        this.sanPhamId = sanPhamId;
        this.donGia = donGia;
    }
}

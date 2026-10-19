package com.studywithme.entity;

import com.studywithme.entity.enums.shared.LoaiSanPhamDonHang;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_don_hang")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id", nullable = false)
    private DonHang donHang;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_san_pham", nullable = false, length = 30)
    private LoaiSanPhamDonHang loaiSanPham;

    @Column(name = "san_pham_id", nullable = false)
    private Long sanPhamId;

    @Column(name = "don_gia", nullable = false, precision = 15, scale = 2)
    private BigDecimal donGia;
}

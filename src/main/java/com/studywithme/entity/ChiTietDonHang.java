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

    @Transient
    private String tenSanPham;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public LoaiSanPhamDonHang getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(LoaiSanPhamDonHang loaiSanPham) {
        this.loaiSanPham = loaiSanPham;
    }

    public Long getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(Long sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public String getTenSanPham() {
        if (tenSanPham != null && !tenSanPham.isBlank()) {
            return tenSanPham;
        }
        return loaiSanPham == LoaiSanPhamDonHang.KHOA_HOC_VIDEO ? "Khóa học trực tuyến" : "Lớp học gia sư trực tuyến";
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public static ChiTietDonHangBuilder builder() {
        return new ChiTietDonHangBuilder();
    }

    public static class ChiTietDonHangBuilder {
        private Long id;
        private DonHang donHang;
        private LoaiSanPhamDonHang loaiSanPham;
        private Long sanPhamId;
        private BigDecimal donGia;
        private String tenSanPham;

        public ChiTietDonHangBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ChiTietDonHangBuilder donHang(DonHang donHang) {
            this.donHang = donHang;
            return this;
        }

        public ChiTietDonHangBuilder loaiSanPham(LoaiSanPhamDonHang loaiSanPham) {
            this.loaiSanPham = loaiSanPham;
            return this;
        }

        public ChiTietDonHangBuilder sanPhamId(Long sanPhamId) {
            this.sanPhamId = sanPhamId;
            return this;
        }

        public ChiTietDonHangBuilder donGia(BigDecimal donGia) {
            this.donGia = donGia;
            return this;
        }

        public ChiTietDonHangBuilder tenSanPham(String tenSanPham) {
            this.tenSanPham = tenSanPham;
            return this;
        }

        public ChiTietDonHang build() {
            ChiTietDonHang obj = new ChiTietDonHang();
            obj.setId(this.id);
            obj.setDonHang(this.donHang);
            obj.setLoaiSanPham(this.loaiSanPham);
            obj.setSanPhamId(this.sanPhamId);
            obj.setDonGia(this.donGia);
            obj.setTenSanPham(this.tenSanPham);
            return obj;
        }
    }
}

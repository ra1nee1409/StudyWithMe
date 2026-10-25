package com.studywithme.entity;

import com.studywithme.entity.enums.shared.PhuongThucThanhToan;
import com.studywithme.entity.enums.shared.TrangThaiThanhToan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "don_hang")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_don_hang", nullable = false, unique = true, length = 50)
    private String maDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "tong_tien_thanh_toan", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal tongTienThanhToan = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc_thanh_toan", nullable = false, length = 30)
    @Builder.Default
    private PhuongThucThanhToan phuongThucThanhToan = PhuongThucThanhToan.VNPAY;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_thanh_toan", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiThanhToan trangThaiThanhToan = TrangThaiThanhToan.CHO_THANH_TOAN;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "ngay_tao_don", nullable = false, updatable = false)
    private LocalDateTime ngayTaoDon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public BigDecimal getTongTienThanhToan() {
        return tongTienThanhToan;
    }

    public void setTongTienThanhToan(BigDecimal tongTienThanhToan) {
        this.tongTienThanhToan = tongTienThanhToan;
    }

    public PhuongThucThanhToan getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public TrangThaiThanhToan getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(TrangThaiThanhToan trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public List<ChiTietDonHang> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietDonHang> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    public LocalDateTime getNgayTaoDon() {
        return ngayTaoDon;
    }

    public void setNgayTaoDon(LocalDateTime ngayTaoDon) {
        this.ngayTaoDon = ngayTaoDon;
    }

    public static DonHangBuilder builder() {
        return new DonHangBuilder();
    }

    public static class DonHangBuilder {
        private Long id;
        private String maDonHang;
        private NguoiDung nguoiDung;
        private BigDecimal tongTienThanhToan = BigDecimal.ZERO;
        private PhuongThucThanhToan phuongThucThanhToan = PhuongThucThanhToan.VNPAY;
        private TrangThaiThanhToan trangThaiThanhToan = TrangThaiThanhToan.CHO_THANH_TOAN;
        private LocalDateTime ngayThanhToan;
        private List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();
        private LocalDateTime ngayTaoDon;

        public DonHangBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DonHangBuilder maDonHang(String maDonHang) {
            this.maDonHang = maDonHang;
            return this;
        }

        public DonHangBuilder nguoiDung(NguoiDung nguoiDung) {
            this.nguoiDung = nguoiDung;
            return this;
        }

        public DonHangBuilder tongTienThanhToan(BigDecimal tongTienThanhToan) {
            this.tongTienThanhToan = tongTienThanhToan;
            return this;
        }

        public DonHangBuilder phuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) {
            this.phuongThucThanhToan = phuongThucThanhToan;
            return this;
        }

        public DonHangBuilder trangThaiThanhToan(TrangThaiThanhToan trangThaiThanhToan) {
            this.trangThaiThanhToan = trangThaiThanhToan;
            return this;
        }

        public DonHangBuilder ngayThanhToan(LocalDateTime ngayThanhToan) {
            this.ngayThanhToan = ngayThanhToan;
            return this;
        }

        public DonHangBuilder danhSachChiTiet(List<ChiTietDonHang> danhSachChiTiet) {
            this.danhSachChiTiet = danhSachChiTiet;
            return this;
        }

        public DonHangBuilder ngayTaoDon(LocalDateTime ngayTaoDon) {
            this.ngayTaoDon = ngayTaoDon;
            return this;
        }

        public DonHang build() {
            DonHang dh = new DonHang();
            dh.setId(this.id);
            dh.setMaDonHang(this.maDonHang);
            dh.setNguoiDung(this.nguoiDung);
            dh.setTongTienThanhToan(this.tongTienThanhToan);
            dh.setPhuongThucThanhToan(this.phuongThucThanhToan);
            dh.setTrangThaiThanhToan(this.trangThaiThanhToan);
            dh.setNgayThanhToan(this.ngayThanhToan);
            dh.setDanhSachChiTiet(this.danhSachChiTiet);
            dh.setNgayTaoDon(this.ngayTaoDon);
            return dh;
        }
    }
}

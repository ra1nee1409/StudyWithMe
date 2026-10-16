package com.studywithme.entity;

import com.studywithme.entity.enums.PhuongThucThanhToan;
import com.studywithme.entity.enums.TrangThaiThanhToan;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
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

    public DonHang() {
    }

    public DonHang(Long id, String maDonHang, NguoiDung nguoiDung, BigDecimal tongTienThanhToan,
                   PhuongThucThanhToan phuongThucThanhToan, TrangThaiThanhToan trangThaiThanhToan,
                   LocalDateTime ngayThanhToan, List<ChiTietDonHang> danhSachChiTiet, LocalDateTime ngayTaoDon) {
        this.id = id;
        this.maDonHang = maDonHang;
        this.nguoiDung = nguoiDung;
        this.tongTienThanhToan = tongTienThanhToan != null ? tongTienThanhToan : BigDecimal.ZERO;
        this.phuongThucThanhToan = phuongThucThanhToan != null ? phuongThucThanhToan : PhuongThucThanhToan.VNPAY;
        this.trangThaiThanhToan = trangThaiThanhToan != null ? trangThaiThanhToan : TrangThaiThanhToan.CHO_THANH_TOAN;
        this.ngayThanhToan = ngayThanhToan;
        this.danhSachChiTiet = danhSachChiTiet != null ? danhSachChiTiet : new ArrayList<>();
        this.ngayTaoDon = ngayTaoDon;
    }
}

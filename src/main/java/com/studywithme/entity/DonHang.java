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
    private PhuongThucThanhToan phuongThucThanhToan = PhuongThucThanhToan.PAYOS;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_thanh_toan", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiThanhToan trangThaiThanhToan = TrangThaiThanhToan.CHO_THANH_TOAN;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "order_code_payos")
    private Long orderCodePayos;

    @Column(name = "ma_giao_dich_payos", length = 100)
    private String maGiaoDichPayos;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "ngay_tao_don", nullable = false, updatable = false)
    private LocalDateTime ngayTaoDon;
}

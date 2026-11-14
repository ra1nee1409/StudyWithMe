package com.studywithme.entity;

import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dat_lich_1k1", indexes = {
        @Index(name = "idx_dat_lich_gv_thoigian", columnList = "giang_vien_id, thoiGian_bat_dau, thoiGian_ket_thuc"),
        @Index(name = "idx_dat_lich_hoc_vien", columnList = "hoc_vien_id"),
        @Index(name = "idx_dat_lich_trang_thai", columnList = "trang_thai")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatLich1k1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_dat_lich", nullable = false, unique = true, length = 50)
    private String maDatLich;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_vien_id", nullable = false)
    private NguoiDung hocVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mon_hoc_id")
    private DanhMuc monHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_day_id")
    private LichDayGiaSu lichDayGiaSu;

    @Column(name = "thoiGian_bat_dau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoiGian_ket_thuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "hoc_phi_ca_hoc", nullable = false, precision = 15, scale = 2)
    private BigDecimal hocPhiCaHoc;

    @Column(name = "ty_le_hoa_hong_san", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal tyLeHoaHongSan = BigDecimal.valueOf(20.0);

    @Column(name = "phi_san_khau_tru", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal phiSanKhauTru = BigDecimal.ZERO;

    @Column(name = "thuc_nhan_gia_su", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal thucNhanGiaSu = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiDatLich trangThai = TrangThaiDatLich.CHO_XAC_NHAN;

    @Column(name = "link_phong_hoc", length = 500)
    private String linkPhongHoc;

    @Column(name = "ghi_chu_hoc_vien", columnDefinition = "NVARCHAR(MAX)")
    private String ghiChuHocVien;

    @Column(name = "ly_do_huy", columnDefinition = "NVARCHAR(MAX)")
    private String lyDoHuy;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}

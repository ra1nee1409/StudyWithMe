package com.studywithme.entity;

import com.studywithme.entity.enums.giaovien.TrangThaiLopHoc;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lop_hoc_truc_tuyen")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LopHocTrucTuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danh_muc_id", nullable = false)
    private DanhMuc danhMuc;

    @Column(name = "tieu_de_lop", nullable = false, length = 255)
    private String tieuDeLop;

    @Column(name = "mo_ta_lop", columnDefinition = "TEXT")
    private String moTaLop;

    @Column(name = "si_so_toi_da", nullable = false)
    @Builder.Default
    private Integer siSoToiDa = 1;

    @Column(name = "hoc_phi_moi_hoc_vien", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal hocPhiMoiHocVien = BigDecimal.ZERO;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "lich_hoc_trong_tuan", length = 255)
    private String lichHocTrongTuan; // VD: T2-T4-T6 từ 19:30 - 21:00

    @Column(name = "link_phong_hoc", length = 500)
    private String linkPhongHoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiLopHoc trangThai = TrangThaiLopHoc.DANG_MO_DANG_KY;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "lopHoc", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DangKyLopHoc> danhSachHocVien = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
}

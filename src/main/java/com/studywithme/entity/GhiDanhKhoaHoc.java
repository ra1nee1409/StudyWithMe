package com.studywithme.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "ghi_danh_khoa_hoc",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_nd_khoa_hoc_ghi_danh", columnNames = {"nguoi_dung_id", "khoa_hoc_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GhiDanhKhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khoa_hoc_id", nullable = false)
    private KhoaHoc khoaHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @CreationTimestamp
    @Column(name = "ngay_ghi_danh", nullable = false, updatable = false)
    private LocalDateTime ngayGhiDanh;

    @Column(name = "phan_tram_tien_do", nullable = false)
    @Builder.Default
    private Integer phanTramTienDo = 0;
}

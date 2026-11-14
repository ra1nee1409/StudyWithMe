package com.studywithme.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cuoc_tro_chuyen", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nguoi_dung_1_id", "nguoi_dung_2_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuocTroChuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_1_id", nullable = false)
    private NguoiDung nguoiDung1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_2_id", nullable = false)
    private NguoiDung nguoiDung2;

    @Column(name = "tin_nhan_cuoi", columnDefinition = "NVARCHAR(MAX)")
    private String tinNhanCuoi;

    @Column(name = "thoi_gian_tin_nhan_cuoi")
    private LocalDateTime thoiGianTinNhanCuoi;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}

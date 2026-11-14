package com.studywithme.entity;

import com.studywithme.entity.enums.shared.LoaiTinNhan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_tro_chuyen_id", nullable = false)
    private CuocTroChuyen cuocTroChuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_gui_id", nullable = false)
    private NguoiDung nguoiGui;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tin_nhan", nullable = false, length = 30)
    @Builder.Default
    private LoaiTinNhan loaiTinNhan = LoaiTinNhan.VAN_BAN;

    @Column(name = "du_lieu_dinh_kem", columnDefinition = "NVARCHAR(MAX)")
    private String duLieuDinhKem;

    @Column(name = "da_doc", nullable = false)
    @Builder.Default
    private boolean daDoc = false;

    @CreationTimestamp
    @Column(name = "thoi_gian_gui", updatable = false)
    private LocalDateTime thoiGianGui;
}

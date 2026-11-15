package com.studywithme.entity;

import com.studywithme.entity.enums.shared.LoaiThongBao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ thông báo tức thời (In-app Notifications - WBS 7.1)
 */
@Entity
@Table(name = "thong_bao", indexes = {
        @Index(name = "idx_thongbao_nguoidung", columnList = "nguoi_dung_id"),
        @Index(name = "idx_thongbao_dadoc", columnList = "da_doc")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, length = 1000)
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_thong_bao", length = 50, nullable = false)
    private LoaiThongBao loaiThongBao;

    @Column(name = "duong_dan_lien_ket", length = 500)
    private String duongDanLienKet;

    @Builder.Default
    @Column(name = "da_doc", nullable = false)
    private Boolean daDoc = false;

    @CreationTimestamp
    @Column(name = "thoi_gian_tao", updatable = false)
    private LocalDateTime thoiGianTao;
}

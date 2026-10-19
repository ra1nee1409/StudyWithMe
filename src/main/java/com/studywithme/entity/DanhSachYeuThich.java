package com.studywithme.entity;

import com.studywithme.entity.enums.hocvien.LoaiYeuThich;
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
    name = "danh_sach_yeu_thich",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_nd_loai_muc_tieu", columnNames = {"nguoi_dung_id", "loai_yeu_thich", "muc_tieu_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanhSachYeuThich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_yeu_thich", nullable = false, length = 30)
    private LoaiYeuThich loaiYeuThich;

    @Column(name = "muc_tieu_id", nullable = false)
    private Long mucTieuId;

    @CreationTimestamp
    @Column(name = "ngay_luu", nullable = false, updatable = false)
    private LocalDateTime ngayLuu;
}

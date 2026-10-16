package com.studywithme.entity;

import com.studywithme.entity.enums.LoaiYeuThich;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
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
public class DanhSachYeuThich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_yeu_thich", nullable = false, length = 30)
    private LoaiYeuThich loaiYeuThich; // KHOA_HOC hoặc GIANG_VIEN

    @Column(name = "muc_tieu_id", nullable = false)
    private Long mucTieuId; // Mã KhoaHoc hoặc HoSoGiangVien

    @CreationTimestamp
    @Column(name = "ngay_luu", nullable = false, updatable = false)
    private LocalDateTime ngayLuu;

    public DanhSachYeuThich() {
    }

    public DanhSachYeuThich(Long id, NguoiDung nguoiDung, LoaiYeuThich loaiYeuThich, Long mucTieuId, LocalDateTime ngayLuu) {
        this.id = id;
        this.nguoiDung = nguoiDung;
        this.loaiYeuThich = loaiYeuThich;
        this.mucTieuId = mucTieuId;
        this.ngayLuu = ngayLuu;
    }
}

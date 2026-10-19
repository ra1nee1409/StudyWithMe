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
@Table(name = "danh_gia_nhan_xet")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanhGiaNhanXet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_danh_gia", nullable = false, length = 30)
    private LoaiYeuThich loaiDanhGia;

    @Column(name = "muc_tieu_id", nullable = false)
    private Long mucTieuId;

    @Column(name = "so_sao_danh_gia", nullable = false, columnDefinition = "TINYINT")
    private Integer soSaoDanhGia;

    @Column(name = "noi_dung_nhan_xet", columnDefinition = "TEXT")
    private String noiDungNhanXet;

    @Column(name = "duoc_hien_thi", nullable = false)
    @Builder.Default
    private Boolean duocHienThi = true;

    @CreationTimestamp
    @Column(name = "ngay_danh_gia", nullable = false, updatable = false)
    private LocalDateTime ngayDanhGia;
}

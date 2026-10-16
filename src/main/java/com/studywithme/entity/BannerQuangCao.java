package com.studywithme.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "banner_quang_cao")
@Getter
@Setter
@Builder
public class BannerQuangCao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "link_anh_banner", nullable = false, length = 500)
    private String linkAnhBanner;

    @Column(name = "link_chuyen_huong", length = 500)
    private String linkChuyenHuong;

    @Column(name = "thu_tu_hien_thi", nullable = false)
    @Builder.Default
    private Integer thuTuHienThi = 0;

    @Column(name = "kich_hoat", nullable = false)
    @Builder.Default
    private Boolean kichHoat = true;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    public BannerQuangCao() {
    }

    public BannerQuangCao(Long id, String tieuDe, String linkAnhBanner, String linkChuyenHuong,
                          Integer thuTuHienThi, Boolean kichHoat, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.id = id;
        this.tieuDe = tieuDe;
        this.linkAnhBanner = linkAnhBanner;
        this.linkChuyenHuong = linkChuyenHuong;
        this.thuTuHienThi = thuTuHienThi != null ? thuTuHienThi : 0;
        this.kichHoat = kichHoat != null ? kichHoat : true;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }
}

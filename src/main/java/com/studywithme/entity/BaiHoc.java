package com.studywithme.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bai_hoc")
@Getter
@Setter
@Builder
public class BaiHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chuong_hoc_id", nullable = false)
    private ChuongHoc chuongHoc;

    @Column(name = "tieu_de_bai_hoc", nullable = false, length = 255)
    private String tieuDeBaiHoc;

    @Column(name = "link_video", length = 500)
    private String linkVideo;

    @Column(name = "thoi_luong_giay", nullable = false)
    @Builder.Default
    private Integer thoiLuongGiay = 0;

    @Column(name = "cho_phep_hoc_thu", nullable = false)
    @Builder.Default
    private Boolean choPhepHocThu = false;

    @Column(name = "thu_tu_sap_xep", nullable = false)
    @Builder.Default
    private Integer thuTuSapXep = 1;

    public BaiHoc() {
    }

    public BaiHoc(Long id, ChuongHoc chuongHoc, String tieuDeBaiHoc, String linkVideo,
                  Integer thoiLuongGiay, Boolean choPhepHocThu, Integer thuTuSapXep) {
        this.id = id;
        this.chuongHoc = chuongHoc;
        this.tieuDeBaiHoc = tieuDeBaiHoc;
        this.linkVideo = linkVideo;
        this.thoiLuongGiay = thoiLuongGiay != null ? thoiLuongGiay : 0;
        this.choPhepHocThu = choPhepHocThu != null ? choPhepHocThu : false;
        this.thuTuSapXep = thuTuSapXep != null ? thuTuSapXep : 1;
    }
}

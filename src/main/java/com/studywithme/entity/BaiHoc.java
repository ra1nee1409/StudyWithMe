package com.studywithme.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bai_hoc")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
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
}

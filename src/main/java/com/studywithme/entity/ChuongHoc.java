package com.studywithme.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chuong_hoc")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChuongHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khoa_hoc_id", nullable = false)
    private KhoaHoc khoaHoc;

    @Column(name = "tieu_de_chuong", nullable = false, length = 255)
    private String tieuDeChuong;

    @Column(name = "thu_tu_sap_xep", nullable = false)
    @Builder.Default
    private Integer thuTuSapXep = 1;

    @OneToMany(mappedBy = "chuongHoc", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("thuTuSapXep ASC")
    @Builder.Default
    private List<BaiHoc> danhSachBaiHoc = new ArrayList<>();
}

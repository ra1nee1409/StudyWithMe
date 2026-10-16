package com.studywithme.entity;

import com.studywithme.entity.enums.LoaiDanhMuc;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "danh_muc")
@Getter
@Setter
@Builder
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_danh_muc", nullable = false, length = 100)
    private String tenDanhMuc;

    @Column(name = "duong_dan_slug", nullable = false, unique = true, length = 100)
    private String duongDanSlug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danh_muc_cha_id")
    private DanhMuc danhMucCha;

    @OneToMany(mappedBy = "danhMucCha", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DanhMuc> danhSachDanhMucCon = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_danh_muc", nullable = false, length = 30)
    @Builder.Default
    private LoaiDanhMuc loaiDanhMuc = LoaiDanhMuc.MON_HOC;

    @Column(name = "link_icon", length = 500)
    private String linkIcon;

    @Column(name = "thu_tu_hien_thi", nullable = false)
    @Builder.Default
    private Integer thuTuHienThi = 0;

    @Column(name = "kich_hoat", nullable = false)
    @Builder.Default
    private Boolean kichHoat = true;

    public DanhMuc() {
    }

    public DanhMuc(Long id, String tenDanhMuc, String duongDanSlug, DanhMuc danhMucCha,
                   List<DanhMuc> danhSachDanhMucCon, LoaiDanhMuc loaiDanhMuc, String linkIcon,
                   Integer thuTuHienThi, Boolean kichHoat) {
        this.id = id;
        this.tenDanhMuc = tenDanhMuc;
        this.duongDanSlug = duongDanSlug;
        this.danhMucCha = danhMucCha;
        this.danhSachDanhMucCon = danhSachDanhMucCon != null ? danhSachDanhMucCon : new ArrayList<>();
        this.loaiDanhMuc = loaiDanhMuc != null ? loaiDanhMuc : LoaiDanhMuc.MON_HOC;
        this.linkIcon = linkIcon;
        this.thuTuHienThi = thuTuHienThi != null ? thuTuHienThi : 0;
        this.kichHoat = kichHoat != null ? kichHoat : true;
    }
}

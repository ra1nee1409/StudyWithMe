package com.studywithme.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChuongHoc getChuongHoc() {
        return chuongHoc;
    }

    public void setChuongHoc(ChuongHoc chuongHoc) {
        this.chuongHoc = chuongHoc;
    }

    public String getTieuDeBaiHoc() {
        return tieuDeBaiHoc;
    }

    public void setTieuDeBaiHoc(String tieuDeBaiHoc) {
        this.tieuDeBaiHoc = tieuDeBaiHoc;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public Integer getThoiLuongGiay() {
        return thoiLuongGiay;
    }

    public void setThoiLuongGiay(Integer thoiLuongGiay) {
        this.thoiLuongGiay = thoiLuongGiay;
    }

    public Boolean getChoPhepHocThu() {
        return choPhepHocThu;
    }

    public void setChoPhepHocThu(Boolean choPhepHocThu) {
        this.choPhepHocThu = choPhepHocThu;
    }

    public Integer getThuTuSapXep() {
        return thuTuSapXep;
    }

    public void setThuTuSapXep(Integer thuTuSapXep) {
        this.thuTuSapXep = thuTuSapXep;
    }

    // Alias helper for templates
    public String getTenBaiHoc() {
        return this.tieuDeBaiHoc;
    }

    public Integer getThoiLuongPhut() {
        return (this.thoiLuongGiay != null && this.thoiLuongGiay > 0) ? (this.thoiLuongGiay / 60) : 15;
    }

    public static BaiHocBuilder builder() {
        return new BaiHocBuilder();
    }

    public static class BaiHocBuilder {
        private Long id;
        private ChuongHoc chuongHoc;
        private String tieuDeBaiHoc;
        private String linkVideo;
        private Integer thoiLuongGiay = 0;
        private Boolean choPhepHocThu = false;
        private Integer thuTuSapXep = 1;

        public BaiHocBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BaiHocBuilder chuongHoc(ChuongHoc chuongHoc) {
            this.chuongHoc = chuongHoc;
            return this;
        }

        public BaiHocBuilder tieuDeBaiHoc(String tieuDeBaiHoc) {
            this.tieuDeBaiHoc = tieuDeBaiHoc;
            return this;
        }

        public BaiHocBuilder linkVideo(String linkVideo) {
            this.linkVideo = linkVideo;
            return this;
        }

        public BaiHocBuilder thoiLuongGiay(Integer thoiLuongGiay) {
            this.thoiLuongGiay = thoiLuongGiay;
            return this;
        }

        public BaiHocBuilder choPhepHocThu(Boolean choPhepHocThu) {
            this.choPhepHocThu = choPhepHocThu;
            return this;
        }

        public BaiHocBuilder thuTuSapXep(Integer thuTuSapXep) {
            this.thuTuSapXep = thuTuSapXep;
            return this;
        }

        public BaiHoc build() {
            BaiHoc b = new BaiHoc();
            b.setId(this.id);
            b.setChuongHoc(this.chuongHoc);
            b.setTieuDeBaiHoc(this.tieuDeBaiHoc);
            b.setLinkVideo(this.linkVideo);
            b.setThoiLuongGiay(this.thoiLuongGiay);
            b.setChoPhepHocThu(this.choPhepHocThu);
            b.setThuTuSapXep(this.thuTuSapXep);
            return b;
        }
    }
}

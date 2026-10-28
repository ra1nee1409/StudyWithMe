package com.studywithme.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KhoaHoc getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(KhoaHoc khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public String getTieuDeChuong() {
        return tieuDeChuong;
    }

    public void setTieuDeChuong(String tieuDeChuong) {
        this.tieuDeChuong = tieuDeChuong;
    }

    public Integer getThuTuSapXep() {
        return thuTuSapXep;
    }

    public void setThuTuSapXep(Integer thuTuSapXep) {
        this.thuTuSapXep = thuTuSapXep;
    }

    public List<BaiHoc> getDanhSachBaiHoc() {
        return danhSachBaiHoc;
    }

    public void setDanhSachBaiHoc(List<BaiHoc> danhSachBaiHoc) {
        this.danhSachBaiHoc = danhSachBaiHoc;
    }

    // Alias helper for templates
    public String getTenChuong() {
        return this.tieuDeChuong;
    }

    public static ChuongHocBuilder builder() {
        return new ChuongHocBuilder();
    }

    public static class ChuongHocBuilder {
        private Long id;
        private KhoaHoc khoaHoc;
        private String tieuDeChuong;
        private Integer thuTuSapXep = 1;
        private List<BaiHoc> danhSachBaiHoc = new ArrayList<>();

        public ChuongHocBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ChuongHocBuilder khoaHoc(KhoaHoc khoaHoc) {
            this.khoaHoc = khoaHoc;
            return this;
        }

        public ChuongHocBuilder tieuDeChuong(String tieuDeChuong) {
            this.tieuDeChuong = tieuDeChuong;
            return this;
        }

        public ChuongHocBuilder thuTuSapXep(Integer thuTuSapXep) {
            this.thuTuSapXep = thuTuSapXep;
            return this;
        }

        public ChuongHocBuilder danhSachBaiHoc(List<BaiHoc> danhSachBaiHoc) {
            this.danhSachBaiHoc = danhSachBaiHoc;
            return this;
        }

        public ChuongHoc build() {
            ChuongHoc c = new ChuongHoc();
            c.setId(this.id);
            c.setKhoaHoc(this.khoaHoc);
            c.setTieuDeChuong(this.tieuDeChuong);
            c.setThuTuSapXep(this.thuTuSapXep);
            c.setDanhSachBaiHoc(this.danhSachBaiHoc);
            return c;
        }
    }
}

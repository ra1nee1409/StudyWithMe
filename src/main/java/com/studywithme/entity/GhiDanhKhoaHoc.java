package com.studywithme.entity;

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
    name = "ghi_danh_khoa_hoc",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_nd_khoa_hoc_ghi_danh", columnNames = {"nguoi_dung_id", "khoa_hoc_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GhiDanhKhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khoa_hoc_id", nullable = false)
    private KhoaHoc khoaHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @CreationTimestamp
    @Column(name = "ngay_ghi_danh", nullable = false, updatable = false)
    private LocalDateTime ngayGhiDanh;

    @Column(name = "phan_tram_tien_do", nullable = false)
    @Builder.Default
    private Integer phanTramTienDo = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public KhoaHoc getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(KhoaHoc khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public LocalDateTime getNgayGhiDanh() {
        return ngayGhiDanh;
    }

    public void setNgayGhiDanh(LocalDateTime ngayGhiDanh) {
        this.ngayGhiDanh = ngayGhiDanh;
    }

    public Integer getPhanTramTienDo() {
        return phanTramTienDo;
    }

    public void setPhanTramTienDo(Integer phanTramTienDo) {
        this.phanTramTienDo = phanTramTienDo;
    }

    public static GhiDanhKhoaHocBuilder builder() {
        return new GhiDanhKhoaHocBuilder();
    }

    public static class GhiDanhKhoaHocBuilder {
        private Long id;
        private NguoiDung nguoiDung;
        private KhoaHoc khoaHoc;
        private DonHang donHang;
        private LocalDateTime ngayGhiDanh;
        private Integer phanTramTienDo = 0;

        public GhiDanhKhoaHocBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GhiDanhKhoaHocBuilder nguoiDung(NguoiDung nguoiDung) {
            this.nguoiDung = nguoiDung;
            return this;
        }

        public GhiDanhKhoaHocBuilder khoaHoc(KhoaHoc khoaHoc) {
            this.khoaHoc = khoaHoc;
            return this;
        }

        public GhiDanhKhoaHocBuilder donHang(DonHang donHang) {
            this.donHang = donHang;
            return this;
        }

        public GhiDanhKhoaHocBuilder ngayGhiDanh(LocalDateTime ngayGhiDanh) {
            this.ngayGhiDanh = ngayGhiDanh;
            return this;
        }

        public GhiDanhKhoaHocBuilder phanTramTienDo(Integer phanTramTienDo) {
            this.phanTramTienDo = phanTramTienDo;
            return this;
        }

        public GhiDanhKhoaHoc build() {
            GhiDanhKhoaHoc gd = new GhiDanhKhoaHoc();
            gd.setId(this.id);
            gd.setNguoiDung(this.nguoiDung);
            gd.setKhoaHoc(this.khoaHoc);
            gd.setDonHang(this.donHang);
            gd.setNgayGhiDanh(this.ngayGhiDanh);
            gd.setPhanTramTienDo(this.phanTramTienDo);
            return gd;
        }
    }
}

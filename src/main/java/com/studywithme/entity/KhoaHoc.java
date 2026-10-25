package com.studywithme.entity;

import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.giaovien.TrinhDoKhoaHoc;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "khoa_hoc")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danh_muc_id", nullable = false)
    private DanhMuc danhMuc;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "duong_dan_slug", nullable = false, unique = true, length = 255)
    private String duongDanSlug;

    @Column(name = "mo_ta_chi_tiet", columnDefinition = "LONGTEXT")
    private String moTaChiTiet;

    @Column(name = "anh_dai_dien_khoa_hoc", length = 500)
    private String anhDaiDienKhoaHoc;

    @Column(name = "gia_goc", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal giaGoc = BigDecimal.ZERO;

    @Column(name = "gia_khuyen_mai", precision = 15, scale = 2)
    private BigDecimal giaKhuyenMai;

    @Enumerated(EnumType.STRING)
    @Column(name = "trinh_do", nullable = false, length = 30)
    @Builder.Default
    private TrinhDoKhoaHoc trinhDo = TrinhDoKhoaHoc.MOI_TRINH_DO;

    @Column(name = "the_phan_loai", length = 50)
    private String thePhanLoai; // CAP_1, CAP_2, CAP_3, IELTS, TOEIC

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiKhoaHoc trangThai = TrangThaiKhoaHoc.BAN_NHAP;

    @Column(name = "ly_do_tu_choi", columnDefinition = "TEXT")
    private String lyDoTuChoi;

    @Column(name = "diem_danh_gia_tb", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal diemDanhGiaTrungBinh = BigDecimal.ZERO;

    @Column(name = "tong_so_danh_gia", nullable = false)
    @Builder.Default
    private Integer tongSoDanhGia = 0;

    @Column(name = "tong_so_hoc_vien", nullable = false)
    @Builder.Default
    private Integer tongSoHocVien = 0;

    @OneToMany(mappedBy = "khoaHoc", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("thuTuSapXep ASC")
    @Builder.Default
    private List<ChuongHoc> danhSachChuongHoc = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    // Explicit Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HoSoGiangVien getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(HoSoGiangVien giangVien) {
        this.giangVien = giangVien;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getDuongDanSlug() {
        return duongDanSlug;
    }

    public void setDuongDanSlug(String duongDanSlug) {
        this.duongDanSlug = duongDanSlug;
    }

    public String getMoTaChiTiet() {
        return moTaChiTiet;
    }

    public void setMoTaChiTiet(String moTaChiTiet) {
        this.moTaChiTiet = moTaChiTiet;
    }

    public String getAnhDaiDienKhoaHoc() {
        return anhDaiDienKhoaHoc;
    }

    public void setAnhDaiDienKhoaHoc(String anhDaiDienKhoaHoc) {
        this.anhDaiDienKhoaHoc = anhDaiDienKhoaHoc;
    }

    public BigDecimal getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(BigDecimal giaGoc) {
        this.giaGoc = giaGoc;
    }

    public BigDecimal getGiaKhuyenMai() {
        return giaKhuyenMai;
    }

    public void setGiaKhuyenMai(BigDecimal giaKhuyenMai) {
        this.giaKhuyenMai = giaKhuyenMai;
    }

    public TrinhDoKhoaHoc getTrinhDo() {
        return trinhDo;
    }

    public void setTrinhDo(TrinhDoKhoaHoc trinhDo) {
        this.trinhDo = trinhDo;
    }

    public String getThePhanLoai() {
        return thePhanLoai;
    }

    public void setThePhanLoai(String thePhanLoai) {
        this.thePhanLoai = thePhanLoai;
    }

    public TrangThaiKhoaHoc getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiKhoaHoc trangThai) {
        this.trangThai = trangThai;
    }

    public String getLyDoTuChoi() {
        return lyDoTuChoi;
    }

    public void setLyDoTuChoi(String lyDoTuChoi) {
        this.lyDoTuChoi = lyDoTuChoi;
    }

    public BigDecimal getDiemDanhGiaTrungBinh() {
        return diemDanhGiaTrungBinh;
    }

    public void setDiemDanhGiaTrungBinh(BigDecimal diemDanhGiaTrungBinh) {
        this.diemDanhGiaTrungBinh = diemDanhGiaTrungBinh;
    }

    public Integer getTongSoDanhGia() {
        return tongSoDanhGia;
    }

    public void setTongSoDanhGia(Integer tongSoDanhGia) {
        this.tongSoDanhGia = tongSoDanhGia;
    }

    public Integer getTongSoHocVien() {
        return tongSoHocVien;
    }

    public void setTongSoHocVien(Integer tongSoHocVien) {
        this.tongSoHocVien = tongSoHocVien;
    }

    public List<ChuongHoc> getDanhSachChuongHoc() {
        return danhSachChuongHoc;
    }

    public void setDanhSachChuongHoc(List<ChuongHoc> danhSachChuongHoc) {
        this.danhSachChuongHoc = danhSachChuongHoc;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    // Convenience Alias Methods for templates and services
    public Integer getSoHocVien() {
        return this.tongSoHocVien;
    }

    public void setSoHocVien(Integer soHocVien) {
        this.tongSoHocVien = soHocVien;
    }

    public String getDuongDanThanThien() {
        return this.duongDanSlug;
    }

    public String getHinhDaiDien() {
        return this.anhDaiDienKhoaHoc;
    }

    public String getAnhDaiDien() {
        return this.anhDaiDienKhoaHoc;
    }

    public static KhoaHocBuilder builder() {
        return new KhoaHocBuilder();
    }

    public static class KhoaHocBuilder {
        private Long id;
        private HoSoGiangVien giangVien;
        private DanhMuc danhMuc;
        private String tieuDe;
        private String duongDanSlug;
        private String moTaChiTiet;
        private String anhDaiDienKhoaHoc;
        private BigDecimal giaGoc = BigDecimal.ZERO;
        private BigDecimal giaKhuyenMai;
        private TrinhDoKhoaHoc trinhDo = TrinhDoKhoaHoc.MOI_TRINH_DO;
        private String thePhanLoai;
        private TrangThaiKhoaHoc trangThai = TrangThaiKhoaHoc.BAN_NHAP;
        private String lyDoTuChoi;
        private BigDecimal diemDanhGiaTrungBinh = BigDecimal.ZERO;
        private Integer tongSoDanhGia = 0;
        private Integer tongSoHocVien = 0;
        private List<ChuongHoc> danhSachChuongHoc = new ArrayList<>();
        private LocalDateTime ngayTao;
        private LocalDateTime ngayCapNhat;

        public KhoaHocBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public KhoaHocBuilder giangVien(HoSoGiangVien giangVien) {
            this.giangVien = giangVien;
            return this;
        }

        public KhoaHocBuilder danhMuc(DanhMuc danhMuc) {
            this.danhMuc = danhMuc;
            return this;
        }

        public KhoaHocBuilder tieuDe(String tieuDe) {
            this.tieuDe = tieuDe;
            return this;
        }

        public KhoaHocBuilder duongDanSlug(String duongDanSlug) {
            this.duongDanSlug = duongDanSlug;
            return this;
        }

        public KhoaHocBuilder moTaChiTiet(String moTaChiTiet) {
            this.moTaChiTiet = moTaChiTiet;
            return this;
        }

        public KhoaHocBuilder anhDaiDienKhoaHoc(String anhDaiDienKhoaHoc) {
            this.anhDaiDienKhoaHoc = anhDaiDienKhoaHoc;
            return this;
        }

        public KhoaHocBuilder giaGoc(BigDecimal giaGoc) {
            this.giaGoc = giaGoc;
            return this;
        }

        public KhoaHocBuilder giaKhuyenMai(BigDecimal giaKhuyenMai) {
            this.giaKhuyenMai = giaKhuyenMai;
            return this;
        }

        public KhoaHocBuilder trinhDo(TrinhDoKhoaHoc trinhDo) {
            this.trinhDo = trinhDo;
            return this;
        }

        public KhoaHocBuilder thePhanLoai(String thePhanLoai) {
            this.thePhanLoai = thePhanLoai;
            return this;
        }

        public KhoaHocBuilder trangThai(TrangThaiKhoaHoc trangThai) {
            this.trangThai = trangThai;
            return this;
        }

        public KhoaHocBuilder lyDoTuChoi(String lyDoTuChoi) {
            this.lyDoTuChoi = lyDoTuChoi;
            return this;
        }

        public KhoaHocBuilder diemDanhGiaTrungBinh(BigDecimal diemDanhGiaTrungBinh) {
            this.diemDanhGiaTrungBinh = diemDanhGiaTrungBinh;
            return this;
        }

        public KhoaHocBuilder tongSoDanhGia(Integer tongSoDanhGia) {
            this.tongSoDanhGia = tongSoDanhGia;
            return this;
        }

        public KhoaHocBuilder tongSoHocVien(Integer tongSoHocVien) {
            this.tongSoHocVien = tongSoHocVien;
            return this;
        }

        public KhoaHocBuilder danhSachChuongHoc(List<ChuongHoc> danhSachChuongHoc) {
            this.danhSachChuongHoc = danhSachChuongHoc;
            return this;
        }

        public KhoaHocBuilder ngayTao(LocalDateTime ngayTao) {
            this.ngayTao = ngayTao;
            return this;
        }

        public KhoaHocBuilder ngayCapNhat(LocalDateTime ngayCapNhat) {
            this.ngayCapNhat = ngayCapNhat;
            return this;
        }

        public KhoaHoc build() {
            KhoaHoc kh = new KhoaHoc();
            kh.setId(this.id);
            kh.setGiangVien(this.giangVien);
            kh.setDanhMuc(this.danhMuc);
            kh.setTieuDe(this.tieuDe);
            kh.setDuongDanSlug(this.duongDanSlug);
            kh.setMoTaChiTiet(this.moTaChiTiet);
            kh.setAnhDaiDienKhoaHoc(this.anhDaiDienKhoaHoc);
            kh.setGiaGoc(this.giaGoc);
            kh.setGiaKhuyenMai(this.giaKhuyenMai);
            kh.setTrinhDo(this.trinhDo);
            kh.setThePhanLoai(this.thePhanLoai);
            kh.setTrangThai(this.trangThai);
            kh.setLyDoTuChoi(this.lyDoTuChoi);
            kh.setDiemDanhGiaTrungBinh(this.diemDanhGiaTrungBinh);
            kh.setTongSoDanhGia(this.tongSoDanhGia);
            kh.setTongSoHocVien(this.tongSoHocVien);
            kh.setDanhSachChuongHoc(this.danhSachChuongHoc);
            kh.setNgayTao(this.ngayTao);
            kh.setNgayCapNhat(this.ngayCapNhat);
            return kh;
        }
    }
}

package com.studywithme.entity;

import com.studywithme.entity.enums.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.TrinhDoKhoaHoc;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
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

    public KhoaHoc() {
    }

    public KhoaHoc(Long id, HoSoGiangVien giangVien, DanhMuc danhMuc, String tieuDe, String duongDanSlug,
                   String moTaChiTiet, String anhDaiDienKhoaHoc, BigDecimal giaGoc, BigDecimal giaKhuyenMai,
                   TrinhDoKhoaHoc trinhDo, String thePhanLoai, TrangThaiKhoaHoc trangThai, String lyDoTuChoi,
                   BigDecimal diemDanhGiaTrungBinh, Integer tongSoDanhGia, Integer tongSoHocVien,
                   List<ChuongHoc> danhSachChuongHoc, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.id = id;
        this.giangVien = giangVien;
        this.danhMuc = danhMuc;
        this.tieuDe = tieuDe;
        this.duongDanSlug = duongDanSlug;
        this.moTaChiTiet = moTaChiTiet;
        this.anhDaiDienKhoaHoc = anhDaiDienKhoaHoc;
        this.giaGoc = giaGoc != null ? giaGoc : BigDecimal.ZERO;
        this.giaKhuyenMai = giaKhuyenMai;
        this.trinhDo = trinhDo != null ? trinhDo : TrinhDoKhoaHoc.MOI_TRINH_DO;
        this.thePhanLoai = thePhanLoai;
        this.trangThai = trangThai != null ? trangThai : TrangThaiKhoaHoc.BAN_NHAP;
        this.lyDoTuChoi = lyDoTuChoi;
        this.diemDanhGiaTrungBinh = diemDanhGiaTrungBinh != null ? diemDanhGiaTrungBinh : BigDecimal.ZERO;
        this.tongSoDanhGia = tongSoDanhGia != null ? tongSoDanhGia : 0;
        this.tongSoHocVien = tongSoHocVien != null ? tongSoHocVien : 0;
        this.danhSachChuongHoc = danhSachChuongHoc != null ? danhSachChuongHoc : new ArrayList<>();
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }
}

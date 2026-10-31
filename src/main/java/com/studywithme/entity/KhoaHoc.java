package com.studywithme.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "nguoiDung"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danh_muc_id", nullable = false)
    private DanhMuc danhMuc;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "duong_dan_slug", nullable = false, unique = true, length = 255)
    private String duongDanSlug;

    @Column(name = "mo_ta_chi_tiet", columnDefinition = "NVARCHAR(MAX)")
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

    @Column(name = "ly_do_tu_choi", columnDefinition = "NVARCHAR(MAX)")
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
}

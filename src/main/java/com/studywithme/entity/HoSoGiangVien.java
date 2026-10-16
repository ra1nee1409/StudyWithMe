package com.studywithme.entity;

import com.studywithme.entity.enums.TrangThaiGiangVien;
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
@Table(name = "ho_so_giang_vien")
@Getter
@Setter
@Builder
public class HoSoGiangVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false, unique = true)
    private NguoiDung nguoiDung;

    @Column(name = "tieu_de_chuyen_mon", length = 255)
    private String tieuDeChuyenMon;

    @Column(name = "gioi_thieu_ban_than", columnDefinition = "TEXT")
    private String gioiThieuBanThan;

    @Column(name = "link_video_day_thu", length = 500)
    private String linkVideoDayThu;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_duyet", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiGiangVien trangThaiDuyet = TrangThaiGiangVien.CHO_DUYET;

    @Column(name = "ly_do_tu_choi", columnDefinition = "TEXT")
    private String lyDoTuChoi;

    @Column(name = "so_du_vi", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal soDuVi = BigDecimal.ZERO;

    @Column(name = "ten_ngan_hang", length = 100)
    private String tenNganHang;

    @Column(name = "so_tai_khoan_ngan_hang", length = 50)
    private String soTaiKhoanNganHang;

    @Column(name = "ten_chu_tai_khoan", length = 100)
    private String tenChuTaiKhoan;

    @OneToMany(mappedBy = "giangVien", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChungChiGiangVien> danhSachChungChi = new ArrayList<>();

    @OneToMany(mappedBy = "giangVien", cascade = CascadeType.ALL)
    @Builder.Default
    private List<LichSuViTien> lichSuGiaoDichVi = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    public HoSoGiangVien() {
    }

    public HoSoGiangVien(Long id, NguoiDung nguoiDung, String tieuDeChuyenMon, String gioiThieuBanThan,
                        String linkVideoDayThu, TrangThaiGiangVien trangThaiDuyet, String lyDoTuChoi,
                        BigDecimal soDuVi, String tenNganHang, String soTaiKhoanNganHang,
                        String tenChuTaiKhoan, List<ChungChiGiangVien> danhSachChungChi,
                        List<LichSuViTien> lichSuGiaoDichVi, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.id = id;
        this.nguoiDung = nguoiDung;
        this.tieuDeChuyenMon = tieuDeChuyenMon;
        this.gioiThieuBanThan = gioiThieuBanThan;
        this.linkVideoDayThu = linkVideoDayThu;
        this.trangThaiDuyet = trangThaiDuyet != null ? trangThaiDuyet : TrangThaiGiangVien.CHO_DUYET;
        this.lyDoTuChoi = lyDoTuChoi;
        this.soDuVi = soDuVi != null ? soDuVi : BigDecimal.ZERO;
        this.tenNganHang = tenNganHang;
        this.soTaiKhoanNganHang = soTaiKhoanNganHang;
        this.tenChuTaiKhoan = tenChuTaiKhoan;
        this.danhSachChungChi = danhSachChungChi != null ? danhSachChungChi : new ArrayList<>();
        this.lichSuGiaoDichVi = lichSuGiaoDichVi != null ? lichSuGiaoDichVi : new ArrayList<>();
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }
}

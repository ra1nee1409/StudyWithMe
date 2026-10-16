package com.studywithme.entity;

import com.studywithme.entity.enums.LoaiChungChi;
import com.studywithme.entity.enums.TrangThaiXacThuc;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chung_chi_giang_vien")
@Getter
@Setter
@Builder
public class ChungChiGiangVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_chung_chi", nullable = false, length = 30)
    private LoaiChungChi loaiChungChi;

    @Column(name = "ten_chung_chi", nullable = false, length = 255)
    private String tenChungChi;

    @Column(name = "diem_so_hoac_xep_loai", length = 100)
    private String diemSoHoacXepLoai;

    @Column(name = "noi_cap", length = 255)
    private String noiCap;

    @Column(name = "ngay_cap")
    private LocalDate ngayCap;

    @Column(name = "link_anh_chung_chi", nullable = false, length = 500)
    private String linkAnhChungChi;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_xac_thuc", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiXacThuc trangThaiXacThuc = TrangThaiXacThuc.CHO_XAC_THUC;

    @Column(name = "ly_do_tu_choi", columnDefinition = "TEXT")
    private String lyDoTuChoi;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    public ChungChiGiangVien() {
    }

    public ChungChiGiangVien(Long id, HoSoGiangVien giangVien, LoaiChungChi loaiChungChi, String tenChungChi,
                            String diemSoHoacXepLoai, String noiCap, LocalDate ngayCap, String linkAnhChungChi,
                            TrangThaiXacThuc trangThaiXacThuc, String lyDoTuChoi,
                            LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.id = id;
        this.giangVien = giangVien;
        this.loaiChungChi = loaiChungChi;
        this.tenChungChi = tenChungChi;
        this.diemSoHoacXepLoai = diemSoHoacXepLoai;
        this.noiCap = noiCap;
        this.ngayCap = ngayCap;
        this.linkAnhChungChi = linkAnhChungChi;
        this.trangThaiXacThuc = trangThaiXacThuc != null ? trangThaiXacThuc : TrangThaiXacThuc.CHO_XAC_THUC;
        this.lyDoTuChoi = lyDoTuChoi;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }
}

package com.studywithme.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "tien_do_bai_hoc",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_nd_bai_hoc_tien_do", columnNames = {"nguoi_dung_id", "bai_hoc_id"})
    }
)
@Getter
@Setter
@Builder
public class TienDoBaiHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bai_hoc_id", nullable = false)
    private BaiHoc baiHoc;

    @Column(name = "da_hoan_thanh", nullable = false)
    @Builder.Default
    private Boolean daHoanThanh = false;

    @Column(name = "giay_dung_lai", nullable = false)
    @Builder.Default
    private Integer giayDungLai = 0;

    @Column(name = "thoi_gian_hoan_thanh")
    private LocalDateTime thoiGianHoanThanh;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    public TienDoBaiHoc() {
    }

    public TienDoBaiHoc(Long id, NguoiDung nguoiDung, BaiHoc baiHoc, Boolean daHoanThanh,
                        Integer giayDungLai, LocalDateTime thoiGianHoanThanh, LocalDateTime ngayCapNhat) {
        this.id = id;
        this.nguoiDung = nguoiDung;
        this.baiHoc = baiHoc;
        this.daHoanThanh = daHoanThanh != null ? daHoanThanh : false;
        this.giayDungLai = giayDungLai != null ? giayDungLai : 0;
        this.thoiGianHoanThanh = thoiGianHoanThanh;
        this.ngayCapNhat = ngayCapNhat;
    }
}

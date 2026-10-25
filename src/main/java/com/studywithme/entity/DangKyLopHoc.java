package com.studywithme.entity;

import com.studywithme.entity.enums.hocvien.TrangThaiThamGiaLop;
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
    name = "dang_ky_lop_hoc",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_hoc_vien_lop_hoc", columnNames = {"hoc_vien_id", "lop_hoc_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DangKyLopHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lop_hoc_id", nullable = false)
    private LopHocTrucTuyen lopHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_vien_id", nullable = false)
    private NguoiDung hocVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_tham_gia", nullable = false, length = 30)
    @Builder.Default
    private TrangThaiThamGiaLop trangThaiThamGia = TrangThaiThamGiaLop.DA_XAC_NHAN;

    @CreationTimestamp
    @Column(name = "ngay_dang_ky", nullable = false, updatable = false)
    private LocalDateTime ngayDangKy;
}

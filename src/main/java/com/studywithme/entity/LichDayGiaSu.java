package com.studywithme.entity;

import com.studywithme.entity.enums.giaovien.LoaiLichDay;
import com.studywithme.entity.enums.giaovien.ThuTrongTuan;
import com.studywithme.entity.enums.giaovien.TrangThaiLichDay;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "lich_day_gia_su")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichDayGiaSu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @Enumerated(EnumType.STRING)
    @Column(name = "thu_trong_tuan", nullable = false, length = 20)
    private ThuTrongTuan thuTrongTuan;

    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime gioKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_lich", nullable = false, length = 20)
    @Builder.Default
    private LoaiLichDay loaiLich = LoaiLichDay.CO_DINH;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiLichDay trangThai = TrangThaiLichDay.CON_TRONG;

    @Column(name = "hoc_phi_moi_gio", precision = 15, scale = 2)
    private BigDecimal hocPhiMoiGio;

    @Column(name = "ghi_chu", columnDefinition = "NVARCHAR(MAX)")
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}

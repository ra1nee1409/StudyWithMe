package com.studywithme.entity;

import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_vi_tien")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichSuViTien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    private HoSoGiangVien giangVien;

    @Column(name = "so_tien_bien_dong", nullable = false, precision = 15, scale = 2)
    private BigDecimal soTienBienDong;

    @Column(name = "so_du_truoc", nullable = false, precision = 15, scale = 2)
    private BigDecimal soDuTruoc;

    @Column(name = "so_du_sau", nullable = false, precision = 15, scale = 2)
    private BigDecimal soDuSau;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giao_dich", nullable = false, length = 40)
    private LoaiGiaoDichVi loaiGiaoDich;

    @Column(name = "ma_tham_chieu")
    private Long maThamChieu;

    @Column(name = "mo_ta_giao_dich", length = 255)
    private String moTaGiaoDich;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
}

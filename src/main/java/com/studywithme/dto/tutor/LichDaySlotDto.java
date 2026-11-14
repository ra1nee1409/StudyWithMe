package com.studywithme.dto.tutor;

import com.studywithme.entity.LichDayGiaSu;
import com.studywithme.entity.enums.giaovien.LoaiLichDay;
import com.studywithme.entity.enums.giaovien.ThuTrongTuan;
import com.studywithme.entity.enums.giaovien.TrangThaiLichDay;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichDaySlotDto {

    private Long id;
    private Long giangVienId;
    private String tenGiangVien;
    private ThuTrongTuan thuTrongTuan;
    private String thuTrongTuanMoTa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private LoaiLichDay loaiLich;
    private TrangThaiLichDay trangThai;
    private BigDecimal hocPhiMoiGio;
    private String ghiChu;

    public static LichDaySlotDto fromEntity(LichDayGiaSu entity) {
        if (entity == null) return null;
        String tenGv = null;
        Long gvId = null;
        if (entity.getGiangVien() != null) {
            gvId = entity.getGiangVien().getId();
            if (entity.getGiangVien().getNguoiDung() != null) {
                tenGv = entity.getGiangVien().getNguoiDung().getHoTen();
            }
        }

        return LichDaySlotDto.builder()
                .id(entity.getId())
                .giangVienId(gvId)
                .tenGiangVien(tenGv)
                .thuTrongTuan(entity.getThuTrongTuan())
                .thuTrongTuanMoTa(entity.getThuTrongTuan() != null ? entity.getThuTrongTuan().getMoTa() : "")
                .gioBatDau(entity.getGioBatDau())
                .gioKetThuc(entity.getGioKetThuc())
                .loaiLich(entity.getLoaiLich())
                .trangThai(entity.getTrangThai())
                .hocPhiMoiGio(entity.getHocPhiMoiGio())
                .ghiChu(entity.getGhiChu())
                .build();
    }
}

package com.studywithme.dto.tutor;

import com.studywithme.entity.enums.giaovien.LoaiLichDay;
import com.studywithme.entity.enums.giaovien.ThuTrongTuan;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichDaySlotRequest {

    private Long id;

    @NotNull(message = "Thứ trong tuần không được để trống")
    private ThuTrongTuan thuTrongTuan;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime gioBatDau;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime gioKetThuc;

    @Builder.Default
    private LoaiLichDay loaiLich = LoaiLichDay.CO_DINH;

    private BigDecimal hocPhiMoiGio;

    private String ghiChu;
}

package com.studywithme.dto.tutor;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatCaHoc1k1Request {

    @NotNull(message = "ID gia sư không được để trống")
    private Long giangVienId;

    private Long monHocId;

    private Long lichDayId;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Future(message = "Thời gian bắt đầu ca học phải ở tương lai")
    private LocalDateTime thoiGianBatDau;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime thoiGianKetThuc;

    @NotNull(message = "Học phí ca học không được để trống")
    private BigDecimal hocPhiCaHoc;

    private String ghiChuHocVien;
}

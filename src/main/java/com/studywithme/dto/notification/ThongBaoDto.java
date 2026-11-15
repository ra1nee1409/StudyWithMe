package com.studywithme.dto.notification;

import com.studywithme.entity.enums.shared.LoaiThongBao;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoDto {
    private Long id;
    private String tieuDe;
    private String noiDung;
    private LoaiThongBao loaiThongBao;
    private String duongDanLienKet;
    private Boolean daDoc;
    private LocalDateTime thoiGianTao;
    private String thoiGianTaoFormatted;
}

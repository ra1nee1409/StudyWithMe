package com.studywithme.dto.chat;

import com.studywithme.entity.enums.shared.LoaiTinNhan;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinNhanDto {

    private String id;
    private String cuocTroChuyenId;
    private Long nguoiGuiId;
    private String tenNguoiGui;
    private String avatarNguoiGui;
    private Long nguoiNhanId;
    private String noiDung;
    private LoaiTinNhan loaiTinNhan;
    private Map<String, Object> duLieuDinhKem;
    private boolean daDoc;
    private LocalDateTime thoiGianGui;
    private boolean laTinNhanCuaToi;
}

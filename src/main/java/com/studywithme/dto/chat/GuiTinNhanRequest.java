package com.studywithme.dto.chat;

import com.studywithme.entity.enums.shared.LoaiTinNhan;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiTinNhanRequest {

    @NotNull(message = "Người nhận không được để trống")
    private Long nguoiNhanId;

    private String noiDung;

    @Builder.Default
    private LoaiTinNhan loaiTinNhan = LoaiTinNhan.VAN_BAN;

    private Map<String, Object> duLieuDinhKem;
}

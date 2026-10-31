package com.studywithme.dto.tutor;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoChuongHocRequest {

    @NotBlank(message = "Tiêu đề chương học không được để trống")
    private String tieuDeChuong;

    private Integer thuTuSapXep;
}


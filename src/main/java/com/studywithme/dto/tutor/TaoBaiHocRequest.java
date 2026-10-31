package com.studywithme.dto.tutor;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoBaiHocRequest {

    @NotBlank(message = "Tiêu đề bài học không được để trống")
    private String tieuDeBaiHoc;

    @NotBlank(message = "Đường dẫn video bài học không được để trống")
    private String linkVideo;

    @Builder.Default
    private Integer thoiLuongGiay = 600;

    @Builder.Default
    private Boolean choPhepHocThu = false;

    private Integer thuTuSapXep;
}


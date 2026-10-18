package com.studywithme.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {

    @NotBlank(message = "Tiêu đề banner không được để trống")
    private String tieuDe;

    private String linkAnhBanner;

    private String linkChuyenHuong;

    @Builder.Default
    private Integer thuTuHienThi = 0;

    @Builder.Default
    private Boolean kichHoat = true;
}

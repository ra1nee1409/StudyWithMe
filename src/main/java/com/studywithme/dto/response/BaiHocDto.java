package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiHocDto {
    private Long id;
    private String tieuDeBaiHoc;
    private String linkVideo;
    private Integer thoiLuongGiay;
    private Boolean choPhepHocThu;
    private Integer thuTuSapXep;
}

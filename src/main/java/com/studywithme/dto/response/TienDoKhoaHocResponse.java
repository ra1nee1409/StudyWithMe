package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TienDoKhoaHocResponse {

    private Long baiHocId;
    private Boolean daHoanThanh;
    private Integer phanTramTienDo;
    private Long soBaiDaHoanThanh;
    private Long tongSoBaiHoc;
    private String thongBao;
}


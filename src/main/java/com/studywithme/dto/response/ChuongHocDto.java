package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChuongHocDto {
    private Long id;
    private String tieuDeChuong;
    private Integer thuTuSapXep;
    @Builder.Default
    private List<BaiHocDto> danhSachBaiHoc = new ArrayList<>();
}

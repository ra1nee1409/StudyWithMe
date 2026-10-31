package com.studywithme.dto.response;

import com.studywithme.entity.BaiHoc;
import com.studywithme.entity.ChuongHoc;
import com.studywithme.entity.KhoaHoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhongHocDto {

    private KhoaHoc khoaHoc;
    private List<ChuongHoc> danhSachChuongHoc;
    private BaiHoc baiHocHienTai;
    private BaiHoc baiHocTruoc;
    private BaiHoc baiHocTiepTheo;

    @Builder.Default
    private Set<Long> tapHopBaiHocDaHoanThanh = new HashSet<>();

    @Builder.Default
    private Integer phanTramTienDo = 0;

    @Builder.Default
    private Long soBaiDaHoanThanh = 0L;

    @Builder.Default
    private Long tongSoBaiHoc = 0L;

    private String embedVideoUrl;
}


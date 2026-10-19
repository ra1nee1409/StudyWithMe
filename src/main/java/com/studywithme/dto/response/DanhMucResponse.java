package com.studywithme.dto.response;

import com.studywithme.entity.enums.shared.LoaiDanhMuc;
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
public class DanhMucResponse {

    private Long id;
    private String tenDanhMuc;
    private String duongDanSlug;
    private Long danhMucChaId;
    private String tenDanhMucCha;
    private LoaiDanhMuc loaiDanhMuc;
    private String loaiDanhMucTen;
    private String linkIcon;
    private Integer thuTuHienThi;
    private Boolean kichHoat;
    private long soKhoaHoc;
    private long soLopHoc;

    @Builder.Default
    private List<DanhMucResponse> danhSachDanhMucCon = new ArrayList<>();
}

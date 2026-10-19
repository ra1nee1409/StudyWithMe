package com.studywithme.dto.request;

import com.studywithme.entity.enums.shared.LoaiDanhMuc;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanhMucRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100, message = "Tên danh mục tối đa 100 ký tự")
    private String tenDanhMuc;

    @Size(max = 100, message = "Đường dẫn slug tối đa 100 ký tự")
    private String duongDanSlug;

    private Long danhMucChaId;

    @Builder.Default
    private LoaiDanhMuc loaiDanhMuc = LoaiDanhMuc.MON_HOC;

    @Size(max = 500, message = "Biểu tượng tối đa 500 ký tự")
    private String linkIcon;

    @Builder.Default
    private Integer thuTuHienThi = 0;

    @Builder.Default
    private Boolean kichHoat = true;
}

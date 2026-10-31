package com.studywithme.dto.tutor;

import com.studywithme.entity.enums.giaovien.TrinhDoKhoaHoc;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatKhoaHocRequest {

    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    private String tieuDe;

    @NotNull(message = "Vui lòng chọn danh mục môn học")
    private Long danhMucId;

    private String thePhanLoai;

    @Builder.Default
    private TrinhDoKhoaHoc trinhDo = TrinhDoKhoaHoc.MOI_TRINH_DO;

    @NotNull(message = "Giá gốc không được để trống")
    @PositiveOrZero(message = "Giá gốc phải lớn hơn hoặc bằng 0")
    private BigDecimal giaGoc;

    private BigDecimal giaKhuyenMai;

    private String anhDaiDienKhoaHoc;

    private String moTaChiTiet;
}


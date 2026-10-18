package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopKhoaHocDto {
    private Long id;
    private String tieuDe;
    private String duongDanSlug;
    private String anhDaiDien;
    private String tenGiangVien;
    private BigDecimal giaGoc;
    private BigDecimal giaHienTai;
    private Integer soHocVien;
    private BigDecimal tongDoanhThuUocTinh;
}

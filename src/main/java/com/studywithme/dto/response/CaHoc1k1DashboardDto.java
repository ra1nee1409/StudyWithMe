package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaHoc1k1DashboardDto {
    private Long id;
    private String tieuDeLop;
    private String tenGiaSu;
    private String emailGiaSu;
    private String tenDanhMuc;
    private BigDecimal hocPhi;
    private String lichHocTrongTuan;
    private String linkPhongHoc;
    private String trangThai;
    private Integer siSoToiDa;
    private Integer soHocVienHienTai;
    private LocalDate ngayBatDau;
}


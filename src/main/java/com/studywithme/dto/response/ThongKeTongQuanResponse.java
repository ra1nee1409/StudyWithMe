package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeTongQuanResponse {
    // 4 Thẻ KPI chính
    private BigDecimal tongDoanhThu;
    private long tongNguoiDung;
    private long tongKhoaHocDangHoatDong; // tương thích ngược
    private long tongCaHoc1k1;            // KPI 1-1
    private long tongGiaSuDaDuyet;        // KPI 1-1
    private long lenhRutTienChoDuyet;
    private long hoSoGiangVienChoDuyet;
    private long tongYeuCauChoDuyet;

    // Chi tiết 1-1 cho Dashboard Admin mới
    @Builder.Default
    private List<TopGiaSuDto> topGiaSu = new ArrayList<>();

    @Builder.Default
    private List<CaHoc1k1DashboardDto> caHocGanDay = new ArrayList<>();

    // Chi tiết phụ trợ cho Dashboard
    @Builder.Default
    private List<TopKhoaHocDto> topKhoaHoc = new ArrayList<>();

    @Builder.Default
    private List<GiangVienMoiDto> giangVienMoi = new ArrayList<>();
}

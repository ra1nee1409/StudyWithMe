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
    private long tongKhoaHocDangHoatDong;
    private long lenhRutTienChoDuyet;
    private long hoSoGiangVienChoDuyet;
    private long tongYeuCauChoDuyet;

    // Chi tiết phụ trợ cho Dashboard
    @Builder.Default
    private List<TopKhoaHocDto> topKhoaHoc = new ArrayList<>();

    @Builder.Default
    private List<GiangVienMoiDto> giangVienMoi = new ArrayList<>();
}

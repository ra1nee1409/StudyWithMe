package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoanhThuThangResponse {
    private int nam;
    private BigDecimal tongDoanhThuNam;
    private List<ThangDoanhThu> danhSachThang;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThangDoanhThu {
        private int thang;
        private String nhanThang;
        private BigDecimal doanhThu;
    }
}

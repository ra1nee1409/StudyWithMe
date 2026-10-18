package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TyLeDanhMucResponse {
    private long tongLuotHocVien;
    private List<NhomDanhMucItem> danhSachTyLe;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NhomDanhMucItem {
        private String maNhom;
        private String tenNhom;
        private long soLuongHocVien;
        private double tyLePhanTram;
        private String mauSac;
    }
}

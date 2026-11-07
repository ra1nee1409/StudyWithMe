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
public class TopGiaSuDto {
    private Long id;
    private Long nguoiDungId;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String tieuDeChuyenMon;
    private String anhDaiDien;
    private Long soCaHoc;
    private Long soHocVienDangKy;
    private BigDecimal tongThuNhapUocTinh;
    private Double diemDanhGia;
}


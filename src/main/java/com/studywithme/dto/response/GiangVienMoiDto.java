package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiangVienMoiDto {
    private Long id;
    private Long nguoiDungId;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String tieuDeChuyenMon;
    private String anhDaiDien;
    private String trangThaiDuyet;
    private LocalDateTime ngayDangKy;
}

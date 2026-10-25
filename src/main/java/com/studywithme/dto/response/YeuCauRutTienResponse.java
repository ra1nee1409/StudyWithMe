package com.studywithme.dto.response;

import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauRutTienResponse {

    private Long id;
    private Long giangVienId;
    private String tenGiangVien;
    private String emailGiangVien;
    private String soDienThoai;
    private String anhDaiDien;
    private BigDecimal soTienRut;
    private BigDecimal soDuViHienTai;
    private String tenNganHang;
    private String soTaiKhoan;
    private String tenChuTaiKhoan;
    private TrangThaiRutTien trangThai;
    private String ghiChuAdmin;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayXuLy;
}

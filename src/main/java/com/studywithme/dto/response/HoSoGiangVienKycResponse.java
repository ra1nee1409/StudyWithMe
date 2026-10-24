package com.studywithme.dto.response;

import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoSoGiangVienKycResponse {
    private Long id;
    private Long nguoiDungId;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String anhDaiDien;
    private String tieuDeChuyenMon;
    private String gioiThieuBanThan;
    private String linkVideoDayThu;
    private TrangThaiGiangVien trangThaiDuyet;
    private String lyDoTuChoi;
    private BigDecimal soDuVi;
    private String tenNganHang;
    private String soTaiKhoanNganHang;
    private String tenChuTaiKhoan;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    @Builder.Default
    private List<ChungChiDto> danhSachChungChi = new ArrayList<>();
}

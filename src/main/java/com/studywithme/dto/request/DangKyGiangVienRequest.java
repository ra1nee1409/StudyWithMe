package com.studywithme.dto.request;

import com.studywithme.entity.enums.giaovien.LoaiChungChi;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DangKyGiangVienRequest {

    @NotBlank(message = "Tiêu đề chuyên môn không được để trống")
    private String tieuDeChuyenMon;

    @NotBlank(message = "Giới thiệu bản thân và phương pháp giảng dạy không được để trống")
    private String gioiThieuBanThan;

    private String linkVideoDayThu;

    private String tenNganHang;
    private String soTaiKhoanNganHang;
    private String tenChuTaiKhoan;

    // Thông tin bằng cấp / chứng chỉ / CV
    private LoaiChungChi loaiChungChi;
    private String tenChungChi;
    private String noiCap;
    private String diemSoHoacXepLoai;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayCap;

    private String linkAnhChungChi;
}

package com.studywithme.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatHoSoRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String hoTen;

    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    private String soDienThoai;

    @Size(max = 500, message = "Đường dẫn ảnh đại diện không hợp lệ")
    private String anhDaiDien;

    // Dành riêng cho Giảng viên / Gia sư
    @Size(max = 255, message = "Tiêu đề chuyên môn không được vượt quá 255 ký tự")
    private String tieuDeChuyenMon;

    private String gioiThieuBanThan;
}


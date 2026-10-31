package com.studywithme.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoiMatKhauRequest {

    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại")
    private String matKhauHienTai;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String matKhauMoi;

    @NotBlank(message = "Vui lòng xác nhận lại mật khẩu mới")
    private String xacNhanMatKhau;
}


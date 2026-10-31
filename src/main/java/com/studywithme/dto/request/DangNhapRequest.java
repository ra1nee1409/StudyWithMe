package com.studywithme.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class DangNhapRequest {

    @NotBlank(message = "Email không được để trống")
    @Size(min = 5, max = 50, message = "Email phải từ 5 đến 50 ký tự")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 1, max = 32, message = "Mật khẩu không được vượt quá 32 ký tự")
    private String matKhau;

    public DangNhapRequest() {
    }

    public DangNhapRequest(String email, String matKhau) {
        this.email = email;
        this.matKhau = matKhau;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}

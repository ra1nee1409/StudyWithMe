package com.studywithme.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class DangKyRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ và tên phải từ 2 đến 50 ký tự")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Size(min = 5, max = 50, message = "Email phải từ 5 đến 50 ký tự")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 32, message = "Mật khẩu phải từ 6 đến 32 ký tự")
    private String matKhau;

    @Pattern(regexp = "^$|^[0-9]{10,11}$", message = "Số điện thoại phải từ 10 đến 11 chữ số")
    @Size(max = 11, message = "Số điện thoại không được vượt quá 11 ký tự")
    private String soDienThoai;

    @Builder.Default
    private boolean dangKyLamGiangVien = false;

    public DangKyRequest() {
    }

    public DangKyRequest(String hoTen, String email, String matKhau, String soDienThoai, boolean dangKyLamGiangVien) {
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.dangKyLamGiangVien = dangKyLamGiangVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
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

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public boolean isDangKyLamGiangVien() {
        return dangKyLamGiangVien;
    }

    public void setDangKyLamGiangVien(boolean dangKyLamGiangVien) {
        this.dangKyLamGiangVien = dangKyLamGiangVien;
    }
}

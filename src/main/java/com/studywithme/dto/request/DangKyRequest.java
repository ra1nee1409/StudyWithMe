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
public class DangKyRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 4, message = "Mật khẩu phải có ít nhất 4 ký tự")
    private String matKhau;

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

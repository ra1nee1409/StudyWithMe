package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ApiResponse<T> {
    private boolean thanhCong;
    private String thongBao;
    private T duLieu;

    public ApiResponse() {
    }

    public ApiResponse(boolean thanhCong, String thongBao, T duLieu) {
        this.thanhCong = thanhCong;
        this.thongBao = thongBao;
        this.duLieu = duLieu;
    }

    public boolean isThanhCong() {
        return thanhCong;
    }

    public void setThanhCong(boolean thanhCong) {
        this.thanhCong = thanhCong;
    }

    public String getThongBao() {
        return thongBao;
    }

    public void setThongBao(String thongBao) {
        this.thongBao = thongBao;
    }

    public T getDuLieu() {
        return duLieu;
    }

    public void setDuLieu(T duLieu) {
        this.duLieu = duLieu;
    }

    public static <T> ApiResponse<T> success(String thongBao, T duLieu) {
        return ApiResponse.<T>builder()
                .thanhCong(true)
                .thongBao(thongBao)
                .duLieu(duLieu)
                .build();
    }

    public static <T> ApiResponse<T> success(T duLieu) {
        return success("Thao tác thành công", duLieu);
    }

    public static <T> ApiResponse<T> error(String thongBao) {
        return ApiResponse.<T>builder()
                .thanhCong(false)
                .thongBao(thongBao)
                .duLieu(null)
                .build();
    }

    public static <T> ApiResponse<T> error(String thongBao, T duLieu) {
        return ApiResponse.<T>builder()
                .thanhCong(false)
                .thongBao(thongBao)
                .duLieu(duLieu)
                .build();
    }

    // Compatibility helpers for JSON clients (status, message, data)
    public String getStatus() {
        return this.thanhCong ? "success" : "error";
    }

    public String getMessage() {
        return this.thongBao;
    }

    public T getData() {
        return this.duLieu;
    }
}

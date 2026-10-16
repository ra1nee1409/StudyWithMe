package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean thanhCong;
    private String thongBao;
    private T duLieu;

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
}

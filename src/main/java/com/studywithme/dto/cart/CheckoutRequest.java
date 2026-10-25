package com.studywithme.dto.cart;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotEmpty(message = "Danh sách sản phẩm thanh toán không được để trống")
    private List<Long> courseIds;

    private List<Long> classIds;

    @Builder.Default
    private String paymentMethod = "VNPAY";

    private String ghiChu;
}

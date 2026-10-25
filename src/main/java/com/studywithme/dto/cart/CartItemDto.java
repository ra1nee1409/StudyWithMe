package com.studywithme.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private String itemType; // "KHOA_HOC" hoặc "LOP_HOC"
    private String title;
    private String slug;
    private String image;
    private String instructorName;
    private BigDecimal price;
    private BigDecimal originalPrice;
}

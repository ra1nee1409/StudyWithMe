package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeRutTienResponse {

    @Builder.Default
    private BigDecimal tongTienChoXuLy = BigDecimal.ZERO;

    @Builder.Default
    private Long soLenhChoXuLy = 0L;

    @Builder.Default
    private BigDecimal tongTienDaChuyen = BigDecimal.ZERO;

    @Builder.Default
    private Long soLenhDaChuyen = 0L;

    @Builder.Default
    private Long soLenhTuChoi = 0L;

    @Builder.Default
    private Long tongSoLenh = 0L;
}

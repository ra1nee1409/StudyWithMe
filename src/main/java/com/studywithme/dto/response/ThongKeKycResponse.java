package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeKycResponse {
    private long choDuyet;
    private long daDuyet;
    private long tuChoi;
    private long tongCong;
}

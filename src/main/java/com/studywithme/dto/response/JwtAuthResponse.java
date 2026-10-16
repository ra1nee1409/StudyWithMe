package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    @Builder.Default
    private String tokenType = "Bearer";

    private String accessToken;
    private Long id;
    private String email;
    private String hoTen;
    private List<String> danhSachVaiTro;
}

package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class JwtAuthResponse {

    @Builder.Default
    private String tokenType = "Bearer";

    private String accessToken;
    private Long id;
    private String email;
    private String hoTen;
    private List<String> danhSachVaiTro;

    public JwtAuthResponse() {
    }

    public JwtAuthResponse(String tokenType, String accessToken, Long id, String email, String hoTen, List<String> danhSachVaiTro) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.id = id;
        this.email = email;
        this.hoTen = hoTen;
        this.danhSachVaiTro = danhSachVaiTro;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public List<String> getDanhSachVaiTro() {
        return danhSachVaiTro;
    }

    public void setDanhSachVaiTro(List<String> danhSachVaiTro) {
        this.danhSachVaiTro = danhSachVaiTro;
    }
}

package com.studywithme.service;

import com.studywithme.dto.request.DangKyRequest;
import com.studywithme.dto.request.DangNhapRequest;
import com.studywithme.dto.response.JwtAuthResponse;

public interface AuthService {
    JwtAuthResponse dangNhap(DangNhapRequest request);
    String dangKy(DangKyRequest request);
}

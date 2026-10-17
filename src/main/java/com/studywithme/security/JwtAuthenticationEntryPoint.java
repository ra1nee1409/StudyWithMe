package com.studywithme.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String acceptHeader = request.getHeader("Accept");
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/") || (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE) && !acceptHeader.contains(MediaType.TEXT_HTML_VALUE))) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"thanhCong\":false,\"thongBao\":\"Yêu cầu không được ủy quyền hoặc Token không hợp lệ.\",\"chiTiet\":\"" 
                    + authException.getMessage() + "\"}");
        } else {
            response.sendRedirect("/login");
        }
    }
}

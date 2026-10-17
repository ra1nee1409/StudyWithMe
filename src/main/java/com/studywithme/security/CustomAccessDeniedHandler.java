package com.studywithme.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String acceptHeader = request.getHeader("Accept");
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/") || (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE) && !acceptHeader.contains(MediaType.TEXT_HTML_VALUE))) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"thanhCong\":false,\"thongBao\":\"Bạn không có quyền thực hiện thao tác này (403 Forbidden).\",\"duLieu\":null}");
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("jakarta.servlet.error.status_code", HttpServletResponse.SC_FORBIDDEN);
            String message = "Bạn không có quyền truy cập vào trang này.";
            if (uri.startsWith("/admin")) {
                message = "Bạn không có quyền truy cập vào khu vực Quản trị hệ thống (Chỉ dành riêng cho tài khoản Quản Trị Viên).";
            } else if (uri.startsWith("/tutor")) {
                message = "Bạn không có quyền truy cập vào Bàn làm việc Giảng viên (Chỉ dành cho Gia sư / Giảng viên đã được phê duyệt hồ sơ).";
            }
            request.setAttribute("message", message);
            request.getRequestDispatcher("/error").forward(request, response);
        }
    }
}

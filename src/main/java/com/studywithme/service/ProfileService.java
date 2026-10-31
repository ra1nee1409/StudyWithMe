package com.studywithme.service;

import com.studywithme.dto.user.CapNhatHoSoRequest;
import com.studywithme.dto.user.DoiMatKhauRequest;
import com.studywithme.entity.NguoiDung;

public interface ProfileService {

    /**
     * Lấy thông tin tài khoản người dùng theo email đăng nhập hiện tại
     */
    NguoiDung layThongTinNguoiDung(String email);

    /**
     * Cập nhật thông tin cá nhân (Họ tên, SĐT, Ảnh đại diện, Giới thiệu).
     * Tuyệt đối KHÔNG thay đổi địa chỉ Email (Gmail đăng nhập).
     */
    NguoiDung capNhatThongTin(String email, CapNhatHoSoRequest request);

    /**
     * Đổi mật khẩu tài khoản người dùng
     */
    void doiMatKhau(String email, DoiMatKhauRequest request);
}


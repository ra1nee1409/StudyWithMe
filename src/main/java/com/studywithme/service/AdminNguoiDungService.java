package com.studywithme.service;

import com.studywithme.entity.NguoiDung;

import java.util.List;

public interface AdminNguoiDungService {

    /**
     * Lấy toàn bộ danh sách người dùng trong hệ thống
     */
    List<NguoiDung> layTatCaNguoiDung();

    /**
     * Đổi trạng thái hoạt động / tạm khóa của người dùng
     */
    void doiTrangThaiNguoiDung(Long id);

    /**
     * Xóa vĩnh viễn tài khoản người dùng và toàn bộ dữ liệu phụ thuộc khỏi CSDL.
     * @param id ID người dùng cần xóa
     * @param currentAdminEmail Email của Admin đang thực hiện thao tác
     */
    void xoaNguoiDungVinhVien(Long id, String currentAdminEmail);
}


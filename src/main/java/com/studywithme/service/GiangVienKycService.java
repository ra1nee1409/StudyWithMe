package com.studywithme.service;

import com.studywithme.dto.response.HoSoGiangVienKycResponse;
import com.studywithme.dto.response.ThongKeKycResponse;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;

import java.util.List;

public interface GiangVienKycService {

    /**
     * Lấy danh sách hồ sơ gia sư theo trạng thái (nếu null thì lấy tất cả)
     */
    List<HoSoGiangVienKycResponse> layDanhSachHoSo(TrangThaiGiangVien trangThai);

    /**
     * Lấy chi tiết hồ sơ gia sư kèm bằng cấp, chứng chỉ và video dạy thử
     */
    HoSoGiangVienKycResponse layChiTietHoSo(Long id);

    /**
     * Phê duyệt hồ sơ gia sư:
     * - Đổi trạng thái sang DA_DUYET
     * - Tự động cấp quyền ROLE_GIANG_VIEN cho tài khoản người dùng
     * - Đổi trạng thái các chứng chỉ sang DA_XAC_THUC
     */
    HoSoGiangVienKycResponse pheDuyetHoSo(Long id);

    /**
     * Từ chối hồ sơ gia sư kèm lưu lý do từ chối
     */
    HoSoGiangVienKycResponse tuChoiHoSo(Long id, String lyDo);

    /**
     * Lấy thống kê số lượng hồ sơ theo các trạng thái
     */
    ThongKeKycResponse layThongKeKyc();
}

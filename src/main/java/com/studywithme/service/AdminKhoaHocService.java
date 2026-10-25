package com.studywithme.service;

import com.studywithme.dto.response.KhoaHocKiemDuyetResponse;
import com.studywithme.dto.response.ThongKeKiemDuyetKhoaHocResponse;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;

import java.util.List;

public interface AdminKhoaHocService {

    /**
     * Lấy danh sách khóa học theo trạng thái (nếu null thì lấy tất cả)
     */
    List<KhoaHocKiemDuyetResponse> layDanhSachKhoaHoc(TrangThaiKhoaHoc trangThai);

    /**
     * Lấy thông tin chi tiết khóa học kèm đề cương chương/bài học để kiểm duyệt
     */
    KhoaHocKiemDuyetResponse layChiTietKhoaHoc(Long id);

    /**
     * Phê duyệt khóa học: Cập nhật trạng thái thành DA_XUAT_BAN
     */
    KhoaHocKiemDuyetResponse pheDuyetKhoaHoc(Long id);

    /**
     * Từ chối khóa học: Cập nhật trạng thái thành TU_CHOI và lưu lý do
     */
    KhoaHocKiemDuyetResponse tuChoiKhoaHoc(Long id, String lyDo);

    /**
     * Thống kê số lượng khóa học theo trạng thái
     */
    ThongKeKiemDuyetKhoaHocResponse layThongKe();
}

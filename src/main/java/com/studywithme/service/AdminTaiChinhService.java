package com.studywithme.service;

import com.studywithme.dto.response.DongTienGiaoDichDto;
import com.studywithme.dto.response.ThongKeDongTienResponse;
import com.studywithme.dto.response.ThongKeRutTienResponse;
import com.studywithme.dto.response.YeuCauRutTienResponse;
import com.studywithme.entity.DonHang;
import com.studywithme.entity.LichSuViTien;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;

import java.util.List;

public interface AdminTaiChinhService {

    List<YeuCauRutTienResponse> layDanhSachYeuCauRutTien(TrangThaiRutTien trangThai);

    ThongKeRutTienResponse layThongKeRutTien();

    YeuCauRutTienResponse layChiTietYeuCau(Long id);

    YeuCauRutTienResponse xacNhanChuyenTien(Long id, String ghiChuAdmin);

    YeuCauRutTienResponse tuChoiRutTien(Long id, String lyDoTuChoi);

    /**
     * Thống kê dòng tiền tổng thể toàn hệ thống (Tiền vào, Tiền ra, Phí sàn 20%, Số dư ví giảng viên)
     */
    ThongKeDongTienResponse layThongKeDongTien();

    /**
     * Lấy danh sách biến động dòng tiền (hợp nhất đơn hàng mua khóa học/gia sư và lệnh rút tiền)
     */
    List<DongTienGiaoDichDto> layDanhSachDongTien(String loaiFilter, String phanLoaiFilter, String keyword);

    /**
     * Lấy tất cả đơn hàng hệ thống
     */
    List<DonHang> layTatCaDonHang();

    /**
     * Lấy toàn bộ biến động ví của các giảng viên
     */
    List<LichSuViTien> layTatCaLichSuVi();
}

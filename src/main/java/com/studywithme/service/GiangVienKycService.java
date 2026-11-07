package com.studywithme.service;

import com.studywithme.dto.request.DangKyGiangVienRequest;
import com.studywithme.dto.response.HoSoGiangVienKycResponse;
import com.studywithme.dto.response.ThongKeKycResponse;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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

    /**
     * Người dùng nộp hồ sơ đăng ký làm giảng viên (kèm CV / chứng chỉ)
     */
    HoSoGiangVien nopHoSoGiangVien(String userEmail, DangKyGiangVienRequest request, MultipartFile fileChungChi);

    /**
     * Lấy thông tin hồ sơ giảng viên của một người dùng theo email
     */
    Optional<HoSoGiangVien> layHoSoTheoUserEmail(String userEmail);

    /**
     * Đình chỉ / Thu hồi quyền giảng dạy của gia sư:
     * - Thu hồi vai trò ROLE_GIANG_VIEN khỏi tài khoản người dùng
     * - Chuyển trạng thái hồ sơ sang TU_CHOI kèm lý do đình chỉ
     */
    HoSoGiangVienKycResponse dinhChiQuyenGiangVien(Long id, String lyDoDinhChi);
}

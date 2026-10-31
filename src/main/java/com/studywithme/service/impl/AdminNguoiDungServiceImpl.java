package com.studywithme.service.impl;

import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.AdminNguoiDungService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNguoiDungServiceImpl implements AdminNguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<NguoiDung> layTatCaNguoiDung() {
        return nguoiDungRepository.findAllByOrderByNgayTaoDesc();
    }

    @Override
    @Transactional
    public void doiTrangThaiNguoiDung(Long id) {
        NguoiDung user = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng với mã ID: " + id));

        if ("admin@studywithme.vn".equalsIgnoreCase(user.getEmail())) {
            throw new IllegalStateException("Không thể khóa tài khoản Quản trị viên mặc định của hệ thống.");
        }

        if (user.getTrangThai() == TrangThaiNguoiDung.HOAT_DONG) {
            user.setTrangThai(TrangThaiNguoiDung.TAM_KHOA);
        } else {
            user.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);
        }
        nguoiDungRepository.save(user);
        log.info("Admin đã đổi trạng thái tài khoản ID #{} ({}) sang: {}", id, user.getEmail(), user.getTrangThai());
    }

    @Override
    @Transactional
    public void xoaNguoiDungVinhVien(Long id, String currentAdminEmail) {
        NguoiDung user = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng với mã ID: " + id));

        if ("admin@studywithme.vn".equalsIgnoreCase(user.getEmail())) {
            throw new IllegalStateException("Tuyệt đối không thể xóa tài khoản Quản trị viên gốc của hệ thống.");
        }

        if (currentAdminEmail != null && currentAdminEmail.equalsIgnoreCase(user.getEmail())) {
            throw new IllegalStateException("Bạn không thể tự xóa tài khoản của chính mình khi đang đăng nhập.");
        }

        log.info("Bắt đầu quy trình xóa vĩnh viễn tài khoản ID #{}, Email: {} khỏi CSDL...", id, user.getEmail());

        // 1. Xóa dữ liệu học viên & liên kết mua sắm / tiến độ
        jdbcTemplate.update("DELETE FROM chi_tiet_don_hang WHERE don_hang_id IN (SELECT id FROM don_hang WHERE nguoi_dung_id = ?)", id);
        jdbcTemplate.update("DELETE FROM don_hang WHERE nguoi_dung_id = ?", id);
        jdbcTemplate.update("DELETE FROM tien_do_bai_hoc WHERE nguoi_dung_id = ?", id);
        jdbcTemplate.update("DELETE FROM ghi_danh_khoa_hoc WHERE nguoi_dung_id = ?", id);
        jdbcTemplate.update("DELETE FROM dang_ky_lop_hoc WHERE hoc_vien_id = ?", id);
        jdbcTemplate.update("DELETE FROM danh_sach_yeu_thich WHERE nguoi_dung_id = ?", id);
        jdbcTemplate.update("DELETE FROM danh_gia_nhan_xet WHERE nguoi_dung_id = ?", id);
        jdbcTemplate.update("DELETE FROM nguoi_dung_vai_tro WHERE nguoi_dung_id = ?", id);

        // 2. Nếu người dùng là Giảng viên / Gia sư, dọn sạch hồ sơ và các bài giảng liên quan
        List<Long> gvIds = jdbcTemplate.queryForList("SELECT id FROM ho_so_giang_vien WHERE nguoi_dung_id = ?", Long.class, id);
        for (Long gvId : gvIds) {
            jdbcTemplate.update("DELETE FROM chung_chi_giang_vien WHERE giang_vien_id = ?", gvId);
            jdbcTemplate.update("DELETE FROM lich_su_vi_tien WHERE giang_vien_id = ?", gvId);
            jdbcTemplate.update("DELETE FROM yeu_cau_rut_tien WHERE giang_vien_id = ?", gvId);
            jdbcTemplate.update("DELETE FROM dang_ky_lop_hoc WHERE lop_hoc_id IN (SELECT id FROM lop_hoc_truc_tuyen WHERE giang_vien_id = ?)", gvId);
            jdbcTemplate.update("DELETE FROM lop_hoc_truc_tuyen WHERE giang_vien_id = ?", gvId);

            // Dọn sạch các khóa học của giảng viên
            List<Long> courseIds = jdbcTemplate.queryForList("SELECT id FROM khoa_hoc WHERE giang_vien_id = ?", Long.class, gvId);
            for (Long cId : courseIds) {
                jdbcTemplate.update("DELETE FROM tien_do_bai_hoc WHERE bai_hoc_id IN (SELECT id FROM bai_hoc WHERE chuong_hoc_id IN (SELECT id FROM chuong_hoc WHERE khoa_hoc_id = ?))", cId);
                jdbcTemplate.update("DELETE FROM bai_hoc WHERE chuong_hoc_id IN (SELECT id FROM chuong_hoc WHERE khoa_hoc_id = ?)", cId);
                jdbcTemplate.update("DELETE FROM chuong_hoc WHERE khoa_hoc_id = ?", cId);
                jdbcTemplate.update("DELETE FROM ghi_danh_khoa_hoc WHERE khoa_hoc_id = ?", cId);
                jdbcTemplate.update("DELETE FROM chi_tiet_don_hang WHERE san_pham_id = ?", cId);
                jdbcTemplate.update("DELETE FROM danh_sach_yeu_thich WHERE khoa_hoc_id = ?", cId);
                jdbcTemplate.update("DELETE FROM danh_gia_nhan_xet WHERE khoa_hoc_id = ?", cId);
                jdbcTemplate.update("DELETE FROM khoa_hoc WHERE id = ?", cId);
            }
            jdbcTemplate.update("DELETE FROM ho_so_giang_vien WHERE id = ?", gvId);
        }

        // 3. Xóa vĩnh viễn trong bảng nguoi_dung
        jdbcTemplate.update("DELETE FROM nguoi_dung WHERE id = ?", id);
        log.info("Đã xóa vĩnh viễn tài khoản ID #{} ({}) khỏi cơ sở dữ liệu thành công.", id, user.getEmail());
    }
}


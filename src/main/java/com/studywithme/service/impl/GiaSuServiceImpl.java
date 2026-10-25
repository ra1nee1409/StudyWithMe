package com.studywithme.service.impl;

import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.LopHocTrucTuyen;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.giaovien.LoaiChungChi;
import com.studywithme.entity.enums.giaovien.TrangThaiLopHoc;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.repository.LopHocTrucTuyenRepository;
import com.studywithme.service.GiaSuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý logic tìm kiếm, xem hồ sơ và lớp dạy kèm của gia sư.
 */
@Service
@Transactional(readOnly = true)
public class GiaSuServiceImpl implements GiaSuService {

    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final LopHocTrucTuyenRepository lopHocTrucTuyenRepository;
    private final KhoaHocRepository khoaHocRepository;

    public GiaSuServiceImpl(HoSoGiangVienRepository hoSoGiangVienRepository,
                            LopHocTrucTuyenRepository lopHocTrucTuyenRepository,
                            KhoaHocRepository khoaHocRepository) {
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.lopHocTrucTuyenRepository = lopHocTrucTuyenRepository;
        this.khoaHocRepository = khoaHocRepository;
    }

    @Override
    public Page<HoSoGiangVien> locGiaSu(String keyword, String loaiChungChiStr, Pageable pageable) {
        LoaiChungChi loaiChungChi = null;
        if (loaiChungChiStr != null && !loaiChungChiStr.isBlank()) {
            try {
                loaiChungChi = LoaiChungChi.valueOf(loaiChungChiStr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return hoSoGiangVienRepository.locGiaSu(TrangThaiGiangVien.DA_DUYET, cleanKeyword, loaiChungChi, pageable);
    }

    @Override
    public HoSoGiangVien layChiTietGiaSu(Long id) {
        return hoSoGiangVienRepository.findById(id)
                .filter(h -> h.getTrangThaiDuyet() == TrangThaiGiangVien.DA_DUYET)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gia sư hợp lệ với ID: " + id));
    }

    @Override
    public List<LopHocTrucTuyen> layLopHocCuaGiaSu(Long giangVienId) {
        return lopHocTrucTuyenRepository.findByGiangVienIdAndTrangThaiOrderByNgayTaoDesc(
                giangVienId, TrangThaiLopHoc.DANG_MO_DANG_KY);
    }

    @Override
    public List<KhoaHoc> layKhoaHocCuaGiaSu(Long giangVienId) {
        return khoaHocRepository.findByGiangVienIdAndTrangThaiOrderByNgayTaoDesc(
                giangVienId, TrangThaiKhoaHoc.DA_XUAT_BAN);
    }

    @Override
    public List<HoSoGiangVien> layGiaSuTuongTu(Long currentId) {
        return hoSoGiangVienRepository.findTop4ByTrangThaiDuyetAndIdNotOrderByNgayTaoDesc(
                TrangThaiGiangVien.DA_DUYET, currentId);
    }

    @Override
    public List<HoSoGiangVien> layTopGiaSuTieuBieu() {
        return hoSoGiangVienRepository.findTop4ByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien.DA_DUYET);
    }
}

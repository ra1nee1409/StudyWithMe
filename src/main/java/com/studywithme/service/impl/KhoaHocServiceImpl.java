package com.studywithme.service.impl;

import com.studywithme.dto.response.TopKhoaHocDto;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.service.KhoaHocService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service xử lý logic tìm kiếm, xem chi tiết và gợi ý khóa học cho học viên.
 */
@Service
@Transactional(readOnly = true)
public class KhoaHocServiceImpl implements KhoaHocService {

    private final KhoaHocRepository khoaHocRepository;

    public KhoaHocServiceImpl(KhoaHocRepository khoaHocRepository) {
        this.khoaHocRepository = khoaHocRepository;
    }

    @Override
    public Page<KhoaHoc> locKhoaHoc(String keyword, 
                                     Long danhMucId, 
                                     String thePhanLoai, 
                                     BigDecimal giaMin, 
                                     BigDecimal giaMax, 
                                     BigDecimal soSaoMin, 
                                     Pageable pageable) {
        String cleanKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        String cleanThePhanLoai = StringUtils.hasText(thePhanLoai) ? thePhanLoai.trim() : null;

        return khoaHocRepository.locKhoaHoc(
                cleanKeyword, 
                danhMucId, 
                cleanThePhanLoai, 
                giaMin, 
                giaMax, 
                soSaoMin, 
                pageable
        );
    }

    @Override
    public KhoaHoc layChiTietKhoaHocTheoSlug(String slug) {
        return khoaHocRepository.findByDuongDanSlugAndTrangThai(slug, TrangThaiKhoaHoc.DA_XUAT_BAN)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với đường dẫn: " + slug));
    }

    @Override
    public List<KhoaHoc> layKhoaHocLienQuan(Long danhMucId, Long khoaHocId) {
        if (danhMucId == null) {
            return List.of();
        }
        return khoaHocRepository.findTop4ByDanhMucIdAndIdNotAndTrangThaiOrderByTongSoHocVienDesc(
                danhMucId, 
                khoaHocId, 
                TrangThaiKhoaHoc.DA_XUAT_BAN
        );
    }

    @Override
    public List<TopKhoaHocDto> layTop8KhoaHocNoiBat() {
        List<KhoaHoc> courses = khoaHocRepository.findTop8ByTrangThaiOrderByTongSoHocVienDesc(TrangThaiKhoaHoc.DA_XUAT_BAN);
        
        return courses.stream().map(k -> TopKhoaHocDto.builder()
                .id(k.getId())
                .tieuDe(k.getTieuDe())
                .duongDanSlug(k.getDuongDanSlug())
                .anhDaiDien(k.getAnhDaiDienKhoaHoc())
                .tenGiangVien(k.getGiangVien() != null && k.getGiangVien().getNguoiDung() != null 
                        ? k.getGiangVien().getNguoiDung().getHoTen() 
                        : "Gia sư StudyWithMe")
                .giaGoc(k.getGiaGoc())
                .giaHienTai(k.getGiaKhuyenMai() != null ? k.getGiaKhuyenMai() : k.getGiaGoc())
                .soHocVien(k.getTongSoHocVien())
                .build()
        ).toList();
    }
}

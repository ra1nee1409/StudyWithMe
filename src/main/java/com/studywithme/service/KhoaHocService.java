package com.studywithme.service;

import com.studywithme.dto.response.TopKhoaHocDto;
import com.studywithme.entity.KhoaHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface KhoaHocService {

    /**
     * Lọc và tìm kiếm khóa học theo nhiều tiêu chí (Cấp học, Giá, Sao, Từ khóa) có phân trang
     */
    Page<KhoaHoc> locKhoaHoc(String keyword, 
                             Long danhMucId, 
                             String thePhanLoai, 
                             BigDecimal giaMin, 
                             BigDecimal giaMax, 
                             BigDecimal soSaoMin, 
                             Pageable pageable);

    /**
     * Lấy thông tin chi tiết khóa học theo Slug
     */
    KhoaHoc layChiTietKhoaHocTheoSlug(String slug);

    /**
     * Lấy danh sách 4 khóa học liên quan cùng danh mục
     */
    List<KhoaHoc> layKhoaHocLienQuan(Long danhMucId, Long khoaHocId);

    /**
     * Lấy top 8 khóa học nhiều học viên đăng ký nhất cho trang chủ / public API
     */
    List<TopKhoaHocDto> layTop8KhoaHocNoiBat();
}

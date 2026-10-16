package com.studywithme.repository;

import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.TrangThaiKhoaHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhoaHocRepository extends JpaRepository<KhoaHoc, Long> {
    Optional<KhoaHoc> findByDuongDanSlug(String duongDanSlug);
    boolean existsByDuongDanSlug(String duongDanSlug);

    List<KhoaHoc> findByGiangVienId(Long giangVienId);
    Page<KhoaHoc> findByTrangThai(TrangThaiKhoaHoc trangThai, Pageable pageable);

    @Query("SELECT k FROM KhoaHoc k WHERE k.trangThai = 'DA_XUAT_BAN' " +
           "AND (:keyword IS NULL OR LOWER(k.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:danhMucId IS NULL OR k.danhMuc.id = :danhMucId)")
    Page<KhoaHoc> timKiemKhoaHoc(@Param("keyword") String keyword, 
                                 @Param("danhMucId") Long danhMucId, 
                                 Pageable pageable);

    List<KhoaHoc> findTop8ByTrangThaiOrderByTongSoHocVienDesc(TrangThaiKhoaHoc trangThai);
}

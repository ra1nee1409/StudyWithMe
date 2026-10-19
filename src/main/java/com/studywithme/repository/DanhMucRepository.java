package com.studywithme.repository;

import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.enums.shared.LoaiDanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {
    Optional<DanhMuc> findByDuongDanSlug(String duongDanSlug);
    boolean existsByDuongDanSlug(String duongDanSlug);
    boolean existsByDuongDanSlugAndIdNot(String duongDanSlug, Long id);
    boolean existsByDanhMucChaId(Long danhMucChaId);
    List<DanhMuc> findAllByOrderByThuTuHienThiAsc();
    List<DanhMuc> findByDanhMucChaIsNullOrderByThuTuHienThiAsc();
    List<DanhMuc> findByDanhMucChaIdOrderByThuTuHienThiAsc(Long danhMucChaId);
    List<DanhMuc> findByLoaiDanhMucOrderByThuTuHienThiAsc(LoaiDanhMuc loaiDanhMuc);
    long countByDanhMucChaIsNull();
    long countByDanhMucChaIsNotNull();
    long countByKichHoatTrue();
}

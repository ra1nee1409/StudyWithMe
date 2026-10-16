package com.studywithme.repository;

import com.studywithme.entity.YeuCauRutTien;
import com.studywithme.entity.enums.TrangThaiRutTien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YeuCauRutTienRepository extends JpaRepository<YeuCauRutTien, Long> {
    List<YeuCauRutTien> findByGiangVienIdOrderByNgayTaoDesc(Long giangVienId);
    Page<YeuCauRutTien> findByTrangThai(TrangThaiRutTien trangThai, Pageable pageable);
}

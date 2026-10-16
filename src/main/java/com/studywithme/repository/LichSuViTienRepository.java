package com.studywithme.repository;

import com.studywithme.entity.LichSuViTien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuViTienRepository extends JpaRepository<LichSuViTien, Long> {
    List<LichSuViTien> findByGiangVienIdOrderByNgayTaoDesc(Long giangVienId);
    Page<LichSuViTien> findByGiangVienId(Long giangVienId, Pageable pageable);
}

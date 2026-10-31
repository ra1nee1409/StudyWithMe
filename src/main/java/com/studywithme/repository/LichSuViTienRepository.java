package com.studywithme.repository;

import com.studywithme.entity.LichSuViTien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichSuViTienRepository extends JpaRepository<LichSuViTien, Long> {

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    List<LichSuViTien> findByGiangVienIdOrderByNgayTaoDesc(Long giangVienId);

    Page<LichSuViTien> findByGiangVienId(Long giangVienId, Pageable pageable);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    List<LichSuViTien> findAllByOrderByNgayTaoDesc();
}

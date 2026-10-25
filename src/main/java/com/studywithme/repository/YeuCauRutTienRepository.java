package com.studywithme.repository;

import com.studywithme.entity.YeuCauRutTien;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YeuCauRutTienRepository extends JpaRepository<YeuCauRutTien, Long> {
    
    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    List<YeuCauRutTien> findByGiangVienIdOrderByNgayTaoDesc(Long giangVienId);
    
    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    List<YeuCauRutTien> findAllByOrderByNgayTaoDesc();

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    List<YeuCauRutTien> findByTrangThaiOrderByNgayTaoDesc(TrangThaiRutTien trangThai);

    @Override
    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    Optional<YeuCauRutTien> findById(Long id);

    Page<YeuCauRutTien> findByTrangThai(TrangThaiRutTien trangThai, Pageable pageable);
    
    long countByTrangThai(TrangThaiRutTien trangThai);
}

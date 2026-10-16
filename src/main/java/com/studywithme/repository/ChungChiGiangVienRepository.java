package com.studywithme.repository;

import com.studywithme.entity.ChungChiGiangVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChungChiGiangVienRepository extends JpaRepository<ChungChiGiangVien, Long> {
    List<ChungChiGiangVien> findByGiangVienId(Long giangVienId);
}

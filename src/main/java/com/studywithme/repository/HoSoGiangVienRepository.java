package com.studywithme.repository;

import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.enums.TrangThaiGiangVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoSoGiangVienRepository extends JpaRepository<HoSoGiangVien, Long> {
    Optional<HoSoGiangVien> findByNguoiDungId(Long nguoiDungId);
    Page<HoSoGiangVien> findByTrangThaiDuyet(TrangThaiGiangVien trangThaiDuyet, Pageable pageable);
    long countByTrangThaiDuyet(TrangThaiGiangVien trangThaiDuyet);
    List<HoSoGiangVien> findByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien trangThaiDuyet);
    List<HoSoGiangVien> findTop4ByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien trangThaiDuyet);
}

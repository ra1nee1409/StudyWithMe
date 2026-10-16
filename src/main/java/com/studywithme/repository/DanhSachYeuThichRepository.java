package com.studywithme.repository;

import com.studywithme.entity.DanhSachYeuThich;
import com.studywithme.entity.enums.LoaiYeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhSachYeuThichRepository extends JpaRepository<DanhSachYeuThich, Long> {
    List<DanhSachYeuThich> findByNguoiDungId(Long nguoiDungId);
    List<DanhSachYeuThich> findByNguoiDungIdAndLoaiYeuThich(Long nguoiDungId, LoaiYeuThich loaiYeuThich);
    Optional<DanhSachYeuThich> findByNguoiDungIdAndLoaiYeuThichAndMucTieuId(Long nguoiDungId, LoaiYeuThich loaiYeuThich, Long mucTieuId);
    boolean existsByNguoiDungIdAndLoaiYeuThichAndMucTieuId(Long nguoiDungId, LoaiYeuThich loaiYeuThich, Long mucTieuId);
    void deleteByNguoiDungIdAndLoaiYeuThichAndMucTieuId(Long nguoiDungId, LoaiYeuThich loaiYeuThich, Long mucTieuId);
}

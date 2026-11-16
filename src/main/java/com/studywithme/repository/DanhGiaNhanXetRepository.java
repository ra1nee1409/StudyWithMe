package com.studywithme.repository;

import com.studywithme.entity.DanhGiaNhanXet;
import com.studywithme.entity.enums.hocvien.LoaiYeuThich;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaNhanXetRepository extends JpaRepository<DanhGiaNhanXet, Long> {
    @EntityGraph(attributePaths = {"nguoiDung"})
    Page<DanhGiaNhanXet> findByLoaiDanhGiaAndMucTieuIdAndDuocHienThiTrue(LoaiYeuThich loaiDanhGia, Long mucTieuId, Pageable pageable);

    @EntityGraph(attributePaths = {"nguoiDung"})
    List<DanhGiaNhanXet> findTop5ByDuocHienThiTrueOrderByNgayDanhGiaDesc();
}

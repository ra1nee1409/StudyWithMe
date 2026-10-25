package com.studywithme.repository;

import com.studywithme.entity.GhiDanhKhoaHoc;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GhiDanhKhoaHocRepository extends JpaRepository<GhiDanhKhoaHoc, Long> {

    boolean existsByNguoiDungIdAndKhoaHocId(Long nguoiDungId, Long khoaHocId);

    @EntityGraph(attributePaths = {"khoaHoc", "khoaHoc.giangVien", "khoaHoc.giangVien.nguoiDung"})
    Optional<GhiDanhKhoaHoc> findByNguoiDungIdAndKhoaHocId(Long nguoiDungId, Long khoaHocId);

    @EntityGraph(attributePaths = {"khoaHoc", "khoaHoc.giangVien", "khoaHoc.giangVien.nguoiDung", "khoaHoc.danhMuc"})
    List<GhiDanhKhoaHoc> findByNguoiDungIdOrderByNgayGhiDanhDesc(Long nguoiDungId);

    long countByNguoiDungId(Long nguoiDungId);
}

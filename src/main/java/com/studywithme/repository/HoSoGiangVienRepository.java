package com.studywithme.repository;

import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.giaovien.LoaiChungChi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoSoGiangVienRepository extends JpaRepository<HoSoGiangVien, Long> {

    Optional<HoSoGiangVien> findByNguoiDungId(Long nguoiDungId);

    Optional<HoSoGiangVien> findByNguoiDungEmail(String email);

    Page<HoSoGiangVien> findByTrangThaiDuyet(TrangThaiGiangVien trangThaiDuyet, Pageable pageable);

    long countByTrangThaiDuyet(TrangThaiGiangVien trangThaiDuyet);

    @EntityGraph(attributePaths = {"nguoiDung"})
    List<HoSoGiangVien> findByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien trangThaiDuyet);

    @EntityGraph(attributePaths = {"nguoiDung"})
    List<HoSoGiangVien> findTop4ByTrangThaiDuyetOrderByNgayTaoDesc(TrangThaiGiangVien trangThaiDuyet);

    @EntityGraph(attributePaths = {"nguoiDung"})
    List<HoSoGiangVien> findTop5ByOrderByNgayTaoDesc();

    @EntityGraph(attributePaths = {"nguoiDung"})
    List<HoSoGiangVien> findAllByOrderByNgayTaoDesc();

    @EntityGraph(attributePaths = {"nguoiDung", "danhSachChungChi"})
    Optional<HoSoGiangVien> findById(Long id);

    @EntityGraph(attributePaths = {"nguoiDung", "danhSachChungChi"})
    @Query("SELECT DISTINCT h FROM HoSoGiangVien h LEFT JOIN h.danhSachChungChi c " +
           "WHERE h.trangThaiDuyet = :trangThai " +
           "AND (:keyword IS NULL OR LOWER(h.nguoiDung.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(h.tieuDeChuyenMon) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(h.gioiThieuBanThan) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:loaiChungChi IS NULL OR c.loaiChungChi = :loaiChungChi)")
    Page<HoSoGiangVien> locGiaSu(@Param("trangThai") TrangThaiGiangVien trangThai,
                                 @Param("keyword") String keyword,
                                 @Param("loaiChungChi") LoaiChungChi loaiChungChi,
                                 Pageable pageable);

    @EntityGraph(attributePaths = {"nguoiDung", "danhSachChungChi"})
    List<HoSoGiangVien> findTop4ByTrangThaiDuyetAndIdNotOrderByNgayTaoDesc(TrangThaiGiangVien trangThaiDuyet, Long id);
}

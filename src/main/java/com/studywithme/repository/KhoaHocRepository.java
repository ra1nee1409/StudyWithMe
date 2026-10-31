package com.studywithme.repository;

import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhoaHocRepository extends JpaRepository<KhoaHoc, Long> {
    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    Optional<KhoaHoc> findByDuongDanSlug(String duongDanSlug);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc", "danhSachChuongHoc"})
    Optional<KhoaHoc> findByDuongDanSlugAndTrangThai(String duongDanSlug, TrangThaiKhoaHoc trangThai);

    boolean existsByDuongDanSlug(String duongDanSlug);

    List<KhoaHoc> findByGiangVienId(Long giangVienId);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc", "danhSachChuongHoc"})
    List<KhoaHoc> findByGiangVienIdOrderByNgayTaoDesc(Long giangVienId);

    long countByGiangVienId(Long giangVienId);

    long countByGiangVienIdAndTrangThai(Long giangVienId, TrangThaiKhoaHoc trangThai);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    List<KhoaHoc> findByGiangVienIdAndTrangThaiOrderByNgayTaoDesc(Long giangVienId, TrangThaiKhoaHoc trangThai);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    List<KhoaHoc> findByTrangThaiOrderByNgayTaoDesc(TrangThaiKhoaHoc trangThai);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    List<KhoaHoc> findAllByOrderByNgayTaoDesc();

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc", "danhSachChuongHoc"})
    @Query("SELECT k FROM KhoaHoc k WHERE k.id = :id")
    Optional<KhoaHoc> findChiTietById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    Page<KhoaHoc> findByTrangThai(TrangThaiKhoaHoc trangThai, Pageable pageable);

    @Query("SELECT k FROM KhoaHoc k WHERE k.trangThai = 'DA_XUAT_BAN' " +
           "AND (:keyword IS NULL OR LOWER(k.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:danhMucId IS NULL OR k.danhMuc.id = :danhMucId)")
    Page<KhoaHoc> timKiemKhoaHoc(@Param("keyword") String keyword, 
                                 @Param("danhMucId") Long danhMucId, 
                                 Pageable pageable);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    @Query("SELECT k FROM KhoaHoc k WHERE k.trangThai = 'DA_XUAT_BAN' " +
           "AND (:keyword IS NULL OR LOWER(k.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(k.moTaChiTiet) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:danhMucId IS NULL OR k.danhMuc.id = :danhMucId OR k.danhMuc.danhMucCha.id = :danhMucId) " +
           "AND (:thePhanLoai IS NULL OR k.thePhanLoai = :thePhanLoai) " +
           "AND (:giaMin IS NULL OR COALESCE(k.giaKhuyenMai, k.giaGoc) >= :giaMin) " +
           "AND (:giaMax IS NULL OR COALESCE(k.giaKhuyenMai, k.giaGoc) <= :giaMax) " +
           "AND (:soSaoMin IS NULL OR k.diemDanhGiaTrungBinh >= :soSaoMin)")
    Page<KhoaHoc> locKhoaHoc(@Param("keyword") String keyword,
                             @Param("danhMucId") Long danhMucId,
                             @Param("thePhanLoai") String thePhanLoai,
                             @Param("giaMin") BigDecimal giaMin,
                             @Param("giaMax") BigDecimal giaMax,
                             @Param("soSaoMin") BigDecimal soSaoMin,
                             Pageable pageable);

    long countByTrangThai(TrangThaiKhoaHoc trangThai);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    List<KhoaHoc> findTop8ByTrangThaiOrderByTongSoHocVienDesc(TrangThaiKhoaHoc trangThai);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    List<KhoaHoc> findTop5ByOrderByTongSoHocVienDesc();

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung", "danhMuc"})
    List<KhoaHoc> findTop4ByDanhMucIdAndIdNotAndTrangThaiOrderByTongSoHocVienDesc(Long danhMucId, Long id, TrangThaiKhoaHoc trangThai);

    boolean existsByDanhMucId(Long danhMucId);
    long countByDanhMucId(Long danhMucId);
}

package com.studywithme.repository;

import com.studywithme.entity.LopHocTrucTuyen;
import com.studywithme.entity.enums.giaovien.TrangThaiLopHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LopHocTrucTuyenRepository extends JpaRepository<LopHocTrucTuyen, Long> {

    @Override
    @EntityGraph(attributePaths = {"danhMuc", "giangVien", "giangVien.nguoiDung"})
    List<LopHocTrucTuyen> findAll();

    @EntityGraph(attributePaths = {"danhMuc"})
    List<LopHocTrucTuyen> findByGiangVienId(Long giangVienId);

    @EntityGraph(attributePaths = {"danhMuc", "danhSachHocVien"})
    List<LopHocTrucTuyen> findByGiangVienIdOrderByNgayTaoDesc(Long giangVienId);

    long countByGiangVienId(Long giangVienId);

    @EntityGraph(attributePaths = {"danhMuc", "giangVien", "giangVien.nguoiDung"})
    Page<LopHocTrucTuyen> findByTrangThai(TrangThaiLopHoc trangThai, Pageable pageable);

    boolean existsByDanhMucId(Long danhMucId);

    long countByDanhMucId(Long danhMucId);

    @EntityGraph(attributePaths = {"danhMuc", "giangVien", "giangVien.nguoiDung"})
    List<LopHocTrucTuyen> findByGiangVienIdAndTrangThaiOrderByNgayTaoDesc(Long giangVienId, TrangThaiLopHoc trangThai);
}

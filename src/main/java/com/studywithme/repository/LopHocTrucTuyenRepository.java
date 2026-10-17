package com.studywithme.repository;

import com.studywithme.entity.LopHocTrucTuyen;
import com.studywithme.entity.enums.TrangThaiLopHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LopHocTrucTuyenRepository extends JpaRepository<LopHocTrucTuyen, Long> {
    List<LopHocTrucTuyen> findByGiangVienId(Long giangVienId);
    List<LopHocTrucTuyen> findByGiangVienIdOrderByNgayTaoDesc(Long giangVienId);
    long countByGiangVienId(Long giangVienId);
    Page<LopHocTrucTuyen> findByTrangThai(TrangThaiLopHoc trangThai, Pageable pageable);
}

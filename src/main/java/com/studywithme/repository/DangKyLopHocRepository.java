package com.studywithme.repository;

import com.studywithme.entity.DangKyLopHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DangKyLopHocRepository extends JpaRepository<DangKyLopHoc, Long> {
    List<DangKyLopHoc> findByHocVienId(Long hocVienId);
    List<DangKyLopHoc> findByLopHocId(Long lopHocId);
    long countByLopHocId(Long lopHocId);
    long countByHocVienId(Long hocVienId);
    List<DangKyLopHoc> findByLopHocGiangVienId(Long giangVienId);
    long countByLopHocGiangVienId(Long giangVienId);
}

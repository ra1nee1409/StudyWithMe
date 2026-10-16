package com.studywithme.repository;

import com.studywithme.entity.ChuongHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChuongHocRepository extends JpaRepository<ChuongHoc, Long> {
    List<ChuongHoc> findByKhoaHocIdOrderByThuTuSapXepAsc(Long khoaHocId);
}

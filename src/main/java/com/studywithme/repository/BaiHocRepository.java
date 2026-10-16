package com.studywithme.repository;

import com.studywithme.entity.BaiHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaiHocRepository extends JpaRepository<BaiHoc, Long> {
    List<BaiHoc> findByChuongHocIdOrderByThuTuSapXepAsc(Long chuongHocId);
    List<BaiHoc> findByChuongHocKhoaHocIdAndChoPhepHocThuTrue(Long khoaHocId);
}

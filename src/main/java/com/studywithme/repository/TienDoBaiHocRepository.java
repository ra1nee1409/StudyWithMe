package com.studywithme.repository;

import com.studywithme.entity.TienDoBaiHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TienDoBaiHocRepository extends JpaRepository<TienDoBaiHoc, Long> {
    Optional<TienDoBaiHoc> findByNguoiDungIdAndBaiHocId(Long nguoiDungId, Long baiHocId);
    List<TienDoBaiHoc> findByNguoiDungIdAndBaiHocChuongHocKhoaHocId(Long nguoiDungId, Long khoaHocId);
}

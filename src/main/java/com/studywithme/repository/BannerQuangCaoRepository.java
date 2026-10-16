package com.studywithme.repository;

import com.studywithme.entity.BannerQuangCao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerQuangCaoRepository extends JpaRepository<BannerQuangCao, Long> {
    List<BannerQuangCao> findByKichHoatTrueOrderByThuTuHienThiAsc();
    List<BannerQuangCao> findAllByOrderByThuTuHienThiAsc();
}

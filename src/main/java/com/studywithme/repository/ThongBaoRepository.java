package com.studywithme.repository;

import com.studywithme.entity.ThongBao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {

    List<ThongBao> findByNguoiDungIdOrderByThoiGianTaoDesc(Long nguoiDungId, Pageable pageable);

    long countByNguoiDungIdAndDaDocFalse(Long nguoiDungId);

    Optional<ThongBao> findByIdAndNguoiDungId(Long id, Long nguoiDungId);

    @Modifying
    @Query("UPDATE ThongBao t SET t.daDoc = true WHERE t.nguoiDung.id = :userId AND t.daDoc = false")
    int danhDauTatCaDaDoc(@Param("userId") Long userId);
}

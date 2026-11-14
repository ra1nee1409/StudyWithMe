package com.studywithme.repository;

import com.studywithme.entity.TinNhan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinNhanRepository extends JpaRepository<TinNhan, Long> {

    @EntityGraph(attributePaths = {"nguoiGui"})
    List<TinNhan> findByCuocTroChuyenIdOrderByThoiGianGuiAsc(Long cuocTroChuyenId);

    @EntityGraph(attributePaths = {"nguoiGui"})
    Page<TinNhan> findByCuocTroChuyenIdOrderByThoiGianGuiDesc(Long cuocTroChuyenId, Pageable pageable);

    long countByCuocTroChuyenIdAndNguoiGuiIdNotAndDaDocFalse(Long cuocTroChuyenId, Long nguoiGuiId);

    @Modifying
    @Query("UPDATE TinNhan t SET t.daDoc = true WHERE t.cuocTroChuyen.id = :cuocTroChuyenId AND t.nguoiGui.id <> :nguoiDocId AND t.daDoc = false")
    int markAllAsRead(@Param("cuocTroChuyenId") Long cuocTroChuyenId, @Param("nguoiDocId") Long nguoiDocId);
}

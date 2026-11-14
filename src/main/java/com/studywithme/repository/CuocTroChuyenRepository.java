package com.studywithme.repository;

import com.studywithme.entity.CuocTroChuyen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuocTroChuyenRepository extends JpaRepository<CuocTroChuyen, Long> {

    @EntityGraph(attributePaths = {"nguoiDung1", "nguoiDung2"})
    @Query("SELECT c FROM CuocTroChuyen c WHERE c.nguoiDung1.id = :userId OR c.nguoiDung2.id = :userId ORDER BY c.thoiGianTinNhanCuoi DESC NULLS LAST")
    List<CuocTroChuyen> findCuocTroChuyenByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"nguoiDung1", "nguoiDung2"})
    @Query("SELECT c FROM CuocTroChuyen c WHERE (c.nguoiDung1.id = :u1 AND c.nguoiDung2.id = :u2) OR (c.nguoiDung1.id = :u2 AND c.nguoiDung2.id = :u1)")
    Optional<CuocTroChuyen> findByTwoUsers(@Param("u1") Long u1, @Param("u2") Long u2);
}

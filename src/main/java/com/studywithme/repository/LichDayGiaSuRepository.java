package com.studywithme.repository;

import com.studywithme.entity.LichDayGiaSu;
import com.studywithme.entity.enums.giaovien.ThuTrongTuan;
import com.studywithme.entity.enums.giaovien.TrangThaiLichDay;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface LichDayGiaSuRepository extends JpaRepository<LichDayGiaSu, Long> {

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    List<LichDayGiaSu> findByGiangVienIdOrderByGioBatDauAsc(Long giangVienId);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    List<LichDayGiaSu> findByGiangVienIdAndThuTrongTuanOrderByGioBatDauAsc(Long giangVienId, ThuTrongTuan thuTrongTuan);

    @EntityGraph(attributePaths = {"giangVien", "giangVien.nguoiDung"})
    List<LichDayGiaSu> findByGiangVienIdAndTrangThaiOrderByGioBatDauAsc(Long giangVienId, TrangThaiLichDay trangThai);

    long countByGiangVienIdAndTrangThai(Long giangVienId, TrangThaiLichDay trangThai);

    boolean existsByGiangVienIdAndThuTrongTuanAndGioBatDauLessThanAndGioKetThucGreaterThan(
            Long giangVienId,
            ThuTrongTuan thuTrongTuan,
            LocalTime gioKetThuc,
            LocalTime gioBatDau
    );

    boolean existsByGiangVienIdAndThuTrongTuanAndGioBatDauLessThanAndGioKetThucGreaterThanAndIdNot(
            Long giangVienId,
            ThuTrongTuan thuTrongTuan,
            LocalTime gioKetThuc,
            LocalTime gioBatDau,
            Long excludedId
    );
}

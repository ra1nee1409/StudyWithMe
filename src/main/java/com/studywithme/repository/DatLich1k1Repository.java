package com.studywithme.repository;

import com.studywithme.entity.DatLich1k1;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DatLich1k1Repository extends JpaRepository<DatLich1k1, Long> {

    @Override
    @EntityGraph(attributePaths = {"hocVien", "giangVien", "giangVien.nguoiDung", "monHoc"})
    Optional<DatLich1k1> findById(Long id);

    Optional<DatLich1k1> findByMaDatLich(String maDatLich);

    Optional<DatLich1k1> findByOrderCodePayos(Long orderCodePayos);

    @EntityGraph(attributePaths = {"hocVien", "giangVien", "giangVien.nguoiDung", "monHoc"})
    List<DatLich1k1> findByHocVienIdOrderByThoiGianBatDauDesc(Long hocVienId);

    @EntityGraph(attributePaths = {"hocVien", "giangVien", "giangVien.nguoiDung", "monHoc"})
    List<DatLich1k1> findByGiangVienIdOrderByThoiGianBatDauDesc(Long giangVienId);

    @EntityGraph(attributePaths = {"hocVien", "giangVien", "giangVien.nguoiDung", "monHoc"})
    Page<DatLich1k1> findByGiangVienIdAndTrangThai(Long giangVienId, TrangThaiDatLich trangThai, Pageable pageable);

    @EntityGraph(attributePaths = {"hocVien", "giangVien", "giangVien.nguoiDung", "monHoc"})
    List<DatLich1k1> findByTrangThaiOrderByThoiGianBatDauDesc(TrangThaiDatLich trangThai);

    long countByGiangVienId(Long giangVienId);

    long countByHocVienId(Long hocVienId);

    long countByHocVienIdAndTrangThai(Long hocVienId, TrangThaiDatLich trangThai);

    long countByTrangThai(TrangThaiDatLich trangThai);

    boolean existsByGiangVienIdAndThoiGianBatDauLessThanAndThoiGianKetThucGreaterThanAndTrangThaiNot(
            Long giangVienId,
            LocalDateTime thoiGianKetThuc,
            LocalDateTime thoiGianBatDau,
            TrangThaiDatLich trangThaiExcluded
    );
}

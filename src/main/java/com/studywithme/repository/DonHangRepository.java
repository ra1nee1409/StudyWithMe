package com.studywithme.repository;

import com.studywithme.entity.DonHang;
import com.studywithme.entity.enums.TrangThaiThanhToan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    Optional<DonHang> findByMaDonHang(String maDonHang);
    List<DonHang> findByNguoiDungIdOrderByNgayTaoDonDesc(Long nguoiDungId);
    Page<DonHang> findByTrangThaiThanhToan(TrangThaiThanhToan trangThaiThanhToan, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(d.tongTienThanhToan) FROM DonHang d WHERE d.trangThaiThanhToan = :trangThai AND d.ngayTaoDon >= :startDate AND d.ngayTaoDon <= :endDate")
    java.math.BigDecimal sumDoanhThuKhoangThoiGian(@org.springframework.data.repository.query.Param("trangThai") TrangThaiThanhToan trangThai,
                                                   @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
                                                   @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);
}

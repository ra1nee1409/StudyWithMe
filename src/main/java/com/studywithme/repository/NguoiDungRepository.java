package com.studywithme.repository;

import com.studywithme.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<NguoiDung> findByHoTenContainingIgnoreCaseOrEmailContainingIgnoreCase(String hoTen, String email, Pageable pageable);
    List<NguoiDung> findTop5ByOrderByNgayTaoDesc();
    List<NguoiDung> findAllByOrderByNgayTaoDesc();
}

package com.studywithme.repository;

import com.studywithme.entity.CauHinhHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CauHinhHeThongRepository extends JpaRepository<CauHinhHeThong, Long> {

    Optional<CauHinhHeThong> findByMaCauHinh(String maCauHinh);

    boolean existsByMaCauHinh(String maCauHinh);
}


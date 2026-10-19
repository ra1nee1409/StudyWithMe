package com.studywithme.repository;

import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.shared.VaiTroName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {
    Optional<VaiTro> findByTenVaiTro(VaiTroName tenVaiTro);
    boolean existsByTenVaiTro(VaiTroName tenVaiTro);
}

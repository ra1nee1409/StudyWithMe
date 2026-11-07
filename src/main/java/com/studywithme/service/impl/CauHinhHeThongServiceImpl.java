package com.studywithme.service.impl;

import com.studywithme.entity.CauHinhHeThong;
import com.studywithme.repository.CauHinhHeThongRepository;
import com.studywithme.service.CauHinhHeThongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class CauHinhHeThongServiceImpl implements CauHinhHeThongService {

    public static final String MA_TY_LE_HOA_HONG = "TY_LE_HOA_HONG_SAN";
    public static final BigDecimal TY_LE_MAC_DINH = BigDecimal.valueOf(20.0); // 20%

    private final CauHinhHeThongRepository cauHinhHeThongRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal layTyLeHoaHongSan() {
        return cauHinhHeThongRepository.findByMaCauHinh(MA_TY_LE_HOA_HONG)
                .map(c -> {
                    try {
                        return new BigDecimal(c.getGiaTri().trim());
                    } catch (Exception e) {
                        log.warn("Giá trị cấu hình hoa hồng không hợp lệ, dùng mặc định 20%: {}", c.getGiaTri());
                        return TY_LE_MAC_DINH;
                    }
                })
                .orElse(TY_LE_MAC_DINH);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal layHeSoHoaHongSan() {
        BigDecimal tyLe = layTyLeHoaHongSan();
        return tyLe.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal layHeSoGiangVienNhan() {
        BigDecimal heSoSan = layHeSoHoaHongSan();
        return BigDecimal.ONE.subtract(heSoSan);
    }

    @Override
    @Transactional
    public void capNhatTyLeHoaHongSan(BigDecimal tyLeMoi) {
        if (tyLeMoi == null) {
            throw new IllegalArgumentException("Tỷ lệ hoa hồng không được để trống.");
        }
        if (tyLeMoi.compareTo(BigDecimal.ZERO) < 0 || tyLeMoi.compareTo(BigDecimal.valueOf(50.0)) > 0) {
            throw new IllegalArgumentException("Tỷ lệ hoa hồng nền tảng phải nằm trong khoảng từ 0% đến 50%.");
        }

        CauHinhHeThong config = cauHinhHeThongRepository.findByMaCauHinh(MA_TY_LE_HOA_HONG)
                .orElseGet(() -> CauHinhHeThong.builder()
                        .maCauHinh(MA_TY_LE_HOA_HONG)
                        .moTa("Tỷ lệ phần trăm hoa hồng nền tảng StudyWithMe giữ lại trên mỗi ca học")
                        .build());

        config.setGiaTri(tyLeMoi.setScale(1, RoundingMode.HALF_UP).toPlainString());
        cauHinhHeThongRepository.save(config);
        log.info("Admin đã cập nhật tỷ lệ hoa hồng nền tảng sang: {}%", config.getGiaTri());
    }
}


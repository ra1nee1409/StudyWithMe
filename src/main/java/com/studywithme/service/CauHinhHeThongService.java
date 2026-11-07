package com.studywithme.service;

import java.math.BigDecimal;

public interface CauHinhHeThongService {

    /**
     * Lấy giá trị phần trăm hoa hồng nền tảng (ví dụ: 20 tức là 20%)
     */
    BigDecimal layTyLeHoaHongSan();

    /**
     * Lấy hệ số phần trăm để tính toán số tiền (ví dụ: 0.20)
     */
    BigDecimal layHeSoHoaHongSan();

    /**
     * Lấy hệ số phần trăm giảng viên/gia sư nhận được (ví dụ: 0.80)
     */
    BigDecimal layHeSoGiangVienNhan();

    /**
     * Cập nhật tỷ lệ hoa hồng nền tảng mới
     */
    void capNhatTyLeHoaHongSan(BigDecimal tyLeMoi);
}


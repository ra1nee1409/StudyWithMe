package com.studywithme.entity.enums.giaovien;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiLichDay {
    CO_DINH("Lịch cố định hàng tuần"),
    LINH_HOAT("Lịch linh hoạt thỏa thuận");

    private final String moTa;
}

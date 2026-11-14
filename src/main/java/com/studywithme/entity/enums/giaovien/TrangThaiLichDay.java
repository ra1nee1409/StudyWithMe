package com.studywithme.entity.enums.giaovien;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiLichDay {
    CON_TRONG("Còn trống"),
    DA_DAT("Đã được đặt"),
    TAM_KHOA("Tạm khóa");

    private final String moTa;
}

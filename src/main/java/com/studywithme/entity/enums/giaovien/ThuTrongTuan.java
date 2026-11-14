package com.studywithme.entity.enums.giaovien;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThuTrongTuan {
    THU_HAI("Thứ 2", 2),
    THU_BA("Thứ 3", 3),
    THU_TU("Thứ 4", 4),
    THU_NAM("Thứ 5", 5),
    THU_SAU("Thứ 6", 6),
    THU_BAY("Thứ 7", 7),
    CHU_NHAT("Chủ Nhật", 1);

    private final String moTa;
    private final int giaTriThu;
}

package com.studywithme.entity.enums.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiTinNhan {
    VAN_BAN("Tin nhắn văn bản"),
    DE_XUAT_CA_HOC("Thẻ đề xuất ca học 1-1"),
    HINH_ANH("Hình ảnh đính kèm");

    private final String moTa;
}

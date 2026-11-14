package com.studywithme.entity.enums.hocvien;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiDatLich {
    CHO_XAC_NHAN("Chờ gia sư xác nhận"),
    CHO_THANH_TOAN("Chờ học viên thanh toán"),
    DA_THANH_TOAN_KY_QUY("Đã thanh toán (Sàn ký quỹ giữ tiền)"),
    DANG_DIEN_RA("Ca học đang diễn ra"),
    HOAN_THANH("Hoàn thành ca học"),
    DA_HUY("Đã hủy ca học"),
    TRANH_CHAP("Khiếu nại / Tranh chấp");

    private final String moTa;
}

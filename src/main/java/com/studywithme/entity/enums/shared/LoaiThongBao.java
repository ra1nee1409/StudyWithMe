package com.studywithme.entity.enums.shared;

/**
 * Phân loại thông báo In-app (WBS 7.1)
 */
public enum LoaiThongBao {
    CA_HOC_MOI,           // Có học viên đặt lịch ca học kèm mới
    DUYET_CA_HOC,         // Gia sư đã duyệt ca học kèm
    TU_CHOI_CA_HOC,       // Gia sư từ chối ca học
    KY_QUY_THANH_CONG,    // Ký quỹ học phí PayOS thành công
    GIAI_NGAN_VI,         // Học phí đã được giải ngân vào ví tiền gia sư
    TIN_NHAN_MOI,         // Có tin nhắn trò chuyện mới
    HE_THONG              // Thông báo chung từ ban quản trị
}

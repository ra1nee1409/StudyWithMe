package com.studywithme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDongTienResponse {
    private BigDecimal tongTienVao;          // Tổng tiền học viên nộp vào (Doanh thu)
    private BigDecimal tongTienRa;           // Tổng tiền đã chuyển khoản cho giảng viên
    private BigDecimal loiNhuanSan;          // 20% phí nền tảng giữ lại
    private BigDecimal tongSoDuViGiangVien;  // Tổng số dư ví khả dụng của các giảng viên
    private long soDonHangThanhCong;         // Số đơn hàng thành công
    private long soLenhRutThanhCong;         // Số lệnh rút tiền thành công
    private long tongSoGiaoDich;             // Tổng số lượng giao dịch
}


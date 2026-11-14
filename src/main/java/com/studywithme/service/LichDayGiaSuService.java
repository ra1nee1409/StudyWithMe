package com.studywithme.service;

import com.studywithme.dto.tutor.LichDaySlotDto;
import com.studywithme.dto.tutor.LichDaySlotRequest;
import com.studywithme.entity.LichDayGiaSu;
import com.studywithme.entity.enums.giaovien.ThuTrongTuan;
import com.studywithme.entity.enums.giaovien.TrangThaiLichDay;

import java.util.List;

public interface LichDayGiaSuService {

    List<LichDaySlotDto> layDanhSachLichDay(Long giangVienId);

    List<LichDaySlotDto> layDanhSachLichDayTheoThu(Long giangVienId, ThuTrongTuan thuTrongTuan);

    List<LichDaySlotDto> layDanhSachLichDayKhaDung(Long giangVienId);

    LichDayGiaSu taoHoacCapNhatSlot(Long giangVienId, LichDaySlotRequest request);

    void xoaSlot(Long giangVienId, Long slotId);

    LichDayGiaSu toggleTrangThaiSlot(Long giangVienId, Long slotId);
}

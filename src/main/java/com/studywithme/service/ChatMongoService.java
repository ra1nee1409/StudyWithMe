package com.studywithme.service;

import com.studywithme.dto.chat.CuocTroChuyenDto;
import com.studywithme.dto.chat.GuiTinNhanRequest;
import com.studywithme.dto.chat.TinNhanDto;

import java.util.List;

public interface ChatMongoService {

    TinNhanDto guiTinNhan(Long nguoiGuiId, GuiTinNhanRequest request);

    List<CuocTroChuyenDto> layDanhSachCuocTroChuyen(Long currentUserId);

    List<TinNhanDto> layLichSuTinNhan(Long currentUserId, String cuocTroChuyenId);

    int danhDauDaDoc(Long currentUserId, String cuocTroChuyenId);

    CuocTroChuyenDto layHoacTaoCuocTroChuyen(Long currentUserId, Long targetUserId);
}

package com.studywithme.service;

import com.studywithme.dto.tutor.CapNhatLinkMeetRequest;
import com.studywithme.dto.tutor.DatCaHoc1k1Request;
import com.studywithme.dto.tutor.DatLich1k1Dto;
import com.studywithme.dto.tutor.XacNhanCaHocRequest;
import com.studywithme.entity.DatLich1k1;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;

import java.util.List;

public interface DatLich1k1Service {

    DatLich1k1 taoYeuCauDatLich(Long hocVienId, DatCaHoc1k1Request request);

    DatLich1k1 giaSuPhanHoiCaHoc(Long giangVienId, Long datLichId, XacNhanCaHocRequest request);

    DatLich1k1 capNhatLinkMeet(Long giangVienId, Long datLichId, CapNhatLinkMeetRequest request);

    DatLich1k1 xacNhanThanhToanKyQuy(Long datLichId);

    DatLich1k1 hoanTatCaHoc(Long giangVienId, Long datLichId);

    DatLich1k1 hocVienHuyCaHoc(Long hocVienId, Long datLichId, String lyDoHuy);

    List<DatLich1k1Dto> layDanhSachCaHocCuaGiaSu(Long giangVienId, TrangThaiDatLich trangThai);

    List<DatLich1k1Dto> layDanhSachCaHocCuaHocVien(Long hocVienId);

    DatLich1k1Dto layChiTietCaHoc(Long id);
}

package com.studywithme.service;

import com.studywithme.dto.tutor.CapNhatKhoaHocRequest;
import com.studywithme.dto.tutor.TaoBaiHocRequest;
import com.studywithme.dto.tutor.TaoChuongHocRequest;
import com.studywithme.dto.tutor.TaoKhoaHocRequest;
import com.studywithme.entity.BaiHoc;
import com.studywithme.entity.ChuongHoc;
import com.studywithme.entity.KhoaHoc;

import java.util.List;

public interface TutorKhoaHocService {

    List<KhoaHoc> layDanhSachKhoaHocCuaGiangVien(Long giangVienId);

    KhoaHoc layChiTietKhoaHocCuaGiangVien(Long giangVienId, Long khoaHocId);

    KhoaHoc taoKhoaHocMoi(Long giangVienId, TaoKhoaHocRequest request);

    KhoaHoc capNhatKhoaHoc(Long giangVienId, Long khoaHocId, CapNhatKhoaHocRequest request);

    KhoaHoc guiKiemDuyet(Long giangVienId, Long khoaHocId);

    void xoaKhoaHoc(Long giangVienId, Long khoaHocId);

    ChuongHoc themChuongHoc(Long giangVienId, Long khoaHocId, TaoChuongHocRequest request);

    void xoaChuongHoc(Long giangVienId, Long chuongHocId);

    BaiHoc themBaiHoc(Long giangVienId, Long chuongHocId, TaoBaiHocRequest request);

    void xoaBaiHoc(Long giangVienId, Long baiHocId);
}


package com.studywithme.service;

import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.LopHocTrucTuyen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GiaSuService {

    Page<HoSoGiangVien> locGiaSu(String keyword, String loaiChungChi, Pageable pageable);

    HoSoGiangVien layChiTietGiaSu(Long id);

    List<LopHocTrucTuyen> layLopHocCuaGiaSu(Long giangVienId);

    List<KhoaHoc> layKhoaHocCuaGiaSu(Long giangVienId);

    List<HoSoGiangVien> layGiaSuTuongTu(Long currentId);

    List<HoSoGiangVien> layTopGiaSuTieuBieu();
}

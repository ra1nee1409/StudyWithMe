package com.studywithme.service;

import com.studywithme.dto.notification.ThongBaoDto;
import com.studywithme.entity.enums.shared.LoaiThongBao;

import java.util.List;

public interface ThongBaoService {

    ThongBaoDto taoThongBao(Long nguoiDungId, String tieuDe, String noiDung, LoaiThongBao loai, String url);

    List<ThongBaoDto> layDanhSachThongBao(Long nguoiDungId, int limit);

    long demSoChuaDoc(Long nguoiDungId);

    boolean danhDauDaDoc(Long thongBaoId, Long nguoiDungId);

    int danhDauTatCaDaDoc(Long nguoiDungId);
}

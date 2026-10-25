package com.studywithme.service;

import com.studywithme.dto.response.ThongKeRutTienResponse;
import com.studywithme.dto.response.YeuCauRutTienResponse;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;

import java.util.List;

public interface AdminTaiChinhService {

    List<YeuCauRutTienResponse> layDanhSachYeuCauRutTien(TrangThaiRutTien trangThai);

    ThongKeRutTienResponse layThongKeRutTien();

    YeuCauRutTienResponse layChiTietYeuCau(Long id);

    YeuCauRutTienResponse xacNhanChuyenTien(Long id, String ghiChuAdmin);

    YeuCauRutTienResponse tuChoiRutTien(Long id, String lyDoTuChoi);
}

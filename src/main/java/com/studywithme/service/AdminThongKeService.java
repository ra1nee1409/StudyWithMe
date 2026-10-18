package com.studywithme.service;

import com.studywithme.dto.response.DoanhThuThangResponse;
import com.studywithme.dto.response.ThongKeTongQuanResponse;
import com.studywithme.dto.response.TyLeDanhMucResponse;

public interface AdminThongKeService {
    ThongKeTongQuanResponse layThongKeTongQuan();
    DoanhThuThangResponse layDoanhThuTheoNam(Integer nam);
    TyLeDanhMucResponse layTyLeTheoDanhMuc();
}

package com.studywithme.service;

import com.studywithme.dto.request.DanhMucRequest;
import com.studywithme.dto.response.DanhMucResponse;

import java.util.List;
import java.util.Map;

public interface DanhMucService {
    List<DanhMucResponse> layCayDanhMuc();
    List<DanhMucResponse> layTatCaDanhMucPhang();
    DanhMucResponse layChiTietDanhMuc(Long id);
    DanhMucResponse themDanhMuc(DanhMucRequest request);
    DanhMucResponse capNhatDanhMuc(Long id, DanhMucRequest request);
    DanhMucResponse doiTrangThaiKichHoat(Long id);
    void xoaDanhMuc(Long id);
    Map<String, Object> layThongKeDanhMuc();
}

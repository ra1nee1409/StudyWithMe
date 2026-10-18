package com.studywithme.service;

import com.studywithme.dto.request.BannerRequest;
import com.studywithme.entity.BannerQuangCao;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerService {
    List<BannerQuangCao> layTatCaBanner();
    List<BannerQuangCao> layBannerDangKichHoat();
    BannerQuangCao layBannerTheoId(Long id);
    BannerQuangCao themBanner(BannerRequest request, MultipartFile file);
    BannerQuangCao capNhatBanner(Long id, BannerRequest request, MultipartFile file);
    BannerQuangCao doiTrangThaiKichHoat(Long id);
    void xoaBanner(Long id);
}

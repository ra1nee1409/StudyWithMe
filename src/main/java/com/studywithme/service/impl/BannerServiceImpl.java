package com.studywithme.service.impl;

import com.studywithme.dto.request.BannerRequest;
import com.studywithme.entity.BannerQuangCao;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.BannerQuangCaoRepository;
import com.studywithme.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class BannerServiceImpl implements BannerService {

    private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);
    private final BannerQuangCaoRepository bannerQuangCaoRepository;

    public BannerServiceImpl(BannerQuangCaoRepository bannerQuangCaoRepository) {
        this.bannerQuangCaoRepository = bannerQuangCaoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerQuangCao> layTatCaBanner() {
        return bannerQuangCaoRepository.findAllByOrderByThuTuHienThiAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerQuangCao> layBannerDangKichHoat() {
        return bannerQuangCaoRepository.findByKichHoatTrueOrderByThuTuHienThiAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public BannerQuangCao layBannerTheoId(Long id) {
        return bannerQuangCaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy banner với ID: " + id));
    }

    @Override
    @Transactional
    public BannerQuangCao themBanner(BannerRequest request, MultipartFile file) {
        String linkAnh = luuAnhHoacLayLink(request.getLinkAnhBanner(), file);

        if (!StringUtils.hasText(linkAnh)) {
            throw new IllegalArgumentException("Vui lòng tải lên ảnh banner hoặc cung cấp đường dẫn ảnh URL.");
        }

        BannerQuangCao banner = BannerQuangCao.builder()
                .tieuDe(request.getTieuDe().trim())
                .linkAnhBanner(linkAnh)
                .linkChuyenHuong(StringUtils.hasText(request.getLinkChuyenHuong()) ? request.getLinkChuyenHuong().trim() : "#")
                .thuTuHienThi(request.getThuTuHienThi() != null ? request.getThuTuHienThi() : 0)
                .kichHoat(request.getKichHoat() != null ? request.getKichHoat() : true)
                .build();

        return bannerQuangCaoRepository.save(banner);
    }

    @Override
    @Transactional
    public BannerQuangCao capNhatBanner(Long id, BannerRequest request, MultipartFile file) {
        BannerQuangCao banner = layBannerTheoId(id);

        if (StringUtils.hasText(request.getTieuDe())) {
            banner.setTieuDe(request.getTieuDe().trim());
        }

        String linkAnhMoi = luuAnhHoacLayLink(request.getLinkAnhBanner(), file);
        if (StringUtils.hasText(linkAnhMoi)) {
            banner.setLinkAnhBanner(linkAnhMoi);
        }

        if (request.getLinkChuyenHuong() != null) {
            banner.setLinkChuyenHuong(request.getLinkChuyenHuong().trim());
        }

        if (request.getThuTuHienThi() != null) {
            banner.setThuTuHienThi(request.getThuTuHienThi());
        }

        if (request.getKichHoat() != null) {
            banner.setKichHoat(request.getKichHoat());
        }

        return bannerQuangCaoRepository.save(banner);
    }

    @Override
    @Transactional
    public BannerQuangCao doiTrangThaiKichHoat(Long id) {
        BannerQuangCao banner = layBannerTheoId(id);
        banner.setKichHoat(!Boolean.TRUE.equals(banner.getKichHoat()));
        return bannerQuangCaoRepository.save(banner);
    }

    @Override
    @Transactional
    public void xoaBanner(Long id) {
        BannerQuangCao banner = layBannerTheoId(id);
        bannerQuangCaoRepository.delete(banner);
    }

    private String luuAnhHoacLayLink(String linkAnhFromBody, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                Path uploadDir = Paths.get("uploads", "banners");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "banner.png");
                String fileExtension = "";
                int dotIndex = originalFileName.lastIndexOf(".");
                if (dotIndex >= 0) {
                    fileExtension = originalFileName.substring(dotIndex);
                }

                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                Path targetLocation = uploadDir.resolve(uniqueFileName);

                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                }

                return "/uploads/banners/" + uniqueFileName;
            } catch (IOException ex) {
                logger.error("Lỗi khi lưu file ảnh banner: ", ex);
                throw new RuntimeException("Không thể lưu file ảnh banner tải lên", ex);
            }
        }

        return StringUtils.hasText(linkAnhFromBody) ? linkAnhFromBody.trim() : null;
    }
}

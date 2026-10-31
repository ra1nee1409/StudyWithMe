package com.studywithme.service.impl;

import com.studywithme.dto.request.TienDoBaiHocRequest;
import com.studywithme.dto.response.PhongHocDto;
import com.studywithme.dto.response.TienDoKhoaHocResponse;
import com.studywithme.entity.BaiHoc;
import com.studywithme.entity.ChuongHoc;
import com.studywithme.entity.GhiDanhKhoaHoc;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.TienDoBaiHoc;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.BaiHocRepository;
import com.studywithme.repository.ChuongHocRepository;
import com.studywithme.repository.GhiDanhKhoaHocRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.TienDoBaiHocRepository;
import com.studywithme.service.StudentLearningService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StudentLearningServiceImpl implements StudentLearningService {

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
            "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:watch\\?v=|embed\\/)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})"
    );

    private final KhoaHocRepository khoaHocRepository;
    private final ChuongHocRepository chuongHocRepository;
    private final BaiHocRepository baiHocRepository;
    private final GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository;
    private final TienDoBaiHocRepository tienDoBaiHocRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public StudentLearningServiceImpl(KhoaHocRepository khoaHocRepository,
                                     ChuongHocRepository chuongHocRepository,
                                     BaiHocRepository baiHocRepository,
                                     GhiDanhKhoaHocRepository ghiDanhKhoaHocRepository,
                                     TienDoBaiHocRepository tienDoBaiHocRepository,
                                     NguoiDungRepository nguoiDungRepository) {
        this.khoaHocRepository = khoaHocRepository;
        this.chuongHocRepository = chuongHocRepository;
        this.baiHocRepository = baiHocRepository;
        this.ghiDanhKhoaHocRepository = ghiDanhKhoaHocRepository;
        this.tienDoBaiHocRepository = tienDoBaiHocRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    @Transactional
    public PhongHocDto layThongTinPhongHoc(String courseSlug, Long studentId, Long lessonId) {
        KhoaHoc khoaHoc = khoaHocRepository.findByDuongDanSlug(courseSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với đường dẫn: " + courseSlug));

        GhiDanhKhoaHoc ghiDanh = ghiDanhKhoaHocRepository.findByNguoiDungIdAndKhoaHocId(studentId, khoaHoc.getId())
                .orElseThrow(() -> new AccessDeniedException("Bạn chưa ghi danh khóa học này. Vui lòng thanh toán học phí để truy cập bài học."));

        List<ChuongHoc> danhSachChuongHoc = chuongHocRepository.findByKhoaHocIdOrderByThuTuSapXepAsc(khoaHoc.getId());

        List<BaiHoc> allLessons = new ArrayList<>();
        for (ChuongHoc chuong : danhSachChuongHoc) {
            List<BaiHoc> baiHocList = baiHocRepository.findByChuongHocIdOrderByThuTuSapXepAsc(chuong.getId());
            if (chuong.getDanhSachBaiHoc() != null) {
                chuong.getDanhSachBaiHoc().clear();
                chuong.getDanhSachBaiHoc().addAll(baiHocList);
            }
            allLessons.addAll(baiHocList);
        }

        List<TienDoBaiHoc> tienDoList = tienDoBaiHocRepository
                .findByNguoiDungIdAndBaiHocChuongHocKhoaHocId(studentId, khoaHoc.getId());

        Set<Long> tapHopBaiHocDaHoanThanh = tienDoList.stream()
                .filter(t -> Boolean.TRUE.equals(t.getDaHoanThanh()))
                .map(t -> t.getBaiHoc().getId())
                .collect(Collectors.toSet());

        BaiHoc baiHocHienTai = null;
        if (lessonId != null) {
            baiHocHienTai = allLessons.stream()
                    .filter(b -> Objects.equals(b.getId(), lessonId))
                    .findFirst()
                    .orElse(null);
        }

        if (baiHocHienTai == null) {
            baiHocHienTai = allLessons.stream()
                    .filter(b -> !tapHopBaiHocDaHoanThanh.contains(b.getId()))
                    .findFirst()
                    .orElse(!allLessons.isEmpty() ? allLessons.get(0) : null);
        }

        BaiHoc baiHocTruoc = null;
        BaiHoc baiHocTiepTheo = null;
        if (baiHocHienTai != null && !allLessons.isEmpty()) {
            int currentIndex = allLessons.indexOf(baiHocHienTai);
            if (currentIndex > 0) {
                baiHocTruoc = allLessons.get(currentIndex - 1);
            }
            if (currentIndex >= 0 && currentIndex < allLessons.size() - 1) {
                baiHocTiepTheo = allLessons.get(currentIndex + 1);
            }
        }

        long tongSoBai = allLessons.size();
        long soBaiHoanThanh = tapHopBaiHocDaHoanThanh.size();
        int phanTram = tongSoBai > 0 ? (int) Math.round((double) soBaiHoanThanh * 100 / tongSoBai) : 0;

        if (ghiDanh.getPhanTramTienDo() == null || ghiDanh.getPhanTramTienDo() != phanTram) {
            ghiDanh.setPhanTramTienDo(phanTram);
            ghiDanhKhoaHocRepository.save(ghiDanh);
        }

        String embedUrl = (baiHocHienTai != null) ? formatVideoEmbedUrl(baiHocHienTai.getLinkVideo()) : "";

        return PhongHocDto.builder()
                .khoaHoc(khoaHoc)
                .danhSachChuongHoc(danhSachChuongHoc)
                .baiHocHienTai(baiHocHienTai)
                .baiHocTruoc(baiHocTruoc)
                .baiHocTiepTheo(baiHocTiepTheo)
                .tapHopBaiHocDaHoanThanh(tapHopBaiHocDaHoanThanh)
                .phanTramTienDo(phanTram)
                .soBaiDaHoanThanh(soBaiHoanThanh)
                .tongSoBaiHoc(tongSoBai)
                .embedVideoUrl(embedUrl)
                .build();
    }

    @Override
    @Transactional
    public TienDoKhoaHocResponse capNhatTienDo(Long studentId, TienDoBaiHocRequest request) {
        BaiHoc baiHoc = baiHocRepository.findById(request.getBaiHocId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học với ID: " + request.getBaiHocId()));

        KhoaHoc khoaHoc = baiHoc.getChuongHoc().getKhoaHoc();
        GhiDanhKhoaHoc ghiDanh = ghiDanhKhoaHocRepository.findByNguoiDungIdAndKhoaHocId(studentId, khoaHoc.getId())
                .orElseThrow(() -> new AccessDeniedException("Học viên chưa ghi danh khóa học chứa bài học này"));

        TienDoBaiHoc tienDo = tienDoBaiHocRepository
                .findByNguoiDungIdAndBaiHocId(studentId, baiHoc.getId())
                .orElseGet(() -> {
                    NguoiDung nguoiDung = nguoiDungRepository.findById(studentId)
                            .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
                    return TienDoBaiHoc.builder()
                            .nguoiDung(nguoiDung)
                            .baiHoc(baiHoc)
                            .build();
                });

        boolean daHoanThanh = Boolean.TRUE.equals(request.getDaHoanThanh());
        tienDo.setDaHoanThanh(daHoanThanh);
        tienDo.setThoiGianHoanThanh(daHoanThanh ? LocalDateTime.now() : null);
        if (request.getGiayDungLai() != null && request.getGiayDungLai() >= 0) {
            tienDo.setGiayDungLai(request.getGiayDungLai());
        }

        tienDoBaiHocRepository.save(tienDo);

        long soBaiHoanThanh = tienDoBaiHocRepository
                .countByNguoiDungIdAndBaiHocChuongHocKhoaHocIdAndDaHoanThanhTrue(studentId, khoaHoc.getId());

        long tongSoBai = baiHocRepository.countByChuongHocKhoaHocId(khoaHoc.getId());

        int phanTram = tongSoBai > 0 ? (int) Math.round((double) soBaiHoanThanh * 100 / tongSoBai) : 0;
        ghiDanh.setPhanTramTienDo(phanTram);
        ghiDanhKhoaHocRepository.save(ghiDanh);

        String message = daHoanThanh ? "Đã đánh dấu hoàn thành bài học!" : "Đã hủy đánh dấu hoàn thành bài học";

        return TienDoKhoaHocResponse.builder()
                .baiHocId(baiHoc.getId())
                .daHoanThanh(daHoanThanh)
                .phanTramTienDo(phanTram)
                .soBaiDaHoanThanh(soBaiHoanThanh)
                .tongSoBaiHoc(tongSoBai)
                .thongBao(message)
                .build();
    }

    @Override
    public String formatVideoEmbedUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return "";
        }
        String trimmed = rawUrl.trim();
        Matcher matcher = YOUTUBE_PATTERN.matcher(trimmed);
        if (matcher.find()) {
            String videoId = matcher.group(1);
            return "https://www.youtube.com/embed/" + videoId + "?autoplay=1&rel=0&modestbranding=1&enablejsapi=1";
        }
        return trimmed;
    }
}

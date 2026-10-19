package com.studywithme.service.impl;

import com.studywithme.dto.request.DanhMucRequest;
import com.studywithme.dto.response.DanhMucResponse;
import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.enums.shared.LoaiDanhMuc;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.DanhMucRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.repository.LopHocTrucTuyenRepository;
import com.studywithme.service.DanhMucService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DanhMucServiceImpl implements DanhMucService {

    private static final Logger logger = LoggerFactory.getLogger(DanhMucServiceImpl.class);

    private final DanhMucRepository danhMucRepository;
    private final KhoaHocRepository khoaHocRepository;
    private final LopHocTrucTuyenRepository lopHocTrucTuyenRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DanhMucResponse> layCayDanhMuc() {
        List<DanhMuc> rootList = danhMucRepository.findByDanhMucChaIsNullOrderByThuTuHienThiAsc();
        return rootList.stream()
                .map(this::chuyenThanhResponseDeQuy)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhMucResponse> layTatCaDanhMucPhang() {
        List<DanhMuc> allList = danhMucRepository.findAllByOrderByThuTuHienThiAsc();
        return allList.stream()
                .map(this::chuyenThanhResponseDonGian)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DanhMucResponse layChiTietDanhMuc(Long id) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));
        return chuyenThanhResponseDeQuy(danhMuc);
    }

    @Override
    @Transactional
    public DanhMucResponse themDanhMuc(DanhMucRequest request) {
        if (!StringUtils.hasText(request.getTenDanhMuc())) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }

        String slug = taoSlugKiemTra(request.getDuongDanSlug(), request.getTenDanhMuc(), null);

        DanhMuc danhMucCha = null;
        if (request.getDanhMucChaId() != null) {
            danhMucCha = danhMucRepository.findById(request.getDanhMucChaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha với ID: " + request.getDanhMucChaId()));
        }

        LoaiDanhMuc loai = request.getLoaiDanhMuc();
        if (loai == null) {
            loai = (danhMucCha != null) ? danhMucCha.getLoaiDanhMuc() : LoaiDanhMuc.MON_HOC;
        }

        DanhMuc danhMuc = DanhMuc.builder()
                .tenDanhMuc(request.getTenDanhMuc().trim())
                .duongDanSlug(slug)
                .danhMucCha(danhMucCha)
                .loaiDanhMuc(loai)
                .linkIcon(StringUtils.hasText(request.getLinkIcon()) ? request.getLinkIcon().trim() : "folder")
                .thuTuHienThi(request.getThuTuHienThi() != null ? request.getThuTuHienThi() : 0)
                .kichHoat(request.getKichHoat() != null ? request.getKichHoat() : true)
                .build();

        DanhMuc saved = danhMucRepository.save(danhMuc);
        logger.info(">>> Đã thêm danh mục mới: {} (ID: {})", saved.getTenDanhMuc(), saved.getId());
        return chuyenThanhResponseDonGian(saved);
    }

    @Override
    @Transactional
    public DanhMucResponse capNhatDanhMuc(Long id, DanhMucRequest request) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        if (StringUtils.hasText(request.getTenDanhMuc())) {
            danhMuc.setTenDanhMuc(request.getTenDanhMuc().trim());
        }

        String slug = taoSlugKiemTra(request.getDuongDanSlug(), request.getTenDanhMuc(), id);
        danhMuc.setDuongDanSlug(slug);

        if (request.getDanhMucChaId() != null) {
            if (request.getDanhMucChaId().equals(id)) {
                throw new IllegalArgumentException("Danh mục cha không thể là chính danh mục này.");
            }
            if (kiemTraVongLapChaCon(id, request.getDanhMucChaId())) {
                throw new IllegalArgumentException("Không thể chọn danh mục con làm danh mục cha (gây lặp vô tận).");
            }

            DanhMuc chaMoi = danhMucRepository.findById(request.getDanhMucChaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha với ID: " + request.getDanhMucChaId()));
            danhMuc.setDanhMucCha(chaMoi);
        } else {
            danhMuc.setDanhMucCha(null);
        }

        if (request.getLoaiDanhMuc() != null) {
            danhMuc.setLoaiDanhMuc(request.getLoaiDanhMuc());
        }

        if (request.getLinkIcon() != null) {
            danhMuc.setLinkIcon(StringUtils.hasText(request.getLinkIcon()) ? request.getLinkIcon().trim() : "folder");
        }

        if (request.getThuTuHienThi() != null) {
            danhMuc.setThuTuHienThi(request.getThuTuHienThi());
        }

        if (request.getKichHoat() != null) {
            danhMuc.setKichHoat(request.getKichHoat());
        }

        DanhMuc saved = danhMucRepository.save(danhMuc);
        logger.info(">>> Đã cập nhật danh mục: {} (ID: {})", saved.getTenDanhMuc(), saved.getId());
        return chuyenThanhResponseDonGian(saved);
    }

    @Override
    @Transactional
    public DanhMucResponse doiTrangThaiKichHoat(Long id) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        boolean statusMoi = !Boolean.TRUE.equals(danhMuc.getKichHoat());
        danhMuc.setKichHoat(statusMoi);
        DanhMuc saved = danhMucRepository.save(danhMuc);
        logger.info(">>> Đổi trạng thái kích hoạt danh mục ID {}: {}", id, statusMoi);
        return chuyenThanhResponseDonGian(saved);
    }

    @Override
    @Transactional
    public void xoaDanhMuc(Long id) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        // 1. Kiểm tra danh mục con
        if (danhMucRepository.existsByDanhMucChaId(id)) {
            throw new IllegalArgumentException("Không thể xóa danh mục '" + danhMuc.getTenDanhMuc()
                    + "' vì đang chứa các danh mục con. Vui lòng chuyển hoặc xóa các danh mục con trước.");
        }

        // 2. Kiểm tra khóa học liên kết
        long soKhoaHoc = khoaHocRepository.countByDanhMucId(id);
        if (soKhoaHoc > 0) {
            throw new IllegalArgumentException("Không thể xóa danh mục '" + danhMuc.getTenDanhMuc()
                    + "' vì đang có " + soKhoaHoc + " khóa học trực thuộc.");
        }

        // 3. Kiểm tra lớp học trực tuyến liên kết
        long soLopHoc = lopHocTrucTuyenRepository.countByDanhMucId(id);
        if (soLopHoc > 0) {
            throw new IllegalArgumentException("Không thể xóa danh mục '" + danhMuc.getTenDanhMuc()
                    + "' vì đang có " + soLopHoc + " lớp học trực tuyến trực thuộc.");
        }

        danhMucRepository.delete(danhMuc);
        logger.info(">>> Đã xóa danh mục ID {}: {}", id, danhMuc.getTenDanhMuc());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> layThongKeDanhMuc() {
        long tongGoc = danhMucRepository.countByDanhMucChaIsNull();
        long tongCon = danhMucRepository.countByDanhMucChaIsNotNull();
        long tongKichHoat = danhMucRepository.countByKichHoatTrue();
        long tongKhoaHoc = khoaHocRepository.count();
        long tongLopHoc = lopHocTrucTuyenRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("tongDanhMucGoc", tongGoc);
        stats.put("tongDanhMucCon", tongCon);
        stats.put("tongKichHoat", tongKichHoat);
        stats.put("tongKhoaHoc", tongKhoaHoc);
        stats.put("tongLopHoc", tongLopHoc);
        return stats;
    }

    private DanhMucResponse chuyenThanhResponseDeQuy(DanhMuc dm) {
        long soKhoaHoc = khoaHocRepository.countByDanhMucId(dm.getId());
        long soLopHoc = lopHocTrucTuyenRepository.countByDanhMucId(dm.getId());

        List<DanhMuc> children = danhMucRepository.findByDanhMucChaIdOrderByThuTuHienThiAsc(dm.getId());
        List<DanhMucResponse> childrenResponses = children.stream()
                .map(this::chuyenThanhResponseDeQuy)
                .collect(Collectors.toList());

        return DanhMucResponse.builder()
                .id(dm.getId())
                .tenDanhMuc(dm.getTenDanhMuc())
                .duongDanSlug(dm.getDuongDanSlug())
                .danhMucChaId(dm.getDanhMucCha() != null ? dm.getDanhMucCha().getId() : null)
                .tenDanhMucCha(dm.getDanhMucCha() != null ? dm.getDanhMucCha().getTenDanhMuc() : null)
                .loaiDanhMuc(dm.getLoaiDanhMuc())
                .loaiDanhMucTen(layTenLoaiDanhMuc(dm.getLoaiDanhMuc()))
                .linkIcon(dm.getLinkIcon())
                .thuTuHienThi(dm.getThuTuHienThi())
                .kichHoat(dm.getKichHoat())
                .soKhoaHoc(soKhoaHoc)
                .soLopHoc(soLopHoc)
                .danhSachDanhMucCon(childrenResponses)
                .build();
    }

    private DanhMucResponse chuyenThanhResponseDonGian(DanhMuc dm) {
        long soKhoaHoc = khoaHocRepository.countByDanhMucId(dm.getId());
        long soLopHoc = lopHocTrucTuyenRepository.countByDanhMucId(dm.getId());

        return DanhMucResponse.builder()
                .id(dm.getId())
                .tenDanhMuc(dm.getTenDanhMuc())
                .duongDanSlug(dm.getDuongDanSlug())
                .danhMucChaId(dm.getDanhMucCha() != null ? dm.getDanhMucCha().getId() : null)
                .tenDanhMucCha(dm.getDanhMucCha() != null ? dm.getDanhMucCha().getTenDanhMuc() : null)
                .loaiDanhMuc(dm.getLoaiDanhMuc())
                .loaiDanhMucTen(layTenLoaiDanhMuc(dm.getLoaiDanhMuc()))
                .linkIcon(dm.getLinkIcon())
                .thuTuHienThi(dm.getThuTuHienThi())
                .kichHoat(dm.getKichHoat())
                .soKhoaHoc(soKhoaHoc)
                .soLopHoc(soLopHoc)
                .danhSachDanhMucCon(new ArrayList<>())
                .build();
    }

    private String layTenLoaiDanhMuc(LoaiDanhMuc loai) {
        if (loai == null) return "Môn học";
        return switch (loai) {
            case CAP_HOC_K12 -> "Cấp học K-12";
            case CHUNG_CHI_NGOAI_NGU -> "Chứng chỉ Quốc tế";
            case MON_HOC -> "Môn học";
        };
    }

    private boolean kiemTraVongLapChaCon(Long currentId, Long targetParentId) {
        Long tempId = targetParentId;
        Set<Long> visited = new HashSet<>();
        while (tempId != null) {
            if (tempId.equals(currentId)) {
                return true;
            }
            if (!visited.add(tempId)) {
                return true;
            }
            Optional<DanhMuc> p = danhMucRepository.findById(tempId);
            if (p.isPresent() && p.get().getDanhMucCha() != null) {
                tempId = p.get().getDanhMucCha().getId();
            } else {
                break;
            }
        }
        return false;
    }

    private String taoSlugKiemTra(String inputSlug, String tenDanhMuc, Long idHienTai) {
        String baseSlug = StringUtils.hasText(inputSlug) ? toSlug(inputSlug) : toSlug(tenDanhMuc);
        if (!StringUtils.hasText(baseSlug)) {
            baseSlug = "danh-muc-" + System.currentTimeMillis();
        }

        String candidateSlug = baseSlug;
        int count = 1;
        while (true) {
            boolean exists;
            if (idHienTai != null) {
                exists = danhMucRepository.existsByDuongDanSlugAndIdNot(candidateSlug, idHienTai);
            } else {
                exists = danhMucRepository.existsByDuongDanSlug(candidateSlug);
            }

            if (!exists) {
                return candidateSlug;
            }
            candidateSlug = baseSlug + "-" + (++count);
        }
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) return "";
        String normalized = Normalizer.normalize(input.trim().toLowerCase(), Normalizer.Form.NFD);
        String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");
        slug = slug.replace("đ", "d").replace("Đ", "d");
        slug = Pattern.compile("[^a-z0-9\\s-]").matcher(slug).replaceAll("");
        slug = Pattern.compile("[\\s-]+").matcher(slug).replaceAll("-");
        slug = Pattern.compile("^-+|-+$").matcher(slug).replaceAll("");
        return slug;
    }
}

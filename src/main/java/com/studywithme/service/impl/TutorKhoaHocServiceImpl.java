package com.studywithme.service.impl;

import com.studywithme.dto.tutor.CapNhatKhoaHocRequest;
import com.studywithme.dto.tutor.TaoBaiHocRequest;
import com.studywithme.dto.tutor.TaoChuongHocRequest;
import com.studywithme.dto.tutor.TaoKhoaHocRequest;
import com.studywithme.entity.BaiHoc;
import com.studywithme.entity.ChuongHoc;
import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.BaiHocRepository;
import com.studywithme.repository.ChuongHocRepository;
import com.studywithme.repository.DanhMucRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.service.TutorKhoaHocService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TutorKhoaHocServiceImpl implements TutorKhoaHocService {

    private final KhoaHocRepository khoaHocRepository;
    private final ChuongHocRepository chuongHocRepository;
    private final BaiHocRepository baiHocRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final DanhMucRepository danhMucRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KhoaHoc> layDanhSachKhoaHocCuaGiangVien(Long giangVienId) {
        return khoaHocRepository.findByGiangVienIdOrderByNgayTaoDesc(giangVienId);
    }

    @Override
    @Transactional(readOnly = true)
    public KhoaHoc layChiTietKhoaHocCuaGiangVien(Long giangVienId, Long khoaHocId) {
        KhoaHoc kh = khoaHocRepository.findChiTietById(khoaHocId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học #" + khoaHocId));

        kiemTraQuyenSoHuu(kh, giangVienId);
        return kh;
    }

    @Override
    @Transactional
    public KhoaHoc taoKhoaHocMoi(Long giangVienId, TaoKhoaHocRequest request) {
        HoSoGiangVien giangVien = hoSoGiangVienRepository.findById(giangVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giảng viên #" + giangVienId));

        DanhMuc danhMuc = danhMucRepository.findById(request.getDanhMucId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục môn học #" + request.getDanhMucId()));

        String slug = taoSlugDuyNhat(request.getDuongDanSlug(), request.getTieuDe());

        String defaultThumbnail = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800&auto=format&fit=crop&q=80";
        String anhDaiDien = (request.getAnhDaiDienKhoaHoc() != null && !request.getAnhDaiDienKhoaHoc().isBlank())
                ? request.getAnhDaiDienKhoaHoc().trim()
                : defaultThumbnail;

        KhoaHoc kh = KhoaHoc.builder()
                .tieuDe(request.getTieuDe().trim())
                .duongDanSlug(slug)
                .danhMuc(danhMuc)
                .giangVien(giangVien)
                .thePhanLoai(request.getThePhanLoai())
                .trinhDo(request.getTrinhDo())
                .giaGoc(request.getGiaGoc())
                .giaKhuyenMai(request.getGiaKhuyenMai())
                .anhDaiDienKhoaHoc(anhDaiDien)
                .moTaChiTiet(request.getMoTaChiTiet())
                .trangThai(TrangThaiKhoaHoc.BAN_NHAP)
                .tongSoHocVien(0)
                .diemDanhGiaTrungBinh(BigDecimal.ZERO)
                .tongSoDanhGia(0)
                .build();

        KhoaHoc saved = khoaHocRepository.save(kh);
        log.info("Giảng viên #{} đã tạo khóa học mới: #{} - {}", giangVienId, saved.getId(), saved.getTieuDe());
        return saved;
    }

    @Override
    @Transactional
    public KhoaHoc capNhatKhoaHoc(Long giangVienId, Long khoaHocId, CapNhatKhoaHocRequest request) {
        KhoaHoc kh = khoaHocRepository.findById(khoaHocId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học #" + khoaHocId));

        kiemTraQuyenSoHuu(kh, giangVienId);

        DanhMuc danhMuc = danhMucRepository.findById(request.getDanhMucId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục #" + request.getDanhMucId()));

        kh.setTieuDe(request.getTieuDe().trim());
        kh.setDanhMuc(danhMuc);
        kh.setThePhanLoai(request.getThePhanLoai());
        kh.setTrinhDo(request.getTrinhDo());
        kh.setGiaGoc(request.getGiaGoc());
        kh.setGiaKhuyenMai(request.getGiaKhuyenMai());
        if (request.getAnhDaiDienKhoaHoc() != null && !request.getAnhDaiDienKhoaHoc().isBlank()) {
            kh.setAnhDaiDienKhoaHoc(request.getAnhDaiDienKhoaHoc().trim());
        }
        kh.setMoTaChiTiet(request.getMoTaChiTiet());

        return khoaHocRepository.save(kh);
    }

    @Override
    @Transactional
    public KhoaHoc guiKiemDuyet(Long giangVienId, Long khoaHocId) {
        KhoaHoc kh = khoaHocRepository.findChiTietById(khoaHocId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học #" + khoaHocId));

        kiemTraQuyenSoHuu(kh, giangVienId);

        if (kh.getDanhSachChuongHoc() == null || kh.getDanhSachChuongHoc().isEmpty()) {
            throw new IllegalArgumentException("Khóa học cần có ít nhất 1 chương học trước khi gửi phê duyệt.");
        }

        long totalLessons = baiHocRepository.countByChuongHocKhoaHocId(khoaHocId);
        if (totalLessons == 0) {
            throw new IllegalArgumentException("Khóa học cần có ít nhất 1 bài học video trước khi gửi phê duyệt.");
        }

        kh.setTrangThai(TrangThaiKhoaHoc.CHO_DUYET);
        kh.setLyDoTuChoi(null); // Xóa lý do từ chối cũ nếu có

        KhoaHoc updated = khoaHocRepository.save(kh);
        log.info("Khóa học #{} đã được gửi phê duyệt sang Admin bởi Giảng viên #{}", khoaHocId, giangVienId);
        return updated;
    }

    @Override
    @Transactional
    public void xoaKhoaHoc(Long giangVienId, Long khoaHocId) {
        KhoaHoc kh = khoaHocRepository.findById(khoaHocId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học #" + khoaHocId));

        kiemTraQuyenSoHuu(kh, giangVienId);

        if (kh.getTrangThai() == TrangThaiKhoaHoc.DA_XUAT_BAN && kh.getTongSoHocVien() != null && kh.getTongSoHocVien() > 0) {
            throw new IllegalStateException("Không thể xóa khóa học đã xuất bản và đang có học viên theo học.");
        }

        khoaHocRepository.delete(kh);
        log.info("Đã xóa khóa học #{} của Giảng viên #{}", khoaHocId, giangVienId);
    }

    @Override
    @Transactional
    public ChuongHoc themChuongHoc(Long giangVienId, Long khoaHocId, TaoChuongHocRequest request) {
        KhoaHoc kh = khoaHocRepository.findById(khoaHocId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học #" + khoaHocId));

        kiemTraQuyenSoHuu(kh, giangVienId);

        int thuTu = (request.getThuTuSapXep() != null && request.getThuTuSapXep() > 0)
                ? request.getThuTuSapXep()
                : (kh.getDanhSachChuongHoc() != null ? kh.getDanhSachChuongHoc().size() + 1 : 1);

        ChuongHoc chuong = ChuongHoc.builder()
                .khoaHoc(kh)
                .tieuDeChuong(request.getTieuDeChuong().trim())
                .thuTuSapXep(thuTu)
                .build();

        return chuongHocRepository.save(chuong);
    }

    @Override
    @Transactional
    public void xoaChuongHoc(Long giangVienId, Long chuongHocId) {
        ChuongHoc chuong = chuongHocRepository.findById(chuongHocId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương học #" + chuongHocId));

        kiemTraQuyenSoHuu(chuong.getKhoaHoc(), giangVienId);
        chuongHocRepository.delete(chuong);
    }

    @Override
    @Transactional
    public BaiHoc themBaiHoc(Long giangVienId, Long chuongHocId, TaoBaiHocRequest request) {
        ChuongHoc chuong = chuongHocRepository.findById(chuongHocId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương học #" + chuongHocId));

        kiemTraQuyenSoHuu(chuong.getKhoaHoc(), giangVienId);

        int thuTu = (request.getThuTuSapXep() != null && request.getThuTuSapXep() > 0)
                ? request.getThuTuSapXep()
                : (chuong.getDanhSachBaiHoc() != null ? chuong.getDanhSachBaiHoc().size() + 1 : 1);

        BaiHoc baiHoc = BaiHoc.builder()
                .chuongHoc(chuong)
                .tieuDeBaiHoc(request.getTieuDeBaiHoc().trim())
                .linkVideo(request.getLinkVideo().trim())
                .thoiLuongGiay(request.getThoiLuongGiay() != null ? request.getThoiLuongGiay() : 600)
                .choPhepHocThu(request.getChoPhepHocThu() != null ? request.getChoPhepHocThu() : false)
                .thuTuSapXep(thuTu)
                .build();

        return baiHocRepository.save(baiHoc);
    }

    @Override
    @Transactional
    public void xoaBaiHoc(Long giangVienId, Long baiHocId) {
        BaiHoc baiHoc = baiHocRepository.findById(baiHocId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học #" + baiHocId));

        kiemTraQuyenSoHuu(baiHoc.getChuongHoc().getKhoaHoc(), giangVienId);
        baiHocRepository.delete(baiHoc);
    }

    private void kiemTraQuyenSoHuu(KhoaHoc khoaHoc, Long giangVienId) {
        if (khoaHoc.getGiangVien() == null || !khoaHoc.getGiangVien().getId().equals(giangVienId)) {
            throw new AccessDeniedException("Bạn không có quyền quản lý khóa học này.");
        }
    }

    private String taoSlugDuyNhat(String inputSlug, String tieuDe) {
        String base = (inputSlug != null && !inputSlug.isBlank()) ? toSlug(inputSlug) : toSlug(tieuDe);
        if (base.isBlank()) {
            base = "khoa-hoc-" + UUID.randomUUID().toString().substring(0, 8);
        }

        String candidate = base;
        int count = 1;
        while (khoaHocRepository.existsByDuongDanSlug(candidate)) {
            candidate = base + "-" + (++count);
        }
        return candidate;
    }

    private String toSlug(String input) {
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


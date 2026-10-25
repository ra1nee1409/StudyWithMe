package com.studywithme.service.impl;

import com.studywithme.dto.response.BaiHocDto;
import com.studywithme.dto.response.ChuongHocDto;
import com.studywithme.dto.response.KhoaHocKiemDuyetResponse;
import com.studywithme.dto.response.ThongKeKiemDuyetKhoaHocResponse;
import com.studywithme.entity.BaiHoc;
import com.studywithme.entity.ChuongHoc;
import com.studywithme.entity.KhoaHoc;
import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.KhoaHocRepository;
import com.studywithme.service.AdminKhoaHocService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminKhoaHocServiceImpl implements AdminKhoaHocService {

    private final KhoaHocRepository khoaHocRepository;

    @Override
    public List<KhoaHocKiemDuyetResponse> layDanhSachKhoaHoc(TrangThaiKhoaHoc trangThai) {
        List<KhoaHoc> list = (trangThai != null)
                ? khoaHocRepository.findByTrangThaiOrderByNgayTaoDesc(trangThai)
                : khoaHocRepository.findAllByOrderByNgayTaoDesc();

        return list.stream()
                .map(this::mapToResponseSummary)
                .toList();
    }

    @Override
    public KhoaHocKiemDuyetResponse layChiTietKhoaHoc(Long id) {
        KhoaHoc kh = khoaHocRepository.findChiTietById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));
        return mapToResponseDetail(kh);
    }

    @Override
    @Transactional
    public KhoaHocKiemDuyetResponse pheDuyetKhoaHoc(Long id) {
        KhoaHoc kh = khoaHocRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));

        kh.setTrangThai(TrangThaiKhoaHoc.DA_XUAT_BAN);
        kh.setLyDoTuChoi(null);
        KhoaHoc saved = khoaHocRepository.save(kh);

        return mapToResponseSummary(saved);
    }

    @Override
    @Transactional
    public KhoaHocKiemDuyetResponse tuChoiKhoaHoc(Long id, String lyDo) {
        KhoaHoc kh = khoaHocRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + id));

        kh.setTrangThai(TrangThaiKhoaHoc.TU_CHOI);
        kh.setLyDoTuChoi((lyDo != null && !lyDo.isBlank()) ? lyDo.trim() : "Nội dung chưa đạt yêu cầu kiểm duyệt.");
        KhoaHoc saved = khoaHocRepository.save(kh);

        return mapToResponseSummary(saved);
    }

    @Override
    public ThongKeKiemDuyetKhoaHocResponse layThongKe() {
        long choDuyet = khoaHocRepository.countByTrangThai(TrangThaiKhoaHoc.CHO_DUYET);
        long daXuatBan = khoaHocRepository.countByTrangThai(TrangThaiKhoaHoc.DA_XUAT_BAN);
        long tuChoi = khoaHocRepository.countByTrangThai(TrangThaiKhoaHoc.TU_CHOI);
        long tongCong = khoaHocRepository.count();

        return ThongKeKiemDuyetKhoaHocResponse.builder()
                .choDuyet(choDuyet)
                .daXuatBan(daXuatBan)
                .tuChoi(tuChoi)
                .tongCong(tongCong)
                .build();
    }

    private KhoaHocKiemDuyetResponse mapToResponseSummary(KhoaHoc kh) {
        int soChuong = kh.getDanhSachChuongHoc() != null ? kh.getDanhSachChuongHoc().size() : 0;
        int soBaiHoc = 0;
        int thoiLuong = 0;

        if (kh.getDanhSachChuongHoc() != null) {
            for (ChuongHoc c : kh.getDanhSachChuongHoc()) {
                if (c.getDanhSachBaiHoc() != null) {
                    soBaiHoc += c.getDanhSachBaiHoc().size();
                    for (BaiHoc b : c.getDanhSachBaiHoc()) {
                        thoiLuong += (b.getThoiLuongGiay() != null ? b.getThoiLuongGiay() : 0);
                    }
                }
            }
        }

        return KhoaHocKiemDuyetResponse.builder()
                .id(kh.getId())
                .tieuDe(kh.getTieuDe())
                .duongDanSlug(kh.getDuongDanSlug())
                .moTaChiTiet(kh.getMoTaChiTiet())
                .anhDaiDienKhoaHoc(kh.getAnhDaiDienKhoaHoc())
                .giaGoc(kh.getGiaGoc())
                .giaKhuyenMai(kh.getGiaKhuyenMai())
                .trinhDo(kh.getTrinhDo())
                .thePhanLoai(kh.getThePhanLoai())
                .trangThai(kh.getTrangThai())
                .lyDoTuChoi(kh.getLyDoTuChoi())
                .ngayTao(kh.getNgayTao())
                .ngayCapNhat(kh.getNgayCapNhat())
                .giangVienId(kh.getGiangVien() != null ? kh.getGiangVien().getId() : null)
                .tenGiangVien(kh.getGiangVien() != null && kh.getGiangVien().getNguoiDung() != null ? kh.getGiangVien().getNguoiDung().getHoTen() : "N/A")
                .emailGiangVien(kh.getGiangVien() != null && kh.getGiangVien().getNguoiDung() != null ? kh.getGiangVien().getNguoiDung().getEmail() : "N/A")
                .anhDaiDienGiangVien(kh.getGiangVien() != null && kh.getGiangVien().getNguoiDung() != null ? kh.getGiangVien().getNguoiDung().getAnhDaiDien() : null)
                .chuyenMonGiangVien(kh.getGiangVien() != null ? kh.getGiangVien().getTieuDeChuyenMon() : null)
                .danhMucId(kh.getDanhMuc() != null ? kh.getDanhMuc().getId() : null)
                .tenDanhMuc(kh.getDanhMuc() != null ? kh.getDanhMuc().getTenDanhMuc() : "Chung")
                .tongSoChuong(soChuong)
                .tongSoBaiHoc(soBaiHoc)
                .tongThoiLuongGiay(thoiLuong)
                .danhSachChuongHoc(Collections.emptyList())
                .build();
    }

    private KhoaHocKiemDuyetResponse mapToResponseDetail(KhoaHoc kh) {
        KhoaHocKiemDuyetResponse resp = mapToResponseSummary(kh);

        if (kh.getDanhSachChuongHoc() != null) {
            List<ChuongHocDto> chuongDtos = kh.getDanhSachChuongHoc().stream().map(c -> {
                List<BaiHocDto> baiDtos = (c.getDanhSachBaiHoc() != null)
                        ? c.getDanhSachBaiHoc().stream().map(b -> BaiHocDto.builder()
                                .id(b.getId())
                                .tieuDeBaiHoc(b.getTieuDeBaiHoc())
                                .linkVideo(b.getLinkVideo())
                                .thoiLuongGiay(b.getThoiLuongGiay())
                                .choPhepHocThu(b.getChoPhepHocThu())
                                .thuTuSapXep(b.getThuTuSapXep())
                                .build()).toList()
                        : Collections.<BaiHocDto>emptyList();

                return ChuongHocDto.builder()
                        .id(c.getId())
                        .tieuDeChuong(c.getTieuDeChuong())
                        .thuTuSapXep(c.getThuTuSapXep())
                        .danhSachBaiHoc(baiDtos)
                        .build();
            }).toList();

            resp.setDanhSachChuongHoc(chuongDtos);
        }

        return resp;
    }
}

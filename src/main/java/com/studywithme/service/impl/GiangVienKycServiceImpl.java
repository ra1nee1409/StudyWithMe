package com.studywithme.service.impl;

import com.studywithme.dto.response.ChungChiDto;
import com.studywithme.dto.response.HoSoGiangVienKycResponse;
import com.studywithme.dto.response.ThongKeKycResponse;
import com.studywithme.entity.ChungChiGiangVien;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.admin.TrangThaiGiangVien;
import com.studywithme.entity.enums.admin.TrangThaiXacThuc;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.ChungChiGiangVienRepository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import com.studywithme.service.GiangVienKycService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GiangVienKycServiceImpl implements GiangVienKycService {

    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final ChungChiGiangVienRepository chungChiGiangVienRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;

    public GiangVienKycServiceImpl(HoSoGiangVienRepository hoSoGiangVienRepository,
                                  ChungChiGiangVienRepository chungChiGiangVienRepository,
                                  NguoiDungRepository nguoiDungRepository,
                                  VaiTroRepository vaiTroRepository) {
        this.hoSoGiangVienRepository = hoSoGiangVienRepository;
        this.chungChiGiangVienRepository = chungChiGiangVienRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.vaiTroRepository = vaiTroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoSoGiangVienKycResponse> layDanhSachHoSo(TrangThaiGiangVien trangThai) {
        List<HoSoGiangVien> list;
        if (trangThai == null) {
            list = hoSoGiangVienRepository.findAllByOrderByNgayTaoDesc();
        } else {
            list = hoSoGiangVienRepository.findByTrangThaiDuyetOrderByNgayTaoDesc(trangThai);
        }
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HoSoGiangVienKycResponse layChiTietHoSo(Long id) {
        HoSoGiangVien hoSo = hoSoGiangVienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giảng viên có ID: " + id));
        return mapToResponse(hoSo);
    }

    @Override
    @Transactional
    public HoSoGiangVienKycResponse pheDuyetHoSo(Long id) {
        HoSoGiangVien hoSo = hoSoGiangVienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giảng viên có ID: " + id));

        // 1. Đổi trạng thái hồ sơ sang ĐÃ DUYỆT & xóa lý do từ chối (nếu có)
        hoSo.setTrangThaiDuyet(TrangThaiGiangVien.DA_DUYET);
        hoSo.setLyDoTuChoi(null);

        // 2. Tự động cấp vai trò ROLE_GIANG_VIEN cho người dùng nếu chưa có
        NguoiDung nguoiDung = hoSo.getNguoiDung();
        if (nguoiDung != null) {
            VaiTro roleGiangVien = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                    .orElseGet(() -> vaiTroRepository.save(VaiTro.builder()
                            .tenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                            .moTa("Giảng viên / Gia sư trực tuyến")
                            .build()));

            nguoiDung.addVaiTro(roleGiangVien);
            nguoiDungRepository.save(nguoiDung);
        }

        // 3. Cập nhật các chứng chỉ chờ xác thực sang ĐÃ XÁC THỰC
        if (hoSo.getDanhSachChungChi() != null) {
            for (ChungChiGiangVien cc : hoSo.getDanhSachChungChi()) {
                if (cc.getTrangThaiXacThuc() == TrangThaiXacThuc.CHO_XAC_THUC) {
                    cc.setTrangThaiXacThuc(TrangThaiXacThuc.DA_XAC_THUC);
                }
            }
        }

        HoSoGiangVien savedHoSo = hoSoGiangVienRepository.save(hoSo);
        return mapToResponse(savedHoSo);
    }

    @Override
    @Transactional
    public HoSoGiangVienKycResponse tuChoiHoSo(Long id, String lyDo) {
        HoSoGiangVien hoSo = hoSoGiangVienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giảng viên có ID: " + id));

        hoSo.setTrangThaiDuyet(TrangThaiGiangVien.TU_CHOI);
        hoSo.setLyDoTuChoi(lyDo != null && !lyDo.trim().isEmpty() ? lyDo.trim() : "Hồ sơ hoặc chứng chỉ chưa đáp ứng đủ tiêu chuẩn kiểm duyệt của hệ thống.");

        HoSoGiangVien savedHoSo = hoSoGiangVienRepository.save(hoSo);
        return mapToResponse(savedHoSo);
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeKycResponse layThongKeKyc() {
        long choDuyet = hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.CHO_DUYET);
        long daDuyet = hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.DA_DUYET);
        long tuChoi = hoSoGiangVienRepository.countByTrangThaiDuyet(TrangThaiGiangVien.TU_CHOI);
        long tong = hoSoGiangVienRepository.count();

        return ThongKeKycResponse.builder()
                .choDuyet(choDuyet)
                .daDuyet(daDuyet)
                .tuChoi(tuChoi)
                .tongCong(tong)
                .build();
    }

    private HoSoGiangVienKycResponse mapToResponse(HoSoGiangVien h) {
        NguoiDung u = h.getNguoiDung();
        List<ChungChiDto> chungChis = h.getDanhSachChungChi() == null ? List.of() :
                h.getDanhSachChungChi().stream().map(cc -> ChungChiDto.builder()
                        .id(cc.getId())
                        .loaiChungChi(cc.getLoaiChungChi())
                        .tenChungChi(cc.getTenChungChi())
                        .diemSoHoacXepLoai(cc.getDiemSoHoacXepLoai())
                        .noiCap(cc.getNoiCap())
                        .ngayCap(cc.getNgayCap())
                        .linkAnhChungChi(cc.getLinkAnhChungChi())
                        .trangThaiXacThuc(cc.getTrangThaiXacThuc())
                        .lyDoTuChoi(cc.getLyDoTuChoi())
                        .build()
                ).collect(Collectors.toList());

        return HoSoGiangVienKycResponse.builder()
                .id(h.getId())
                .nguoiDungId(u != null ? u.getId() : null)
                .hoTen(u != null ? u.getHoTen() : null)
                .email(u != null ? u.getEmail() : null)
                .soDienThoai(u != null ? u.getSoDienThoai() : null)
                .anhDaiDien(u != null ? u.getAnhDaiDien() : null)
                .tieuDeChuyenMon(h.getTieuDeChuyenMon())
                .gioiThieuBanThan(h.getGioiThieuBanThan())
                .linkVideoDayThu(h.getLinkVideoDayThu())
                .trangThaiDuyet(h.getTrangThaiDuyet())
                .lyDoTuChoi(h.getLyDoTuChoi())
                .soDuVi(h.getSoDuVi())
                .tenNganHang(h.getTenNganHang())
                .soTaiKhoanNganHang(h.getSoTaiKhoanNganHang())
                .tenChuTaiKhoan(h.getTenChuTaiKhoan())
                .ngayTao(h.getNgayTao())
                .ngayCapNhat(h.getNgayCapNhat())
                .danhSachChungChi(chungChis)
                .build();
    }
}

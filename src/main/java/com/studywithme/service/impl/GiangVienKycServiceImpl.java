package com.studywithme.service.impl;

import com.studywithme.dto.request.DangKyGiangVienRequest;
import com.studywithme.dto.response.ChungChiDto;
import com.studywithme.dto.response.HoSoGiangVienKycResponse;
import com.studywithme.dto.response.ThongKeKycResponse;
import com.studywithme.entity.ChungChiGiangVien;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.giaovien.LoaiChungChi;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @Override
    @Transactional(readOnly = true)
    public Optional<HoSoGiangVien> layHoSoTheoUserEmail(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) return Optional.empty();
        return nguoiDungRepository.findByEmailIgnoreCase(userEmail.trim())
                .flatMap(u -> hoSoGiangVienRepository.findByNguoiDungId(u.getId()));
    }

    @Override
    @Transactional
    public HoSoGiangVien nopHoSoGiangVien(String userEmail, DangKyGiangVienRequest request, MultipartFile fileChungChi) {
        NguoiDung user = nguoiDungRepository.findByEmailIgnoreCase(userEmail.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản: " + userEmail));

        // Kiểm tra xem người dùng đã là Giảng viên chính thức chưa
        boolean daLaGiangVien = user.getDanhSachVaiTro().stream()
                .anyMatch(vt -> vt.getTenVaiTro() == VaiTroName.ROLE_GIANG_VIEN);

        Optional<HoSoGiangVien> hoSoOpt = hoSoGiangVienRepository.findByNguoiDungId(user.getId());

        if (daLaGiangVien && hoSoOpt.isPresent() && hoSoOpt.get().getTrangThaiDuyet() == TrangThaiGiangVien.DA_DUYET) {
            throw new IllegalStateException("Tài khoản của bạn đã được phê duyệt làm Giảng viên chính thức!");
        }

        HoSoGiangVien hoSo;
        if (hoSoOpt.isPresent()) {
            hoSo = hoSoOpt.get();
            if (hoSo.getTrangThaiDuyet() == TrangThaiGiangVien.CHO_DUYET) {
                throw new IllegalStateException("Hồ sơ của bạn đang được Ban Quản Trị xem xét kiểm duyệt. Vui lòng kiên nhẫn chờ đợi!");
            }
            // Nếu bị từ chối trước đó, cập nhật lại để gửi xét duyệt lại
            hoSo.setTieuDeChuyenMon(request.getTieuDeChuyenMon().trim());
            hoSo.setGioiThieuBanThan(request.getGioiThieuBanThan().trim());
            hoSo.setLinkVideoDayThu(request.getLinkVideoDayThu() != null ? request.getLinkVideoDayThu().trim() : null);
            hoSo.setTenNganHang(request.getTenNganHang() != null ? request.getTenNganHang().trim() : null);
            hoSo.setSoTaiKhoanNganHang(request.getSoTaiKhoanNganHang() != null ? request.getSoTaiKhoanNganHang().trim() : null);
            hoSo.setTenChuTaiKhoan(request.getTenChuTaiKhoan() != null ? request.getTenChuTaiKhoan().trim() : null);
            hoSo.setTrangThaiDuyet(TrangThaiGiangVien.CHO_DUYET);
            hoSo.setLyDoTuChoi(null);
        } else {
            hoSo = HoSoGiangVien.builder()
                    .nguoiDung(user)
                    .tieuDeChuyenMon(request.getTieuDeChuyenMon().trim())
                    .gioiThieuBanThan(request.getGioiThieuBanThan().trim())
                    .linkVideoDayThu(request.getLinkVideoDayThu() != null ? request.getLinkVideoDayThu().trim() : null)
                    .tenNganHang(request.getTenNganHang() != null ? request.getTenNganHang().trim() : null)
                    .soTaiKhoanNganHang(request.getSoTaiKhoanNganHang() != null ? request.getSoTaiKhoanNganHang().trim() : null)
                    .tenChuTaiKhoan(request.getTenChuTaiKhoan() != null ? request.getTenChuTaiKhoan().trim() : null)
                    .soDuVi(BigDecimal.ZERO)
                    .trangThaiDuyet(TrangThaiGiangVien.CHO_DUYET)
                    .build();
        }

        // Xử lý tệp bằng cấp / CV upload nếu có
        String finalFileUrl = null;
        if (fileChungChi != null && !fileChungChi.isEmpty()) {
            try {
                Path uploadDir = Paths.get("uploads", "cv");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                String originalFileName = fileChungChi.getOriginalFilename();
                String ext = (originalFileName != null && originalFileName.contains(".")) ?
                        originalFileName.substring(originalFileName.lastIndexOf(".")) : ".pdf";
                String uniqueFileName = UUID.randomUUID().toString() + ext;
                Path targetLocation = uploadDir.resolve(uniqueFileName);
                Files.copy(fileChungChi.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                finalFileUrl = "/uploads/cv/" + uniqueFileName;
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu tệp bằng cấp / CV: " + e.getMessage());
            }
        } else if (request.getLinkAnhChungChi() != null && !request.getLinkAnhChungChi().isBlank()) {
            finalFileUrl = request.getLinkAnhChungChi().trim();
        }

        HoSoGiangVien savedHoSo = hoSoGiangVienRepository.save(hoSo);

        // Lưu thông tin chứng chỉ / bằng cấp vào bảng chung_chi_giang_vien
        if (request.getTenChungChi() != null && !request.getTenChungChi().isBlank()) {
            ChungChiGiangVien cc = ChungChiGiangVien.builder()
                    .giangVien(savedHoSo)
                    .loaiChungChi(request.getLoaiChungChi() != null ? request.getLoaiChungChi() : LoaiChungChi.BANG_DAI_HOC)
                    .tenChungChi(request.getTenChungChi().trim())
                    .noiCap(request.getNoiCap() != null ? request.getNoiCap().trim() : "Trường Đại học / Đơn vị cấp chứng chỉ")
                    .diemSoHoacXepLoai(request.getDiemSoHoacXepLoai() != null ? request.getDiemSoHoacXepLoai().trim() : null)
                    .ngayCap(request.getNgayCap())
                    .linkAnhChungChi(finalFileUrl != null ? finalFileUrl : "/images/certificate-default.png")
                    .trangThaiXacThuc(TrangThaiXacThuc.CHO_XAC_THUC)
                    .build();
            chungChiGiangVienRepository.save(cc);
        }

        return savedHoSo;
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

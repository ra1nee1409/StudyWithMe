package com.studywithme.service.impl;

import com.studywithme.dto.response.ThongKeRutTienResponse;
import com.studywithme.dto.response.YeuCauRutTienResponse;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.LichSuViTien;
import com.studywithme.entity.YeuCauRutTien;
import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import com.studywithme.entity.enums.admin.TrangThaiRutTien;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LichSuViTienRepository;
import com.studywithme.repository.YeuCauRutTienRepository;
import com.studywithme.service.AdminTaiChinhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTaiChinhServiceImpl implements AdminTaiChinhService {

    private final YeuCauRutTienRepository yeuCauRutTienRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final LichSuViTienRepository lichSuViTienRepository;

    @Override
    @Transactional(readOnly = true)
    public List<YeuCauRutTienResponse> layDanhSachYeuCauRutTien(TrangThaiRutTien trangThai) {
        List<YeuCauRutTien> list = (trangThai == null)
                ? yeuCauRutTienRepository.findAllByOrderByNgayTaoDesc()
                : yeuCauRutTienRepository.findByTrangThaiOrderByNgayTaoDesc(trangThai);

        return list.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeRutTienResponse layThongKeRutTien() {
        List<YeuCauRutTien> all = yeuCauRutTienRepository.findAll();

        BigDecimal tongTienChoXuLy = BigDecimal.ZERO;
        long soLenhChoXuLy = 0;
        BigDecimal tongTienDaChuyen = BigDecimal.ZERO;
        long soLenhDaChuyen = 0;
        long soLenhTuChoi = 0;

        for (YeuCauRutTien yc : all) {
            if (yc.getTrangThai() == TrangThaiRutTien.CHO_XU_LY) {
                soLenhChoXuLy++;
                if (yc.getSoTienRut() != null) {
                    tongTienChoXuLy = tongTienChoXuLy.add(yc.getSoTienRut());
                }
            } else if (yc.getTrangThai() == TrangThaiRutTien.DA_CHUYEN_TIEN) {
                soLenhDaChuyen++;
                if (yc.getSoTienRut() != null) {
                    tongTienDaChuyen = tongTienDaChuyen.add(yc.getSoTienRut());
                }
            } else if (yc.getTrangThai() == TrangThaiRutTien.TU_CHOI) {
                soLenhTuChoi++;
            }
        }

        return ThongKeRutTienResponse.builder()
                .tongTienChoXuLy(tongTienChoXuLy)
                .soLenhChoXuLy(soLenhChoXuLy)
                .tongTienDaChuyen(tongTienDaChuyen)
                .soLenhDaChuyen(soLenhDaChuyen)
                .soLenhTuChoi(soLenhTuChoi)
                .tongSoLenh((long) all.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public YeuCauRutTienResponse layChiTietYeuCau(Long id) {
        YeuCauRutTien yc = yeuCauRutTienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu rút tiền với mã ID: " + id));
        return mapToResponse(yc);
    }

    @Override
    @Transactional
    public YeuCauRutTienResponse xacNhanChuyenTien(Long id, String ghiChuAdmin) {
        YeuCauRutTien yc = yeuCauRutTienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu rút tiền với mã ID: " + id));

        if (yc.getTrangThai() != TrangThaiRutTien.CHO_XU_LY) {
            throw new IllegalStateException("Lệnh rút tiền này đã được xử lý trước đó, không thể xác nhận lại.");
        }

        yc.setTrangThai(TrangThaiRutTien.DA_CHUYEN_TIEN);
        yc.setNgayXuLy(LocalDateTime.now());
        yc.setGhiChuAdmin(ghiChuAdmin != null && !ghiChuAdmin.isBlank() ? ghiChuAdmin : "Đã hoàn tất chuyển khoản thành công.");
        yeuCauRutTienRepository.save(yc);

        // Ghi nhận sổ cái ví tiền (Ledger)
        HoSoGiangVien gv = yc.getGiangVien();
        if (gv != null) {
            BigDecimal soDuHienTai = gv.getSoDuVi() != null ? gv.getSoDuVi() : BigDecimal.ZERO;
            LichSuViTien lichSu = LichSuViTien.builder()
                    .giangVien(gv)
                    .soTienBienDong(yc.getSoTienRut())
                    .soDuTruoc(soDuHienTai)
                    .soDuSau(soDuHienTai) // Số dư đã bị giữ từ khi tạo lệnh rút
                    .loaiGiaoDich(LoaiGiaoDichVi.RUT_TIEN_VE_NGAN_HANG)
                    .maThamChieu(yc.getId())
                    .moTaGiaoDich("Giải ngân thành công lệnh rút tiền #" + yc.getId() + " về tài khoản " + yc.getTenNganHang() + " - " + yc.getSoTaiKhoan())
                    .build();
            lichSuViTienRepository.save(lichSu);
        }

        return mapToResponse(yc);
    }

    @Override
    @Transactional
    public YeuCauRutTienResponse tuChoiRutTien(Long id, String lyDoTuChoi) {
        YeuCauRutTien yc = yeuCauRutTienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu rút tiền với mã ID: " + id));

        if (yc.getTrangThai() != TrangThaiRutTien.CHO_XU_LY) {
            throw new IllegalStateException("Lệnh rút tiền này đã được xử lý trước đó, không thể từ chối lại.");
        }

        if (lyDoTuChoi == null || lyDoTuChoi.isBlank()) {
            throw new IllegalArgumentException("Vui lòng cung cấp lý do từ chối để thông báo cho giảng viên.");
        }

        yc.setTrangThai(TrangThaiRutTien.TU_CHOI);
        yc.setNgayXuLy(LocalDateTime.now());
        yc.setGhiChuAdmin(lyDoTuChoi);
        yeuCauRutTienRepository.save(yc);

        // Hoàn tiền lại vào ví của giảng viên
        HoSoGiangVien gv = yc.getGiangVien();
        if (gv != null) {
            BigDecimal soDuTruoc = gv.getSoDuVi() != null ? gv.getSoDuVi() : BigDecimal.ZERO;
            BigDecimal soTienHoan = yc.getSoTienRut() != null ? yc.getSoTienRut() : BigDecimal.ZERO;
            BigDecimal soDuSau = soDuTruoc.add(soTienHoan);

            gv.setSoDuVi(soDuSau);
            hoSoGiangVienRepository.save(gv);

            // Ghi nhận biến động hoàn tiền vào sổ cái
            LichSuViTien lichSu = LichSuViTien.builder()
                    .giangVien(gv)
                    .soTienBienDong(soTienHoan)
                    .soDuTruoc(soDuTruoc)
                    .soDuSau(soDuSau)
                    .loaiGiaoDich(LoaiGiaoDichVi.HOAN_TIEN)
                    .maThamChieu(yc.getId())
                    .moTaGiaoDich("Hoàn tiền lệnh rút #" + yc.getId() + " bị từ chối: " + lyDoTuChoi)
                    .build();
            lichSuViTienRepository.save(lichSu);
        }

        return mapToResponse(yc);
    }

    private YeuCauRutTienResponse mapToResponse(YeuCauRutTien yc) {
        HoSoGiangVien gv = yc.getGiangVien();
        String tenGv = "Chưa cập nhật";
        String emailGv = "";
        String sdtGv = "";
        String avatarGv = "";
        BigDecimal soDuVi = BigDecimal.ZERO;

        if (gv != null) {
            soDuVi = gv.getSoDuVi() != null ? gv.getSoDuVi() : BigDecimal.ZERO;
            if (gv.getNguoiDung() != null) {
                tenGv = gv.getNguoiDung().getHoTen();
                emailGv = gv.getNguoiDung().getEmail();
                sdtGv = gv.getNguoiDung().getSoDienThoai();
                avatarGv = gv.getNguoiDung().getAnhDaiDien();
            }
        }

        return YeuCauRutTienResponse.builder()
                .id(yc.getId())
                .giangVienId(gv != null ? gv.getId() : null)
                .tenGiangVien(tenGv)
                .emailGiangVien(emailGv)
                .soDienThoai(sdtGv)
                .anhDaiDien(avatarGv)
                .soTienRut(yc.getSoTienRut())
                .soDuViHienTai(soDuVi)
                .tenNganHang(yc.getTenNganHang())
                .soTaiKhoan(yc.getSoTaiKhoan())
                .tenChuTaiKhoan(yc.getTenChuTaiKhoan())
                .trangThai(yc.getTrangThai())
                .ghiChuAdmin(yc.getGhiChuAdmin())
                .ngayTao(yc.getNgayTao())
                .ngayXuLy(yc.getNgayXuLy())
                .build();
    }
}

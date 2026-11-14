package com.studywithme.service.impl;

import com.studywithme.dto.tutor.CapNhatLinkMeetRequest;
import com.studywithme.dto.tutor.DatCaHoc1k1Request;
import com.studywithme.dto.tutor.DatLich1k1Dto;
import com.studywithme.dto.tutor.XacNhanCaHocRequest;
import com.studywithme.entity.DanhMuc;
import com.studywithme.entity.DatLich1k1;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.LichDayGiaSu;
import com.studywithme.entity.LichSuViTien;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.admin.LoaiGiaoDichVi;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.DanhMucRepository;
import com.studywithme.repository.DatLich1k1Repository;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LichDayGiaSuRepository;
import com.studywithme.repository.LichSuViTienRepository;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.service.CauHinhHeThongService;
import com.studywithme.service.DatLich1k1Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatLich1k1ServiceImpl implements DatLich1k1Service {

    private final DatLich1k1Repository datLich1k1Repository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DanhMucRepository danhMucRepository;
    private final LichDayGiaSuRepository lichDayGiaSuRepository;
    private final CauHinhHeThongService cauHinhHeThongService;
    private final LichSuViTienRepository lichSuViTienRepository;
    private final PayOS payOS;

    @Override
    @Transactional
    public DatLich1k1 taoYeuCauDatLich(Long hocVienId, DatCaHoc1k1Request request) {
        NguoiDung hocVien = nguoiDungRepository.findById(hocVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy học viên ID: " + hocVienId));

        HoSoGiangVien giangVien = hoSoGiangVienRepository.findById(request.getGiangVienId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gia sư ID: " + request.getGiangVienId()));

        if (giangVien.getNguoiDung() != null && giangVien.getNguoiDung().getId().equals(hocVienId)) {
            throw new IllegalArgumentException("Gia sư không thể tự đặt lịch kèm học cho chính mình!");
        }

        if (request.getThoiGianBatDau() == null || request.getThoiGianKetThuc() == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu và kết thúc ca học không được để trống!");
        }

        if (!request.getThoiGianBatDau().isBefore(request.getThoiGianKetThuc())) {
            throw new IllegalArgumentException("Thời gian bắt đầu phải sớm hơn thời gian kết thúc!");
        }

        if (request.getThoiGianBatDau().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Không thể đặt ca học ở thời điểm trong quá khứ!");
        }

        // Kiểm tra tránh trùng ca học đã có của gia sư
        boolean isConflicting = datLich1k1Repository.existsByGiangVienIdAndThoiGianBatDauLessThanAndThoiGianKetThucGreaterThanAndTrangThaiNot(
                giangVien.getId(),
                request.getThoiGianKetThuc(),
                request.getThoiGianBatDau(),
                TrangThaiDatLich.DA_HUY
        );

        if (isConflicting) {
            throw new IllegalArgumentException("Gia sư đã có ca học khác trong khung giờ này. Vui lòng chọn thời gian khác!");
        }

        BigDecimal hocPhi = request.getHocPhiCaHoc() != null ? request.getHocPhiCaHoc() : BigDecimal.ZERO;
        if (hocPhi.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Học phí ca học không thể âm!");
        }

        BigDecimal tyLeSan = cauHinhHeThongService.layTyLeHoaHongSan();
        BigDecimal heSoSan = cauHinhHeThongService.layHeSoHoaHongSan();
        BigDecimal phiSanKhauTru = hocPhi.multiply(heSoSan).setScale(2, RoundingMode.HALF_UP);
        BigDecimal thucNhanGiaSu = hocPhi.subtract(phiSanKhauTru);

        DanhMuc monHoc = null;
        if (request.getMonHocId() != null) {
            monHoc = danhMucRepository.findById(request.getMonHocId()).orElse(null);
        }

        LichDayGiaSu lichDay = null;
        if (request.getLichDayId() != null) {
            lichDay = lichDayGiaSuRepository.findById(request.getLichDayId()).orElse(null);
        }

        String maDatLich = "DL1K1-" + System.currentTimeMillis();

        DatLich1k1 datLich = DatLich1k1.builder()
                .maDatLich(maDatLich)
                .hocVien(hocVien)
                .giangVien(giangVien)
                .monHoc(monHoc)
                .lichDayGiaSu(lichDay)
                .thoiGianBatDau(request.getThoiGianBatDau())
                .thoiGianKetThuc(request.getThoiGianKetThuc())
                .hocPhiCaHoc(hocPhi)
                .tyLeHoaHongSan(tyLeSan)
                .phiSanKhauTru(phiSanKhauTru)
                .thucNhanGiaSu(thucNhanGiaSu)
                .trangThai(TrangThaiDatLich.CHO_XAC_NHAN)
                .ghiChuHocVien(request.getGhiChuHocVien())
                .build();

        DatLich1k1 saved = datLich1k1Repository.save(datLich);
        log.info("Hoc vien ID {} da dat ca hoc 1-1 ma {} voi gia su ID {}", hocVienId, maDatLich, giangVien.getId());
        return saved;
    }

    @Override
    @Transactional
    public DatLich1k1 giaSuPhanHoiCaHoc(Long giangVienId, Long datLichId, XacNhanCaHocRequest request) {
        DatLich1k1 caHoc = datLich1k1Repository.findById(datLichId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + datLichId));

        if (!caHoc.getGiangVien().getId().equals(giangVienId)) {
            throw new IllegalArgumentException("Bạn không có quyền điều phối ca học này!");
        }

        if (caHoc.getTrangThai() != TrangThaiDatLich.CHO_XAC_NHAN) {
            throw new IllegalArgumentException("Ca học này hiện không ở trạng thái chờ xác nhận (hiện tại: " + caHoc.getTrangThai().getMoTa() + ")!");
        }

        if (request.isChapNhan()) {
            caHoc.setTrangThai(TrangThaiDatLich.CHO_THANH_TOAN);
            log.info("Gia su ID {} da CHAP NHAN ca hoc 1-1 ma {}", giangVienId, caHoc.getMaDatLich());
        } else {
            caHoc.setTrangThai(TrangThaiDatLich.DA_HUY);
            caHoc.setLyDoHuy(request.getLyDoTuChoi() != null ? request.getLyDoTuChoi() : "Gia sư bận khung giờ này");
            log.info("Gia su ID {} da TU CHOI ca hoc 1-1 ma {}", giangVienId, caHoc.getMaDatLich());
        }

        return datLich1k1Repository.save(caHoc);
    }

    @Override
    @Transactional
    public DatLich1k1 capNhatLinkMeet(Long giangVienId, Long datLichId, CapNhatLinkMeetRequest request) {
        DatLich1k1 caHoc = datLich1k1Repository.findById(datLichId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + datLichId));

        if (!caHoc.getGiangVien().getId().equals(giangVienId)) {
            throw new IllegalArgumentException("Bạn không có quyền cập nhật link phòng học của ca học này!");
        }

        caHoc.setLinkPhongHoc(request.getLinkPhongHoc());
        return datLich1k1Repository.save(caHoc);
    }

    @Override
    @Transactional
    public DatLich1k1 xacNhanThanhToanKyQuy(Long datLichId) {
        DatLich1k1 caHoc = datLich1k1Repository.findById(datLichId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + datLichId));

        caHoc.setTrangThai(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY);
        log.info("Ca hoc 1-1 ma {} da duoc hoc vien thanh toan va SAN DANG KY QUY GIU TIEN", caHoc.getMaDatLich());
        return datLich1k1Repository.save(caHoc);
    }

    @Override
    @Transactional
    public String taoLinkThanhToanPayOs(Long hocVienId, Long datLichId, String baseUrl) {
        DatLich1k1 caHoc = datLich1k1Repository.findById(datLichId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + datLichId));

        if (!caHoc.getHocVien().getId().equals(hocVienId)) {
            throw new IllegalArgumentException("Bạn không có quyền thanh toán cho ca học này!");
        }

        if (caHoc.getTrangThai() != TrangThaiDatLich.CHO_THANH_TOAN && caHoc.getTrangThai() != TrangThaiDatLich.CHO_XAC_NHAN) {
            throw new IllegalArgumentException("Ca học này không ở trạng thái cần thanh toán (hiện tại: " + caHoc.getTrangThai().getMoTa() + ")!");
        }

        long orderCode = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(3));
        caHoc.setOrderCodePayos(orderCode);
        datLich1k1Repository.save(caHoc);

        long amount = caHoc.getHocPhiCaHoc().longValue();
        String description = "KY QUY CA " + caHoc.getId();
        if (description.length() > 25) {
            description = description.substring(0, 25);
        }

        String returnUrl = baseUrl + "/payment/payos-callback?type=1k1&id=" + caHoc.getId();
        String cancelUrl = baseUrl + "/student/ca-hoc?cancel=true";

        List<PaymentLinkItem> items = List.of(
                PaymentLinkItem.builder()
                        .name("Ca hoc 1-1")
                        .quantity(1)
                        .price(amount)
                        .build()
        );

        CreatePaymentLinkRequest paymentLinkRequest = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .items(items)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

        try {
            CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentLinkRequest);
            return response != null ? response.getCheckoutUrl() : (baseUrl + "/payment/payos-callback?type=1k1&id=" + caHoc.getId());
        } catch (Exception e) {
            log.warn("PayOS createPaymentLink không kết nối được gateway thật, chuyển chế độ fallback: {}", e.getMessage());
            return baseUrl + "/payment/payos-callback?type=1k1&id=" + caHoc.getId() + "&orderCode=" + orderCode + "&status=PAID";
        }
    }

    @Override
    @Transactional
    public DatLich1k1 xacNhanThanhToanKyQuyNgay(Long hocVienId, Long datLichId) {
        DatLich1k1 caHoc = datLich1k1Repository.findById(datLichId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + datLichId));

        if (!caHoc.getHocVien().getId().equals(hocVienId)) {
            throw new IllegalArgumentException("Bạn không có quyền thực hiện thao tác này!");
        }

        caHoc.setTrangThai(TrangThaiDatLich.DA_THANH_TOAN_KY_QUY);
        log.info("Hoc vien ID {} da thanh toan ky quy thanh cong ca hoc 1-1 ma {}", hocVienId, caHoc.getMaDatLich());
        return datLich1k1Repository.save(caHoc);
    }

    @Override
    @Transactional
    public DatLich1k1 danhGiaCaHoc(Long hocVienId, Long datLichId, int soSao, String nhanXet) {
        DatLich1k1 caHoc = datLich1k1Repository.findById(datLichId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + datLichId));

        if (!caHoc.getHocVien().getId().equals(hocVienId)) {
            throw new IllegalArgumentException("Bạn không có quyền đánh giá ca học này!");
        }

        if (caHoc.getTrangThai() != TrangThaiDatLich.HOAN_THANH) {
            throw new IllegalArgumentException("Chỉ có thể đánh giá ca học đã hoàn tất!");
        }

        caHoc.setDanhGiaSao(Math.max(1, Math.min(5, soSao)));
        caHoc.setNhanXetHocVien(nhanXet != null ? nhanXet.trim() : "");
        log.info("Hoc vien ID {} da danh gia {} sao cho ca hoc 1-1 ma {}", hocVienId, soSao, caHoc.getMaDatLich());
        return datLich1k1Repository.save(caHoc);
    }

    @Override
    @Transactional
    public DatLich1k1 hoanTatCaHoc(Long giangVienId, Long datLichId) {
        DatLich1k1 caHoc = datLich1k1Repository.findById(datLichId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + datLichId));

        if (!caHoc.getGiangVien().getId().equals(giangVienId)) {
            throw new IllegalArgumentException("Bạn không có quyền xác nhận hoàn tất ca học này!");
        }

        if (caHoc.getTrangThai() != TrangThaiDatLich.DA_THANH_TOAN_KY_QUY && caHoc.getTrangThai() != TrangThaiDatLich.DANG_DIEN_RA) {
            throw new IllegalArgumentException("Chỉ có thể hoàn tất ca học đã được thanh toán ký quỹ!");
        }

        caHoc.setTrangThai(TrangThaiDatLich.HOAN_THANH);

        // Giải phóng học phí ký quỹ vào ví gia sư
        HoSoGiangVien gv = caHoc.getGiangVien();
        BigDecimal soDuTruoc = gv.getSoDuVi() != null ? gv.getSoDuVi() : BigDecimal.ZERO;
        BigDecimal thucNhan = caHoc.getThucNhanGiaSu();
        BigDecimal soDuSau = soDuTruoc.add(thucNhan);

        gv.setSoDuVi(soDuSau);
        hoSoGiangVienRepository.save(gv);

        LichSuViTien ls = LichSuViTien.builder()
                .giangVien(gv)
                .soTienBienDong(thucNhan)
                .soDuTruoc(soDuTruoc)
                .soDuSau(soDuSau)
                .loaiGiaoDich(LoaiGiaoDichVi.TIEN_DAY_KEM_1K1)
                .maThamChieu(caHoc.getId())
                .moTaGiaoDich("Học phí ca dạy 1-1 mã " + caHoc.getMaDatLich() + " (Đã khấu trừ " + caHoc.getTyLeHoaHongSan() + "% phí sàn)")
                .build();
        lichSuViTienRepository.save(ls);

        log.info("Hoan tat ca hoc 1-1 ma {}. Da giai phong {} VND ve vi gia su ID {}", caHoc.getMaDatLich(), thucNhan, giangVienId);
        return datLich1k1Repository.save(caHoc);
    }

    @Override
    @Transactional
    public DatLich1k1 hocVienHuyCaHoc(Long hocVienId, Long datLichId, String lyDoHuy) {
        DatLich1k1 caHoc = datLich1k1Repository.findById(datLichId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + datLichId));

        if (!caHoc.getHocVien().getId().equals(hocVienId)) {
            throw new IllegalArgumentException("Bạn không phải người đặt ca học này!");
        }

        if (caHoc.getTrangThai() != TrangThaiDatLich.CHO_XAC_NHAN && caHoc.getTrangThai() != TrangThaiDatLich.CHO_THANH_TOAN) {
            throw new IllegalArgumentException("Chỉ có thể hủy ca học khi đang ở trạng thái chờ xác nhận hoặc chờ thanh toán!");
        }

        caHoc.setTrangThai(TrangThaiDatLich.DA_HUY);
        caHoc.setLyDoHuy(lyDoHuy != null && !lyDoHuy.isBlank() ? lyDoHuy : "Học viên hủy yêu cầu");
        log.info("Hoc vien ID {} da huy ca hoc 1-1 ma {}", hocVienId, caHoc.getMaDatLich());
        return datLich1k1Repository.save(caHoc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatLich1k1Dto> layDanhSachCaHocCuaGiaSu(Long giangVienId, TrangThaiDatLich trangThai) {
        List<DatLich1k1> list = datLich1k1Repository.findByGiangVienIdOrderByThoiGianBatDauDesc(giangVienId);
        if (trangThai != null) {
            list = list.stream().filter(c -> c.getTrangThai() == trangThai).toList();
        }
        return list.stream().map(DatLich1k1Dto::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatLich1k1Dto> layDanhSachCaHocCuaHocVien(Long hocVienId) {
        return datLich1k1Repository.findByHocVienIdOrderByThoiGianBatDauDesc(hocVienId)
                .stream()
                .map(DatLich1k1Dto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DatLich1k1Dto layChiTietCaHoc(Long id) {
        return datLich1k1Repository.findById(id)
                .map(DatLich1k1Dto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca học ID: " + id));
    }
}

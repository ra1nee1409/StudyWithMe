package com.studywithme.service.impl;

import com.studywithme.dto.tutor.LichDaySlotDto;
import com.studywithme.dto.tutor.LichDaySlotRequest;
import com.studywithme.entity.HoSoGiangVien;
import com.studywithme.entity.LichDayGiaSu;
import com.studywithme.entity.enums.giaovien.LoaiLichDay;
import com.studywithme.entity.enums.giaovien.ThuTrongTuan;
import com.studywithme.entity.enums.giaovien.TrangThaiLichDay;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.HoSoGiangVienRepository;
import com.studywithme.repository.LichDayGiaSuRepository;
import com.studywithme.service.LichDayGiaSuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LichDayGiaSuServiceImpl implements LichDayGiaSuService {

    private final LichDayGiaSuRepository lichDayGiaSuRepository;
    private final HoSoGiangVienRepository hoSoGiangVienRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LichDaySlotDto> layDanhSachLichDay(Long giangVienId) {
        return lichDayGiaSuRepository.findByGiangVienIdOrderByGioBatDauAsc(giangVienId)
                .stream()
                .map(LichDaySlotDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LichDaySlotDto> layDanhSachLichDayTheoThu(Long giangVienId, ThuTrongTuan thuTrongTuan) {
        return lichDayGiaSuRepository.findByGiangVienIdAndThuTrongTuanOrderByGioBatDauAsc(giangVienId, thuTrongTuan)
                .stream()
                .map(LichDaySlotDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LichDaySlotDto> layDanhSachLichDayKhaDung(Long giangVienId) {
        return lichDayGiaSuRepository.findByGiangVienIdAndTrangThaiOrderByGioBatDauAsc(giangVienId, TrangThaiLichDay.CON_TRONG)
                .stream()
                .map(LichDaySlotDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public LichDayGiaSu taoHoacCapNhatSlot(Long giangVienId, LichDaySlotRequest request) {
        HoSoGiangVien giangVien = hoSoGiangVienRepository.findById(giangVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ giảng viên ID: " + giangVienId));

        if (request.getGioBatDau() == null || request.getGioKetThuc() == null) {
            throw new IllegalArgumentException("Giờ bắt đầu và kết thúc không được để trống!");
        }

        if (!request.getGioBatDau().isBefore(request.getGioKetThuc())) {
            throw new IllegalArgumentException("Giờ bắt đầu (" + request.getGioBatDau() + ") phải sớm hơn giờ kết thúc (" + request.getGioKetThuc() + ")!");
        }

        // Kiểm tra không chồng chéo khung giờ với các slot khác cùng thứ trong tuần
        List<LichDayGiaSu> existingSlots = lichDayGiaSuRepository.findByGiangVienIdAndThuTrongTuanOrderByGioBatDauAsc(giangVienId, request.getThuTrongTuan());
        for (LichDayGiaSu slot : existingSlots) {
            if (request.getId() != null && slot.getId().equals(request.getId())) {
                continue;
            }
            boolean isNonOverlapping = request.getGioKetThuc().compareTo(slot.getGioBatDau()) <= 0
                    || request.getGioBatDau().compareTo(slot.getGioKetThuc()) >= 0;
            if (!isNonOverlapping) {
                throw new IllegalArgumentException("Khung giờ này bị trùng lặp với slot rảnh đã có: " 
                        + slot.getGioBatDau() + " - " + slot.getGioKetThuc() + " (" + request.getThuTrongTuan().getMoTa() + ")");
            }
        }

        LichDayGiaSu slotEntity;
        if (request.getId() != null) {
            slotEntity = lichDayGiaSuRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ ID: " + request.getId()));
            if (!slotEntity.getGiangVien().getId().equals(giangVienId)) {
                throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa khung giờ này!");
            }
        } else {
            slotEntity = LichDayGiaSu.builder()
                    .giangVien(giangVien)
                    .trangThai(TrangThaiLichDay.CON_TRONG)
                    .build();
        }

        slotEntity.setThuTrongTuan(request.getThuTrongTuan());
        slotEntity.setGioBatDau(request.getGioBatDau());
        slotEntity.setGioKetThuc(request.getGioKetThuc());
        slotEntity.setLoaiLich(request.getLoaiLich() != null ? request.getLoaiLich() : LoaiLichDay.CO_DINH);
        slotEntity.setHocPhiMoiGio(request.getHocPhiMoiGio() != null ? request.getHocPhiMoiGio() : BigDecimal.valueOf(150000));
        slotEntity.setGhiChu(request.getGhiChu());

        return lichDayGiaSuRepository.save(slotEntity);
    }

    @Override
    @Transactional
    public void xoaSlot(Long giangVienId, Long slotId) {
        LichDayGiaSu slot = lichDayGiaSuRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ ID: " + slotId));

        if (!slot.getGiangVien().getId().equals(giangVienId)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa khung giờ này!");
        }

        lichDayGiaSuRepository.delete(slot);
        log.info("Gia su ID {} da xoa slot lich day ID {}", giangVienId, slotId);
    }

    @Override
    @Transactional
    public LichDayGiaSu toggleTrangThaiSlot(Long giangVienId, Long slotId) {
        LichDayGiaSu slot = lichDayGiaSuRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ ID: " + slotId));

        if (!slot.getGiangVien().getId().equals(giangVienId)) {
            throw new IllegalArgumentException("Bạn không có quyền thay đổi trạng thái khung giờ này!");
        }

        if (slot.getTrangThai() == TrangThaiLichDay.CON_TRONG) {
            slot.setTrangThai(TrangThaiLichDay.TAM_KHOA);
        } else if (slot.getTrangThai() == TrangThaiLichDay.TAM_KHOA) {
            slot.setTrangThai(TrangThaiLichDay.CON_TRONG);
        }
        return lichDayGiaSuRepository.save(slot);
    }
}

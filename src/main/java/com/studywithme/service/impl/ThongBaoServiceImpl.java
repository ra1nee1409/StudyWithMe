package com.studywithme.service.impl;

import com.studywithme.dto.notification.ThongBaoDto;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.ThongBao;
import com.studywithme.entity.enums.shared.LoaiThongBao;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.ThongBaoRepository;
import com.studywithme.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThongBaoServiceImpl implements ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ThongBaoDto taoThongBao(Long nguoiDungId, String tieuDe, String noiDung, LoaiThongBao loai, String url) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + nguoiDungId));

        ThongBao thongBao = ThongBao.builder()
                .nguoiDung(nguoiDung)
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .loaiThongBao(loai != null ? loai : LoaiThongBao.HE_THONG)
                .duongDanLienKet(url)
                .daDoc(false)
                .build();

        thongBao = thongBaoRepository.save(thongBao);
        ThongBaoDto dto = mapToDto(thongBao);

        // Bắn WebSocket Realtime tới người dùng tương ứng (0s delay)
        try {
            messagingTemplate.convertAndSend("/topic/users/" + nguoiDungId + "/notifications", (Object) dto);
        } catch (Exception e) {
            log.warn("Lỗi khi bắn WebSocket thông báo cho user {}: {}", nguoiDungId, e.getMessage());
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThongBaoDto> layDanhSachThongBao(Long nguoiDungId, int limit) {
        int pageSize = limit > 0 ? limit : 10;
        List<ThongBao> list = thongBaoRepository.findByNguoiDungIdOrderByThoiGianTaoDesc(
                nguoiDungId, PageRequest.of(0, pageSize));
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long demSoChuaDoc(Long nguoiDungId) {
        return thongBaoRepository.countByNguoiDungIdAndDaDocFalse(nguoiDungId);
    }

    @Override
    @Transactional
    public boolean danhDauDaDoc(Long thongBaoId, Long nguoiDungId) {
        return thongBaoRepository.findByIdAndNguoiDungId(thongBaoId, nguoiDungId)
                .map(tb -> {
                    tb.setDaDoc(true);
                    thongBaoRepository.save(tb);
                    return true;
                }).orElse(false);
    }

    @Override
    @Transactional
    public int danhDauTatCaDaDoc(Long nguoiDungId) {
        return thongBaoRepository.danhDauTatCaDaDoc(nguoiDungId);
    }

    private ThongBaoDto mapToDto(ThongBao entity) {
        return ThongBaoDto.builder()
                .id(entity.getId())
                .tieuDe(entity.getTieuDe())
                .noiDung(entity.getNoiDung())
                .loaiThongBao(entity.getLoaiThongBao())
                .duongDanLienKet(entity.getDuongDanLienKet())
                .daDoc(entity.getDaDoc())
                .thoiGianTao(entity.getThoiGianTao())
                .thoiGianTaoFormatted(formatTimeAgo(entity.getThoiGianTao()))
                .build();
    }

    private String formatTimeAgo(LocalDateTime time) {
        if (time == null) return "Vừa xong";
        Duration duration = Duration.between(time, LocalDateTime.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Vừa xong";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " phút trước";
        long hours = minutes / 60;
        if (hours < 24) return hours + " giờ trước";
        long days = hours / 24;
        if (days < 30) return days + " ngày trước";
        return time.getDayOfMonth() + "/" + time.getMonthValue() + "/" + time.getYear();
    }
}

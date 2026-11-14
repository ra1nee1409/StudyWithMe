package com.studywithme.service.impl;

import com.studywithme.chat.mongo.CuocTroChuyenMongo;
import com.studywithme.chat.mongo.TinNhanMongo;
import com.studywithme.dto.chat.CuocTroChuyenDto;
import com.studywithme.dto.chat.GuiTinNhanRequest;
import com.studywithme.dto.chat.TinNhanDto;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.shared.LoaiTinNhan;
import com.studywithme.exception.ResourceNotFoundException;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.mongo.CuocTroChuyenMongoRepository;
import com.studywithme.repository.mongo.TinNhanMongoRepository;
import com.studywithme.service.ChatMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMongoServiceImpl implements ChatMongoService {

    private final CuocTroChuyenMongoRepository cuocTroChuyenMongoRepository;
    private final TinNhanMongoRepository tinNhanMongoRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public TinNhanDto guiTinNhan(Long nguoiGuiId, GuiTinNhanRequest request) {
        if (nguoiGuiId.equals(request.getNguoiNhanId())) {
            throw new IllegalArgumentException("Không thể gửi tin nhắn cho chính mình.");
        }

        NguoiDung nguoiGui = nguoiDungRepository.findById(nguoiGuiId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người gửi với ID: " + nguoiGuiId));

        NguoiDung nguoiNhan = nguoiDungRepository.findById(request.getNguoiNhanId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người nhận với ID: " + request.getNguoiNhanId()));

        // 1. Tìm hoặc tạo cuộc trò chuyện
        CuocTroChuyenMongo cuocTroChuyen = cuocTroChuyenMongoRepository
                .findConversationBetweenUsers(nguoiGuiId, request.getNguoiNhanId())
                .orElseGet(() -> {
                    CuocTroChuyenMongo newConv = CuocTroChuyenMongo.builder()
                            .nguoiDung1Id(nguoiGuiId)
                            .tenNguoiDung1(nguoiGui.getHoTen())
                            .avatarNguoiDung1(nguoiGui.getAnhDaiDien())
                            .nguoiDung2Id(request.getNguoiNhanId())
                            .tenNguoiDung2(nguoiNhan.getHoTen())
                            .avatarNguoiDung2(nguoiNhan.getAnhDaiDien())
                            .ngayTao(LocalDateTime.now())
                            .build();
                    return cuocTroChuyenMongoRepository.save(newConv);
                });

        // 2. Tạo tin nhắn mới trên MongoDB
        String previewContent = (request.getLoaiTinNhan() == LoaiTinNhan.DE_XUAT_CA_HOC)
                ? "[Đề xuất ca học 1-1]"
                : (request.getNoiDung() != null ? request.getNoiDung().trim() : "");

        TinNhanMongo tinNhan = TinNhanMongo.builder()
                .cuocTroChuyenId(cuocTroChuyen.getId())
                .nguoiGuiId(nguoiGuiId)
                .tenNguoiGui(nguoiGui.getHoTen())
                .avatarNguoiGui(nguoiGui.getAnhDaiDien())
                .nguoiNhanId(request.getNguoiNhanId())
                .noiDung(previewContent)
                .loaiTinNhan(request.getLoaiTinNhan() != null ? request.getLoaiTinNhan() : LoaiTinNhan.VAN_BAN)
                .duLieuDinhKem(request.getDuLieuDinhKem())
                .daDoc(false)
                .thoiGianGui(LocalDateTime.now())
                .build();

        TinNhanMongo savedMessage = tinNhanMongoRepository.save(tinNhan);

        // 3. Cập nhật preview cuộc trò chuyện
        cuocTroChuyen.setTinNhanCuoi(previewContent);
        cuocTroChuyen.setThoiGianTinNhanCuoi(savedMessage.getThoiGianGui());
        cuocTroChuyen.setNgayCapNhat(LocalDateTime.now());

        if (Objects.equals(cuocTroChuyen.getNguoiDung1Id(), request.getNguoiNhanId())) {
            cuocTroChuyen.setSoTinChuaDocUser1(cuocTroChuyen.getSoTinChuaDocUser1() + 1);
        } else {
            cuocTroChuyen.setSoTinChuaDocUser2(cuocTroChuyen.getSoTinChuaDocUser2() + 1);
        }
        cuocTroChuyenMongoRepository.save(cuocTroChuyen);

        return mapToTinNhanDto(savedMessage, nguoiGuiId);
    }

    @Override
    public List<CuocTroChuyenDto> layDanhSachCuocTroChuyen(Long currentUserId) {
        List<CuocTroChuyenMongo> list = cuocTroChuyenMongoRepository.findAllByUserId(currentUserId);
        List<CuocTroChuyenDto> dtos = new ArrayList<>();

        for (CuocTroChuyenMongo c : list) {
            boolean isUser1 = Objects.equals(c.getNguoiDung1Id(), currentUserId);
            Long doiPhuongId = isUser1 ? c.getNguoiDung2Id() : c.getNguoiDung1Id();
            String tenDoiPhuong = isUser1 ? c.getTenNguoiDung2() : c.getTenNguoiDung1();
            String avatarDoiPhuong = isUser1 ? c.getAvatarNguoiDung2() : c.getAvatarNguoiDung1();
            int unread = isUser1 ? c.getSoTinChuaDocUser1() : c.getSoTinChuaDocUser2();

            dtos.add(CuocTroChuyenDto.builder()
                    .id(c.getId())
                    .doiPhuongId(doiPhuongId)
                    .tenDoiPhuong(tenDoiPhuong)
                    .avatarDoiPhuong(avatarDoiPhuong)
                    .tinNhanCuoi(c.getTinNhanCuoi())
                    .thoiGianTinNhanCuoi(c.getThoiGianTinNhanCuoi())
                    .soTinChuaDoc(unread)
                    .build());
        }

        dtos.sort((a, b) -> {
            if (a.getThoiGianTinNhanCuoi() == null && b.getThoiGianTinNhanCuoi() == null) return 0;
            if (a.getThoiGianTinNhanCuoi() == null) return 1;
            if (b.getThoiGianTinNhanCuoi() == null) return -1;
            return b.getThoiGianTinNhanCuoi().compareTo(a.getThoiGianTinNhanCuoi());
        });

        return dtos;
    }

    @Override
    public List<TinNhanDto> layLichSuTinNhan(Long currentUserId, String cuocTroChuyenId) {
        CuocTroChuyenMongo conv = cuocTroChuyenMongoRepository.findById(cuocTroChuyenId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cuộc trò chuyện"));

        if (!Objects.equals(conv.getNguoiDung1Id(), currentUserId) && !Objects.equals(conv.getNguoiDung2Id(), currentUserId)) {
            throw new IllegalArgumentException("Bạn không có quyền truy cập cuộc trò chuyện này.");
        }

        List<TinNhanMongo> messages = tinNhanMongoRepository.findByCuocTroChuyenIdOrderByThoiGianGuiAsc(cuocTroChuyenId);
        return messages.stream().map(m -> mapToTinNhanDto(m, currentUserId)).toList();
    }

    @Override
    public int danhDauDaDoc(Long currentUserId, String cuocTroChuyenId) {
        CuocTroChuyenMongo conv = cuocTroChuyenMongoRepository.findById(cuocTroChuyenId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cuộc trò chuyện"));

        List<TinNhanMongo> unreadList = tinNhanMongoRepository
                .findByCuocTroChuyenIdAndNguoiNhanIdAndDaDocFalse(cuocTroChuyenId, currentUserId);

        for (TinNhanMongo m : unreadList) {
            m.setDaDoc(true);
        }
        tinNhanMongoRepository.saveAll(unreadList);

        if (Objects.equals(conv.getNguoiDung1Id(), currentUserId)) {
            conv.setSoTinChuaDocUser1(0);
        } else {
            conv.setSoTinChuaDocUser2(0);
        }
        cuocTroChuyenMongoRepository.save(conv);

        return unreadList.size();
    }

    @Override
    public CuocTroChuyenDto layHoacTaoCuocTroChuyen(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Không thể tạo cuộc trò chuyện với chính mình.");
        }

        NguoiDung me = nguoiDungRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin của bạn"));

        NguoiDung target = nguoiDungRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng đối phương"));

        CuocTroChuyenMongo conv = cuocTroChuyenMongoRepository
                .findConversationBetweenUsers(currentUserId, targetUserId)
                .orElseGet(() -> {
                    CuocTroChuyenMongo newC = CuocTroChuyenMongo.builder()
                            .nguoiDung1Id(currentUserId)
                            .tenNguoiDung1(me.getHoTen())
                            .avatarNguoiDung1(me.getAnhDaiDien())
                            .nguoiDung2Id(targetUserId)
                            .tenNguoiDung2(target.getHoTen())
                            .avatarNguoiDung2(target.getAnhDaiDien())
                            .ngayTao(LocalDateTime.now())
                            .build();
                    return cuocTroChuyenMongoRepository.save(newC);
                });

        boolean isUser1 = Objects.equals(conv.getNguoiDung1Id(), currentUserId);
        return CuocTroChuyenDto.builder()
                .id(conv.getId())
                .doiPhuongId(targetUserId)
                .tenDoiPhuong(target.getHoTen())
                .avatarDoiPhuong(target.getAnhDaiDien())
                .tinNhanCuoi(conv.getTinNhanCuoi())
                .thoiGianTinNhanCuoi(conv.getThoiGianTinNhanCuoi())
                .soTinChuaDoc(isUser1 ? conv.getSoTinChuaDocUser1() : conv.getSoTinChuaDocUser2())
                .build();
    }

    private TinNhanDto mapToTinNhanDto(TinNhanMongo m, Long currentUserId) {
        return TinNhanDto.builder()
                .id(m.getId())
                .cuocTroChuyenId(m.getCuocTroChuyenId())
                .nguoiGuiId(m.getNguoiGuiId())
                .tenNguoiGui(m.getTenNguoiGui())
                .avatarNguoiGui(m.getAvatarNguoiGui())
                .nguoiNhanId(m.getNguoiNhanId())
                .noiDung(m.getNoiDung())
                .loaiTinNhan(m.getLoaiTinNhan())
                .duLieuDinhKem(m.getDuLieuDinhKem())
                .daDoc(m.isDaDoc())
                .thoiGianGui(m.getThoiGianGui())
                .laTinNhanCuaToi(Objects.equals(m.getNguoiGuiId(), currentUserId))
                .build();
    }
}

package com.studywithme;

import com.studywithme.chat.mongo.CuocTroChuyenMongo;
import com.studywithme.chat.mongo.TinNhanMongo;
import com.studywithme.dto.chat.CuocTroChuyenDto;
import com.studywithme.dto.chat.GuiTinNhanRequest;
import com.studywithme.dto.chat.TinNhanDto;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.shared.LoaiTinNhan;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.entity.VaiTro;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import com.studywithme.repository.mongo.CuocTroChuyenMongoRepository;
import com.studywithme.repository.mongo.TinNhanMongoRepository;
import com.studywithme.service.ChatMongoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatMongoIntegrationTest {

    @Autowired
    private ChatMongoService chatMongoService;

    @Autowired
    private CuocTroChuyenMongoRepository cuocTroChuyenMongoRepository;

    @Autowired
    private TinNhanMongoRepository tinNhanMongoRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private NguoiDung sender;
    private NguoiDung recipient;
    private List<String> createdConversationIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        VaiTro roleHv = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).build()));

        String uid = UUID.randomUUID().toString().substring(0, 8);

        sender = NguoiDung.builder()
                .email("chat_sender_" + uid + "@studywithme.vn")
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Người Gửi Chat")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>(Set.of(roleHv)))
                .build();
        sender = nguoiDungRepository.save(sender);

        recipient = NguoiDung.builder()
                .email("chat_recipient_" + uid + "@studywithme.vn")
                .matKhau(passwordEncoder.encode("Password@123"))
                .hoTen("Người Nhận Chat")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>(Set.of(roleHv)))
                .build();
        recipient = nguoiDungRepository.save(recipient);
    }

    @AfterEach
    void cleanUpMongo() {
        for (String convId : createdConversationIds) {
            tinNhanMongoRepository.deleteAll(tinNhanMongoRepository.findByCuocTroChuyenIdOrderByThoiGianGuiAsc(convId));
            cuocTroChuyenMongoRepository.deleteById(convId);
        }
    }

    @Test
    @DisplayName("MongoDB: Gửi tin nhắn văn bản và thẻ đề xuất ca học 1-1, lưu trữ thành công lên MongoDB Atlas")
    void testGuiTinNhanVaLuuTruMongoDB() {
        // 1. Gửi tin nhắn văn bản thông thường
        GuiTinNhanRequest req1 = GuiTinNhanRequest.builder()
                .nguoiNhanId(recipient.getId())
                .noiDung("Chào bạn, mình muốn hỏi về ca học kèm 1-1")
                .loaiTinNhan(LoaiTinNhan.VAN_BAN)
                .build();

        TinNhanDto msg1 = chatMongoService.guiTinNhan(sender.getId(), req1);
        assertNotNull(msg1.getId(), "Tin nhắn phải có MongoDB ObjectId");
        assertNotNull(msg1.getCuocTroChuyenId());
        assertEquals("Chào bạn, mình muốn hỏi về ca học kèm 1-1", msg1.getNoiDung());
        createdConversationIds.add(msg1.getCuocTroChuyenId());

        // 2. Gửi tin nhắn dạng Thẻ đề xuất ca học 1-1 với JSON Metadata linh hoạt trên Mongo
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("monHoc", "Toán Lớp 12");
        metadata.put("gioHoc", "Thứ 2: 19:30 - 21:00");
        metadata.put("hocPhi", 180000);

        GuiTinNhanRequest req2 = GuiTinNhanRequest.builder()
                .nguoiNhanId(recipient.getId())
                .loaiTinNhan(LoaiTinNhan.DE_XUAT_CA_HOC)
                .duLieuDinhKem(metadata)
                .build();

        TinNhanDto msg2 = chatMongoService.guiTinNhan(sender.getId(), req2);
        assertNotNull(msg2.getId());
        assertEquals(LoaiTinNhan.DE_XUAT_CA_HOC, msg2.getLoaiTinNhan());
        assertEquals(180000, msg2.getDuLieuDinhKem().get("hocPhi"));

        // 3. Kiểm tra danh sách cuộc trò chuyện
        List<CuocTroChuyenDto> senderConversations = chatMongoService.layDanhSachCuocTroChuyen(sender.getId());
        assertFalse(senderConversations.isEmpty());
        assertEquals(recipient.getHoTen(), senderConversations.get(0).getTenDoiPhuong());

        // 4. Kiểm tra lịch sử tin nhắn
        List<TinNhanDto> history = chatMongoService.layLichSuTinNhan(recipient.getId(), msg1.getCuocTroChuyenId());
        assertEquals(2, history.size());

        // 5. Đánh dấu đã đọc
        int markedCount = chatMongoService.danhDauDaDoc(recipient.getId(), msg1.getCuocTroChuyenId());
        assertEquals(2, markedCount, "Phải đánh dấu 2 tin nhắn chưa đọc");
    }
}

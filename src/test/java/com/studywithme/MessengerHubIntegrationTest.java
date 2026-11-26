package com.studywithme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studywithme.chat.mongo.CuocTroChuyenMongo;
import com.studywithme.chat.mongo.TinNhanMongo;
import com.studywithme.dto.chat.GuiTinNhanRequest;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.shared.LoaiTinNhan;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import com.studywithme.entity.enums.shared.VaiTroName;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.ThongBaoRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GÓI WBS 4.0: KIỂM THỬ TÍCH HỢP TRUNG TÂM TIN NHẮN REALTIME (MESSENGER HUB & MONGODB)
 */
@SpringBootTest
class MessengerHubIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ChatMongoService chatMongoService;

    @Autowired
    private CuocTroChuyenMongoRepository cuocTroChuyenMongoRepository;

    @Autowired
    private TinNhanMongoRepository tinNhanMongoRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;
    private NguoiDung testStudent;
    private NguoiDung testTutor;
    private List<String> createdConversationIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        VaiTro roleHv = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_HOC_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_HOC_VIEN).build()));

        VaiTro roleGv = vaiTroRepository.findByTenVaiTro(VaiTroName.ROLE_GIANG_VIEN)
                .orElseGet(() -> vaiTroRepository.save(VaiTro.builder().tenVaiTro(VaiTroName.ROLE_GIANG_VIEN).build()));

        String studentEmail = "messenger.student." + UUID.randomUUID().toString().substring(0, 8) + "@studywithme.test";
        testStudent = nguoiDungRepository.save(NguoiDung.builder()
                .email(studentEmail)
                .matKhau(passwordEncoder.encode("Pass@123456"))
                .hoTen("Học Viên Messenger Test")
                .soDienThoai("0981112233")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>(Collections.singletonList(roleHv)))
                .build());

        String tutorEmail = "messenger.tutor." + UUID.randomUUID().toString().substring(0, 8) + "@studywithme.test";
        testTutor = nguoiDungRepository.save(NguoiDung.builder()
                .email(tutorEmail)
                .matKhau(passwordEncoder.encode("Pass@123456"))
                .hoTen("Thầy Giáo Messenger Test")
                .soDienThoai("0984445566")
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .danhSachVaiTro(new HashSet<>(Collections.singletonList(roleGv)))
                .build());
    }

    @AfterEach
    void tearDown() {
        // Dọn dẹp dữ liệu chat trên MongoDB
        for (String convId : createdConversationIds) {
            try {
                tinNhanMongoRepository.deleteAll(tinNhanMongoRepository.findByCuocTroChuyenIdOrderByThoiGianGuiAsc(convId));
                cuocTroChuyenMongoRepository.deleteById(convId);
            } catch (Exception ignored) {}
        }

        // Dọn dẹp cuộc trò chuyện giữa 2 user test nếu còn
        if (testStudent != null && testTutor != null) {
            try {
                Optional<CuocTroChuyenMongo> conv = cuocTroChuyenMongoRepository
                        .findConversationBetweenUsers(testStudent.getId(), testTutor.getId());
                conv.ifPresent(c -> {
                    tinNhanMongoRepository.deleteAll(tinNhanMongoRepository.findByCuocTroChuyenIdOrderByThoiGianGuiAsc(c.getId()));
                    cuocTroChuyenMongoRepository.deleteById(c.getId());
                });
            } catch (Exception ignored) {}
        }

        // Xóa thông báo liên quan (tránh lỗi khóa ngoại FK) rồi xóa user trong SQL Server
        if (testStudent != null && testStudent.getId() != null) {
            try {
                thongBaoRepository.deleteAll(thongBaoRepository.findByNguoiDungIdOrderByThoiGianTaoDesc(testStudent.getId(), Pageable.unpaged()));
                nguoiDungRepository.deleteById(testStudent.getId());
            } catch (Exception ignored) {}
        }
        if (testTutor != null && testTutor.getId() != null) {
            try {
                thongBaoRepository.deleteAll(thongBaoRepository.findByNguoiDungIdOrderByThoiGianTaoDesc(testTutor.getId(), Pageable.unpaged()));
                nguoiDungRepository.deleteById(testTutor.getId());
            } catch (Exception ignored) {}
        }
    }

    @Test
    @DisplayName("WBS 4.2.1: Truy cập /chat khi chưa đăng nhập -> Chuyển hướng sang /login")
    void testAccessMessengerHub_Unauthenticated() throws Exception {
        mockMvc.perform(get("/chat"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("WBS 4.2.2: Học viên đã đăng nhập truy cập /chat -> Trả về giao diện Messenger Hub 2 cột thành công")
    void testAccessMessengerHub_Authenticated() throws Exception {
        mockMvc.perform(get("/chat")
                        .with(user(testStudent.getEmail()).roles("HOC_VIEN")))
                .andExpect(status().isOk())
                .andExpect(view().name("chat/messenger"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("conversations"))
                .andExpect(model().attribute("currentUserId", testStudent.getId()));
    }

    private boolean isMongoConnected() {
        try {
            cuocTroChuyenMongoRepository.count();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    @DisplayName("WBS 4.2.3: Truy cập /chat?userId={targetId} -> Tự động khởi tạo cuộc trò chuyện với gia sư")
    void testAccessMessengerHub_WithTargetUser() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeTrue(isMongoConnected(), "Bỏ qua test do cụm MongoDB Atlas từ xa không thể kết nối");

        mockMvc.perform(get("/chat")
                        .param("userId", String.valueOf(testTutor.getId()))
                        .with(user(testStudent.getEmail()).roles("HOC_VIEN")))
                .andExpect(status().isOk())
                .andExpect(view().name("chat/messenger"))
                .andExpect(model().attributeExists("activeConvId"))
                .andExpect(model().attribute("activeTargetUserId", testTutor.getId()));

        // Kiểm tra MongoDB đã tạo hội thoại này
        Optional<CuocTroChuyenMongo> conv = cuocTroChuyenMongoRepository
                .findConversationBetweenUsers(testStudent.getId(), testTutor.getId());
        assertTrue(conv.isPresent(), "Cuộc trò chuyện giữa Student và Tutor phải được lưu vào MongoDB");
        createdConversationIds.add(conv.get().getId());
    }

    @Test
    @DisplayName("WBS 4.1 & 4.3: Gửi tin nhắn Text và Thẻ Đề Xuất Ca Học 1-1 qua REST API -> Lưu chuẩn vào MongoDB")
    void testSendMessageAndProposalCard() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeTrue(isMongoConnected(), "Bỏ qua test do cụm MongoDB Atlas từ xa không thể kết nối");

        // 1. Gửi tin nhắn Text bình thường
        GuiTinNhanRequest textReq = GuiTinNhanRequest.builder()
                .nguoiNhanId(testTutor.getId())
                .noiDung("Chào thầy, em muốn hỏi thông tin ca học Toán 12")
                .loaiTinNhan(LoaiTinNhan.VAN_BAN)
                .build();

        mockMvc.perform(post("/api/chat/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(textReq))
                        .with(csrf())
                        .with(user(testStudent.getEmail()).roles("HOC_VIEN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.noiDung").value("Chào thầy, em muốn hỏi thông tin ca học Toán 12"));

        // 2. Gia sư gửi tin nhắn Thẻ đề xuất ca học kèm 1-1 (WBS 4.3)
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("tutorId", testTutor.getId());
        metadata.put("hocPhi", 300000);
        metadata.put("thoiGianBatDau", "2026-11-20T19:30:00");
        metadata.put("thoiGianKetThuc", "2026-11-20T21:00:00");

        GuiTinNhanRequest cardReq = GuiTinNhanRequest.builder()
                .nguoiNhanId(testStudent.getId())
                .noiDung("Thầy gửi em đề xuất ca học 1-1 tối thứ 6 tuần này nhé!")
                .loaiTinNhan(LoaiTinNhan.DE_XUAT_CA_HOC)
                .duLieuDinhKem(metadata)
                .build();

        mockMvc.perform(post("/api/chat/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardReq))
                        .with(csrf())
                        .with(user(testTutor.getEmail()).roles("GIANG_VIEN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.loaiTinNhan").value("DE_XUAT_CA_HOC"))
                .andExpect(jsonPath("$.duLieu.duLieuDinhKem.hocPhi").value(300000));

        // 3. Kiểm tra danh sách tin nhắn từ MongoDB
        Optional<CuocTroChuyenMongo> conv = cuocTroChuyenMongoRepository
                .findConversationBetweenUsers(testStudent.getId(), testTutor.getId());
        assertTrue(conv.isPresent());
        createdConversationIds.add(conv.get().getId());

        List<TinNhanMongo> messages = tinNhanMongoRepository.findByCuocTroChuyenIdOrderByThoiGianGuiAsc(conv.get().getId());
        assertEquals(2, messages.size(), "Phải có đúng 2 tin nhắn được lưu trong MongoDB");
        assertEquals(LoaiTinNhan.VAN_BAN, messages.get(0).getLoaiTinNhan());
        assertEquals(LoaiTinNhan.DE_XUAT_CA_HOC, messages.get(1).getLoaiTinNhan());

        // 4. Test đánh dấu đã đọc (Seen status)
        mockMvc.perform(post("/api/chat/conversations/" + conv.get().getId() + "/read")
                        .with(csrf())
                        .with(user(testStudent.getEmail()).roles("HOC_VIEN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true));

        TinNhanMongo updatedMsg = tinNhanMongoRepository.findById(messages.get(1).getId()).orElseThrow();
        assertTrue(updatedMsg.isDaDoc(), "Tin nhắn của tutor phải chuyển thành đã đọc sau khi student đọc");
    }
}

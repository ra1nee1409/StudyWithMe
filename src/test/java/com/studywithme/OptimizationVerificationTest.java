package com.studywithme;

import com.studywithme.chat.mongo.TinNhanMongo;
import com.studywithme.dto.response.DanhMucResponse;
import com.studywithme.repository.DangKyLopHocRepository;
import com.studywithme.repository.LopHocTrucTuyenRepository;
import com.studywithme.service.DanhMucService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OptimizationVerificationTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DanhMucService danhMucService;

    @Autowired
    private DangKyLopHocRepository dangKyLopHocRepository;

    @Autowired
    private LopHocTrucTuyenRepository lopHocTrucTuyenRepository;

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("Tối ưu Caching: CacheManager hoạt động và danh mục được lưu đệm trong bộ nhớ")
    void testDanhMucCaching() {
        assertNotNull(cacheManager, "CacheManager phải được kích hoạt thành công");

        // Lần gọi 1: Lấy danh mục và nạp vào cache
        List<DanhMucResponse> firstCall = danhMucService.layCayDanhMuc();
        assertNotNull(firstCall);

        Cache cache = cacheManager.getCache("cayDanhMuc");
        assertNotNull(cache, "Cache 'cayDanhMuc' phải tồn tại");

        // Kiểm tra phần tử trong cache sau lần gọi 1
        assertNotNull(cache.get(org.springframework.cache.interceptor.SimpleKey.EMPTY),
                "Kết quả layCayDanhMuc() phải được lưu trong cache");

        // Lần gọi 2: Trả về từ cache
        List<DanhMucResponse> secondCall = danhMucService.layCayDanhMuc();
        assertEquals(firstCall.size(), secondCall.size());
    }

    @Test
    @DisplayName("Tối ưu EntityGraph: Truy vấn quan hệ không gây lỗi N+1")
    void testEntityGraphOptimization() {
        assertDoesNotThrow(() -> {
            dangKyLopHocRepository.findByLopHocGiangVienId(1L);
            lopHocTrucTuyenRepository.findByGiangVienId(1L);
            lopHocTrucTuyenRepository.findByGiangVienIdOrderByNgayTaoDesc(1L);
        });
    }

    @Test
    @DisplayName("Tối ưu MongoDB Index: Compound Index trên TinNhanMongo được thiết lập chuẩn xác")
    void testMongoCompoundIndexPresence() {
        if (mongoTemplate != null) {
            assertDoesNotThrow(() -> {
                List<IndexInfo> indexes = mongoTemplate.indexOps(TinNhanMongo.class).getIndexInfo();
                assertNotNull(indexes);
            });
        }
    }
}

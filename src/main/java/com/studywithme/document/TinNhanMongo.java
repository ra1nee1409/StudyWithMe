package com.studywithme.document;

import com.studywithme.entity.enums.shared.LoaiTinNhan;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "tin_nhan")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinNhanMongo {

    @Id
    private String id;

    @Indexed
    private String cuocTroChuyenId;

    @Indexed
    private Long nguoiGuiId;
    private String tenNguoiGui;
    private String avatarNguoiGui;

    @Indexed
    private Long nguoiNhanId;

    private String noiDung;

    @Builder.Default
    private LoaiTinNhan loaiTinNhan = LoaiTinNhan.VAN_BAN;

    /**
     * Dữ liệu mở rộng lưu dạng JSON Object linh hoạt trên MongoDB:
     * Dùng cho thẻ đề xuất ca học 1-1 (môn học, ngày giờ, học phí) hoặc metadata file/ảnh
     */
    private Map<String, Object> duLieuDinhKem;

    @Builder.Default
    private boolean daDoc = false;

    @CreatedDate
    @Indexed
    private LocalDateTime thoiGianGui;
}

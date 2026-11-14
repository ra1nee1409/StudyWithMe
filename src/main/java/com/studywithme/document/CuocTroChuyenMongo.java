package com.studywithme.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "cuoc_tro_chuyen")
@CompoundIndex(name = "unique_two_users", def = "{'nguoiDung1Id': 1, 'nguoiDung2Id': 1}", unique = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuocTroChuyenMongo {

    @Id
    private String id;

    @Indexed
    private Long nguoiDung1Id;

    @Indexed
    private Long nguoiDung2Id;

    private String tenNguoiDung1;
    private String avatarNguoiDung1;

    private String tenNguoiDung2;
    private String avatarNguoiDung2;

    private String tinNhanCuoi;
    private LocalDateTime thoiGianTinNhanCuoi;

    @Builder.Default
    private int soTinChuaDocUser1 = 0;

    @Builder.Default
    private int soTinChuaDocUser2 = 0;

    @CreatedDate
    private LocalDateTime ngayTao;

    @LastModifiedDate
    private LocalDateTime ngayCapNhat;
}

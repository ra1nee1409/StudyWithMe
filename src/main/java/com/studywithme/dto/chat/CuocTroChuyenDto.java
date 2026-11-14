package com.studywithme.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuocTroChuyenDto {

    private String id;
    private Long doiPhuongId;
    private String tenDoiPhuong;
    private String avatarDoiPhuong;
    private String tinNhanCuoi;
    private LocalDateTime thoiGianTinNhanCuoi;
    private int soTinChuaDoc;
}

package com.studywithme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XacNhanChuyenTienRequest {

    private String maGiaoDichNganHang;
    private String ghiChu;
}

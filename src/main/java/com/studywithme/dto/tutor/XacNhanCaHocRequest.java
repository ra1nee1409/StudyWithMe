package com.studywithme.dto.tutor;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XacNhanCaHocRequest {

    private boolean chapNhan;
    private String lyDoTuChoi;
}

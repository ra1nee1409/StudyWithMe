package com.studywithme.dto.tutor;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatLinkMeetRequest {

    @NotBlank(message = "Đường dẫn link phòng học (Google Meet / Zoom) không được để trống")
    private String linkPhongHoc;
}

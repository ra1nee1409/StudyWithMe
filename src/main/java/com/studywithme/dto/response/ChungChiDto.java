package com.studywithme.dto.response;

import com.studywithme.entity.enums.giaovien.LoaiChungChi;
import com.studywithme.entity.enums.admin.TrangThaiXacThuc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChungChiDto {
    private Long id;
    private LoaiChungChi loaiChungChi;
    private String tenChungChi;
    private String diemSoHoacXepLoai;
    private String noiCap;
    private LocalDate ngayCap;
    private String linkAnhChungChi;
    private TrangThaiXacThuc trangThaiXacThuc;
    private String lyDoTuChoi;
}

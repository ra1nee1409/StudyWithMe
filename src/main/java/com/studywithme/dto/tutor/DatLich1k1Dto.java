package com.studywithme.dto.tutor;

import com.studywithme.entity.DatLich1k1;
import com.studywithme.entity.enums.hocvien.TrangThaiDatLich;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatLich1k1Dto {

    private Long id;
    private String maDatLich;
    private Long hocVienId;
    private String tenHocVien;
    private String emailHocVien;
    private String sdtHocVien;
    private Long giangVienId;
    private String tenGiangVien;
    private Long monHocId;
    private String tenMonHoc;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private BigDecimal hocPhiCaHoc;
    private BigDecimal tyLeHoaHongSan;
    private BigDecimal phiSanKhauTru;
    private BigDecimal thucNhanGiaSu;
    private TrangThaiDatLich trangThai;
    private String trangThaiMoTa;
    private String linkPhongHoc;
    private String ghiChuHocVien;
    private String lyDoHuy;
    private LocalDateTime ngayTao;

    public static DatLich1k1Dto fromEntity(DatLich1k1 entity) {
        if (entity == null) return null;

        String tenHv = null, emailHv = null, sdtHv = null;
        if (entity.getHocVien() != null) {
            tenHv = entity.getHocVien().getHoTen();
            emailHv = entity.getHocVien().getEmail();
            sdtHv = entity.getHocVien().getSoDienThoai();
        }

        String tenGv = null;
        if (entity.getGiangVien() != null && entity.getGiangVien().getNguoiDung() != null) {
            tenGv = entity.getGiangVien().getNguoiDung().getHoTen();
        }

        String tenMon = null;
        Long monId = null;
        if (entity.getMonHoc() != null) {
            monId = entity.getMonHoc().getId();
            tenMon = entity.getMonHoc().getTenDanhMuc();
        }

        return DatLich1k1Dto.builder()
                .id(entity.getId())
                .maDatLich(entity.getMaDatLich())
                .hocVienId(entity.getHocVien() != null ? entity.getHocVien().getId() : null)
                .tenHocVien(tenHv)
                .emailHocVien(emailHv)
                .sdtHocVien(sdtHv)
                .giangVienId(entity.getGiangVien() != null ? entity.getGiangVien().getId() : null)
                .tenGiangVien(tenGv)
                .monHocId(monId)
                .tenMonHoc(tenMon)
                .thoiGianBatDau(entity.getThoiGianBatDau())
                .thoiGianKetThuc(entity.getThoiGianKetThuc())
                .hocPhiCaHoc(entity.getHocPhiCaHoc())
                .tyLeHoaHongSan(entity.getTyLeHoaHongSan())
                .phiSanKhauTru(entity.getPhiSanKhauTru())
                .thucNhanGiaSu(entity.getThucNhanGiaSu())
                .trangThai(entity.getTrangThai())
                .trangThaiMoTa(entity.getTrangThai() != null ? entity.getTrangThai().getMoTa() : "")
                .linkPhongHoc(entity.getLinkPhongHoc())
                .ghiChuHocVien(entity.getGhiChuHocVien())
                .lyDoHuy(entity.getLyDoHuy())
                .ngayTao(entity.getNgayTao())
                .build();
    }
}

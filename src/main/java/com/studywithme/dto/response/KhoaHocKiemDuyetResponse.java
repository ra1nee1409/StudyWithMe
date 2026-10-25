package com.studywithme.dto.response;

import com.studywithme.entity.enums.admin.TrangThaiKhoaHoc;
import com.studywithme.entity.enums.giaovien.TrinhDoKhoaHoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KhoaHocKiemDuyetResponse {
    private Long id;
    private String tieuDe;
    private String duongDanSlug;
    private String moTaChiTiet;
    private String anhDaiDienKhoaHoc;
    private BigDecimal giaGoc;
    private BigDecimal giaKhuyenMai;
    private TrinhDoKhoaHoc trinhDo;
    private String thePhanLoai;
    private TrangThaiKhoaHoc trangThai;
    private String lyDoTuChoi;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    // Giảng viên info
    private Long giangVienId;
    private String tenGiangVien;
    private String emailGiangVien;
    private String anhDaiDienGiangVien;
    private String chuyenMonGiangVien;

    // Danh mục info
    private Long danhMucId;
    private String tenDanhMuc;

    // Thống kê giáo trình
    private Integer tongSoChuong;
    private Integer tongSoBaiHoc;
    private Integer tongThoiLuongGiay;

    // Chi tiết giáo trình (nạp khi xem chi tiết kiểm duyệt)
    @Builder.Default
    private List<ChuongHocDto> danhSachChuongHoc = new ArrayList<>();
}

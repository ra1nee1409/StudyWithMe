-- ==============================================================================
-- ĐỒ ÁN TỐT NGHIỆP: NỀN TẢNG HỌC TẬP & KẾT NỐI GIA SƯ STUDYWITHME
-- CƠ SỞ DỮ LIỆU DÀNH CHO MICROSOFT SQL SERVER (T-SQL)
-- ==============================================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'studywithme_db')
BEGIN
    CREATE DATABASE [studywithme_db];
END
GO

USE [studywithme_db];
GO

-- 1. BẢNG VAI TRÒ (Phân quyền người dùng)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='vai_tro' AND xtype='U')
BEGIN
    CREATE TABLE [vai_tro] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [ten_vai_tro] NVARCHAR(50) NOT NULL UNIQUE,
        [mo_ta] NVARCHAR(255) NULL
    );
END
GO

-- 2. BẢNG NGƯỜI DÙNG (Tài khoản học viên, giảng viên, admin)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='nguoi_dung' AND xtype='U')
BEGIN
    CREATE TABLE [nguoi_dung] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [email] NVARCHAR(100) NOT NULL UNIQUE,
        [mat_khau] NVARCHAR(255) NOT NULL,
        [ho_ten] NVARCHAR(100) NOT NULL,
        [so_dien_thoai] NVARCHAR(20) NULL,
        [anh_dai_dien] NVARCHAR(500) NULL,
        [trang_thai] NVARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG' CHECK ([trang_thai] IN ('HOAT_DONG', 'TAM_KHOA', 'BI_CAM')),
        [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        [ngay_cap_nhat] DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
    CREATE INDEX [idx_nguoi_dung_email] ON [nguoi_dung]([email]);
END
GO

-- 3. BẢNG NGƯỜI DÙNG - VAI TRÒ (Liên kết nhiều - nhiều)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='nguoi_dung_vai_tro' AND xtype='U')
BEGIN
    CREATE TABLE [nguoi_dung_vai_tro] (
        [nguoi_dung_id] BIGINT NOT NULL,
        [vai_tro_id] BIGINT NOT NULL,
        PRIMARY KEY ([nguoi_dung_id], [vai_tro_id]),
        CONSTRAINT [fk_ndvt_nguoi_dung] FOREIGN KEY ([nguoi_dung_id]) REFERENCES [nguoi_dung] ([id]) ON DELETE CASCADE,
        CONSTRAINT [fk_ndvt_vai_tro] FOREIGN KEY ([vai_tro_id]) REFERENCES [vai_tro] ([id]) ON DELETE CASCADE
    );
END
GO

-- 4. BẢNG HỒ SƠ GIẢNG VIÊN (Thông tin giáo viên / gia sư, ví tiền)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ho_so_giang_vien' AND xtype='U')
BEGIN
    CREATE TABLE [ho_so_giang_vien] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [nguoi_dung_id] BIGINT NOT NULL UNIQUE,
        [tieu_de_chuyen_mon] NVARCHAR(255) NULL,
        [gioi_thieu_ban_than] NVARCHAR(MAX) NULL,
        [link_video_day_thu] NVARCHAR(500) NULL,
        [trang_thai_duyet] NVARCHAR(20) NOT NULL DEFAULT 'CHO_DUYET' CHECK ([trang_thai_duyet] IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI')),
        [ly_do_tu_choi] NVARCHAR(MAX) NULL,
        [so_du_vi] DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
        [ten_ngan_hang] NVARCHAR(100) NULL,
        [so_tai_khoan_ngan_hang] NVARCHAR(50) NULL,
        [ten_chu_tai_khoan] NVARCHAR(100) NULL,
        [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        [ngay_cap_nhat] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_hsgv_nguoi_dung] FOREIGN KEY ([nguoi_dung_id]) REFERENCES [nguoi_dung] ([id]) ON DELETE CASCADE
    );
END
GO

-- 5. BẢNG CHỨNG CHỈ GIẢNG VIÊN (Bằng cấp, IELTS, TOEIC để Admin duyệt)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='chung_chi_giang_vien' AND xtype='U')
BEGIN
    CREATE TABLE [chung_chi_giang_vien] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [giang_vien_id] BIGINT NOT NULL,
        [loai_chung_chi] NVARCHAR(30) NOT NULL CHECK ([loai_chung_chi] IN ('BANG_DAI_HOC', 'IELTS', 'TOEIC', 'CHUNG_CHI_SU_PHAM', 'KHAC')),
        [ten_chung_chi] NVARCHAR(255) NOT NULL,
        [diem_so_hoac_xep_loai] NVARCHAR(100) NULL,
        [noi_cap] NVARCHAR(255) NULL,
        [ngay_cap] DATE NULL,
        [link_anh_chung_chi] NVARCHAR(500) NOT NULL,
        [trang_thai_xac_thuc] NVARCHAR(30) NOT NULL DEFAULT 'CHO_XAC_THUC' CHECK ([trang_thai_xac_thuc] IN ('CHO_XAC_THUC', 'DA_XAC_THUC', 'TU_CHOI')),
        [ly_do_tu_choi] NVARCHAR(MAX) NULL,
        [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        [ngay_cap_nhat] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_ccgv_giang_vien] FOREIGN KEY ([giang_vien_id]) REFERENCES [ho_so_giang_vien] ([id]) ON DELETE CASCADE
    );
END
GO

-- 6. BẢNG DANH MỤC (Cấp 1-3, Môn học, IELTS, TOEIC)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='danh_muc' AND xtype='U')
BEGIN
    CREATE TABLE [danh_muc] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [ten_danh_muc] NVARCHAR(100) NOT NULL,
        [duong_dan_slug] NVARCHAR(100) NOT NULL UNIQUE,
        [danh_muc_cha_id] BIGINT NULL,
        [loai_danh_muc] NVARCHAR(30) NOT NULL DEFAULT 'MON_HOC' CHECK ([loai_danh_muc] IN ('CAP_HOC_K12', 'CHUNG_CHI_NGOAI_NGU', 'MON_HOC')),
        [link_icon] NVARCHAR(500) NULL,
        [thu_tu_hien_thi] INT NOT NULL DEFAULT 0,
        [kich_hoat] BIT NOT NULL DEFAULT 1,
        CONSTRAINT [fk_danh_muc_cha] FOREIGN KEY ([danh_muc_cha_id]) REFERENCES [danh_muc] ([id])
    );
END
GO

-- 7. BẢNG KHÓA HỌC (Khóa học video theo yêu cầu)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='khoa_hoc' AND xtype='U')
BEGIN
    CREATE TABLE [khoa_hoc] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [giang_vien_id] BIGINT NOT NULL,
        [danh_muc_id] BIGINT NOT NULL,
        [tieu_de] NVARCHAR(255) NOT NULL,
        [duong_dan_slug] NVARCHAR(255) NOT NULL UNIQUE,
        [mo_ta_chi_tiet] NVARCHAR(MAX) NULL,
        [anh_dai_dien_khoa_hoc] NVARCHAR(500) NULL,
        [gia_goc] DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
        [gia_khuyen_mai] DECIMAL(15, 2) NULL,
        [trinh_do] NVARCHAR(30) NOT NULL DEFAULT 'MOI_TRINH_DO' CHECK ([trinh_do] IN ('CO_BAN', 'TRUNG_CAP', 'NANG_CAO', 'MOI_TRINH_DO')),
        [the_phan_loai] NVARCHAR(50) NULL,
        [trang_thai] NVARCHAR(30) NOT NULL DEFAULT 'BAN_NHAP' CHECK ([trang_thai] IN ('BAN_NHAP', 'CHO_DUYET', 'DA_XUAT_BAN', 'TU_CHOI')),
        [ly_do_tu_choi] NVARCHAR(MAX) NULL,
        [diem_danh_gia_tb] DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
        [tong_so_danh_gia] INT NOT NULL DEFAULT 0,
        [tong_so_hoc_vien] INT NOT NULL DEFAULT 0,
        [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        [ngay_cap_nhat] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_khoa_hoc_giang_vien] FOREIGN KEY ([giang_vien_id]) REFERENCES [ho_so_giang_vien] ([id]),
        CONSTRAINT [fk_khoa_hoc_danh_muc] FOREIGN KEY ([danh_muc_id]) REFERENCES [danh_muc] ([id])
    );
END
GO

-- 8. BẢNG CHƯƠNG HỌC (Các chương mục trong khóa học)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='chuong_hoc' AND xtype='U')
BEGIN
    CREATE TABLE [chuong_hoc] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [khoa_hoc_id] BIGINT NOT NULL,
        [tieu_de_chuong] NVARCHAR(255) NOT NULL,
        [thu_tu_sap_xep] INT NOT NULL DEFAULT 1,
        CONSTRAINT [fk_chuong_khoa_hoc] FOREIGN KEY ([khoa_hoc_id]) REFERENCES [khoa_hoc] ([id]) ON DELETE CASCADE
    );
END
GO

-- 9. BẢNG BÀI HỌC (Bài giảng video chi tiết)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='bai_hoc' AND xtype='U')
BEGIN
    CREATE TABLE [bai_hoc] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [chuong_hoc_id] BIGINT NOT NULL,
        [tieu_de_bai_hoc] NVARCHAR(255) NOT NULL,
        [link_video] NVARCHAR(500) NULL,
        [thoi_luong_giay] INT NOT NULL DEFAULT 0,
        [cho_phep_hoc_thu] BIT NOT NULL DEFAULT 0,
        [thu_tu_sap_xep] INT NOT NULL DEFAULT 1,
        CONSTRAINT [fk_bai_hoc_chuong] FOREIGN KEY ([chuong_hoc_id]) REFERENCES [chuong_hoc] ([id]) ON DELETE CASCADE
    );
END
GO

-- 10. BẢNG TIẾN ĐỘ BÀI HỌC (Theo dõi học viên xem tới giây nào, hoàn thành bài nào)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tien_do_bai_hoc' AND xtype='U')
BEGIN
    CREATE TABLE [tien_do_bai_hoc] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [nguoi_dung_id] BIGINT NOT NULL,
        [bai_hoc_id] BIGINT NOT NULL,
        [da_hoan_thanh] BIT NOT NULL DEFAULT 0,
        [giay_dung_lai] INT NOT NULL DEFAULT 0,
        [thoi_gian_hoan_thanh] DATETIME2 NULL,
        [ngay_cap_nhat] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_tien_do_nguoi_dung] FOREIGN KEY ([nguoi_dung_id]) REFERENCES [nguoi_dung] ([id]) ON DELETE CASCADE,
        CONSTRAINT [fk_tien_do_bai_hoc] FOREIGN KEY ([bai_hoc_id]) REFERENCES [bai_hoc] ([id]) ON DELETE CASCADE,
        CONSTRAINT [uk_nd_bai_hoc_tien_do] UNIQUE ([nguoi_dung_id], [bai_hoc_id])
    );
END
GO

-- 11. BẢNG LỚP HỌC TRỰC TUYẾN (Gia sư dạy kèm trực tiếp qua Meet / Zoom)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='lop_hoc_truc_tuyen' AND xtype='U')
BEGIN
    CREATE TABLE [lop_hoc_truc_tuyen] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [giang_vien_id] BIGINT NOT NULL,
        [danh_muc_id] BIGINT NOT NULL,
        [tieu_de_lop] NVARCHAR(255) NOT NULL,
        [mo_ta_lop] NVARCHAR(MAX) NULL,
        [si_so_toi_da] INT NOT NULL DEFAULT 1,
        [hoc_phi_moi_hoc_vien] DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
        [ngay_bat_dau] DATE NULL,
        [ngay_ket_thuc] DATE NULL,
        [lich_hoc_trong_tuan] NVARCHAR(255) NULL,
        [link_phong_hoc] NVARCHAR(500) NULL,
        [trang_thai] NVARCHAR(30) NOT NULL DEFAULT 'DANG_MO_DANG_KY' CHECK ([trang_thai] IN ('DANG_MO_DANG_KY', 'DANG_HOC', 'DA_KET_THUC', 'DA_HUY')),
        [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_lop_truc_tuyen_gv] FOREIGN KEY ([giang_vien_id]) REFERENCES [ho_so_giang_vien] ([id]),
        CONSTRAINT [fk_lop_truc_tuyen_dm] FOREIGN KEY ([danh_muc_id]) REFERENCES [danh_muc] ([id])
    );
END
GO

-- 12. BẢNG ĐƠN HÀNG (Mua khóa học hoặc đặt chỗ lớp học)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='don_hang' AND xtype='U')
BEGIN
    CREATE TABLE [don_hang] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [ma_don_hang] NVARCHAR(50) NOT NULL UNIQUE,
        [nguoi_dung_id] BIGINT NOT NULL,
        [tong_tien_thanh_toan] DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
        [phuong_thuc_thanh_toan] NVARCHAR(30) NOT NULL DEFAULT 'VNPAY' CHECK ([phuong_thuc_thanh_toan] IN ('VNPAY', 'MOMO', 'CHUYEN_KHOAN')),
        [trang_thai_thanh_toan] NVARCHAR(30) NOT NULL DEFAULT 'CHO_THANH_TOAN' CHECK ([trang_thai_thanh_toan] IN ('CHO_THANH_TOAN', 'DA_THANH_TOAN', 'THAT_BAI', 'DA_HUY')),
        [ngay_thanh_toan] DATETIME2 NULL,
        [ngay_tao_don] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_don_hang_nguoi_dung] FOREIGN KEY ([nguoi_dung_id]) REFERENCES [nguoi_dung] ([id])
    );
END
GO

-- 13. BẢNG CHI TIẾT ĐƠN HÀNG (Từng khóa học hoặc lớp học trong đơn)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='chi_tiet_don_hang' AND xtype='U')
BEGIN
    CREATE TABLE [chi_tiet_don_hang] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [don_hang_id] BIGINT NOT NULL,
        [loai_san_pham] NVARCHAR(30) NOT NULL CHECK ([loai_san_pham] IN ('KHOA_HOC_VIDEO', 'LOP_HOC_GIA_SU')),
        [san_pham_id] BIGINT NOT NULL,
        [don_gia] DECIMAL(15, 2) NOT NULL,
        CONSTRAINT [fk_chi_tiet_don_hang] FOREIGN KEY ([don_hang_id]) REFERENCES [don_hang] ([id]) ON DELETE CASCADE
    );
END
GO

-- 14. BẢNG ĐĂNG KÝ LỚP HỌC (Quản lý học viên trong lớp gia sư trực tiếp)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='dang_ky_lop_hoc' AND xtype='U')
BEGIN
    CREATE TABLE [dang_ky_lop_hoc] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [lop_hoc_id] BIGINT NOT NULL,
        [hoc_vien_id] BIGINT NOT NULL,
        [don_hang_id] BIGINT NULL,
        [trang_thai_tham_gia] NVARCHAR(30) NOT NULL DEFAULT 'DA_XAC_NHAN' CHECK ([trang_thai_tham_gia] IN ('DA_XAC_NHAN', 'DA_HUY', 'DA_HOAN_TIEN')),
        [ngay_dang_ky] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_dang_ky_lop_hoc] FOREIGN KEY ([lop_hoc_id]) REFERENCES [lop_hoc_truc_tuyen] ([id]) ON DELETE CASCADE,
        CONSTRAINT [fk_dang_ky_hoc_vien] FOREIGN KEY ([hoc_vien_id]) REFERENCES [nguoi_dung] ([id]) ON DELETE CASCADE,
        CONSTRAINT [fk_dang_ky_don_hang] FOREIGN KEY ([don_hang_id]) REFERENCES [don_hang] ([id]),
        CONSTRAINT [uk_hoc_vien_lop_hoc] UNIQUE ([hoc_vien_id], [lop_hoc_id])
    );
END
GO

-- 15. BẢNG GHI DANH KHÓA HỌC (Quyền sở hữu khóa học video sau khi thanh toán)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ghi_danh_khoa_hoc' AND xtype='U')
BEGIN
    CREATE TABLE [ghi_danh_khoa_hoc] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [nguoi_dung_id] BIGINT NOT NULL,
        [khoa_hoc_id] BIGINT NOT NULL,
        [don_hang_id] BIGINT NULL,
        [ngay_ghi_danh] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        [phan_tram_tien_do] INT NOT NULL DEFAULT 0,
        CONSTRAINT [fk_ghi_danh_nguoi_dung] FOREIGN KEY ([nguoi_dung_id]) REFERENCES [nguoi_dung] ([id]) ON DELETE CASCADE,
        CONSTRAINT [fk_ghi_danh_khoa_hoc] FOREIGN KEY ([khoa_hoc_id]) REFERENCES [khoa_hoc] ([id]) ON DELETE CASCADE,
        CONSTRAINT [fk_ghi_danh_don_hang] FOREIGN KEY ([don_hang_id]) REFERENCES [don_hang] ([id]),
        CONSTRAINT [uk_nd_khoa_hoc_ghi_danh] UNIQUE ([nguoi_dung_id], [khoa_hoc_id])
    );
END
GO

-- 16. BẢNG DANH SÁCH YÊU THÍCH (Wishlist: lưu khóa học hoặc giảng viên)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='danh_sach_yeu_thich' AND xtype='U')
BEGIN
    CREATE TABLE [danh_sach_yeu_thich] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [nguoi_dung_id] BIGINT NOT NULL,
        [loai_yeu_thich] NVARCHAR(30) NOT NULL CHECK ([loai_yeu_thich] IN ('KHOA_HOC', 'GIANG_VIEN')),
        [muc_tieu_id] BIGINT NOT NULL,
        [ngay_luu] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_yeu_thich_nguoi_dung] FOREIGN KEY ([nguoi_dung_id]) REFERENCES [nguoi_dung] ([id]) ON DELETE CASCADE,
        CONSTRAINT [uk_nd_loai_muc_tieu] UNIQUE ([nguoi_dung_id], [loai_yeu_thich], [muc_tieu_id])
    );
END
GO

-- 17. BẢNG ĐÁNH GIÁ VÀ NHẬN XÉT (Review sao & bình luận của học viên)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='danh_gia_nhan_xet' AND xtype='U')
BEGIN
    CREATE TABLE [danh_gia_nhan_xet] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [nguoi_dung_id] BIGINT NOT NULL,
        [loai_danh_gia] NVARCHAR(30) NOT NULL CHECK ([loai_danh_gia] IN ('KHOA_HOC', 'GIANG_VIEN')),
        [muc_tieu_id] BIGINT NOT NULL,
        [so_sao_danh_gia] TINYINT NOT NULL CHECK ([so_sao_danh_gia] BETWEEN 1 AND 5),
        [noi_dung_nhan_xet] NVARCHAR(MAX) NULL,
        [duoc_hien_thi] BIT NOT NULL DEFAULT 1,
        [ngay_danh_gia] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_danh_gia_nguoi_dung] FOREIGN KEY ([nguoi_dung_id]) REFERENCES [nguoi_dung] ([id]) ON DELETE CASCADE
    );
END
GO

-- 18. BẢNG BANNER QUẢNG CÁO (Admin quản lý banner trên trang chủ)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='banner_quang_cao' AND xtype='U')
BEGIN
    CREATE TABLE [banner_quang_cao] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [tieu_de] NVARCHAR(255) NOT NULL,
        [link_anh_banner] NVARCHAR(500) NOT NULL,
        [link_chuyen_huong] NVARCHAR(500) NULL,
        [thu_tu_hien_thi] INT NOT NULL DEFAULT 0,
        [kich_hoat] BIT NOT NULL DEFAULT 1,
        [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        [ngay_cap_nhat] DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

-- 19. BẢNG YÊU CẦU RÚT TIỀN (Giảng viên rút tiền về tài khoản ngân hàng)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='yeu_cau_rut_tien' AND xtype='U')
BEGIN
    CREATE TABLE [yeu_cau_rut_tien] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [giang_vien_id] BIGINT NOT NULL,
        [so_tien_rut] DECIMAL(15, 2) NOT NULL,
        [ten_ngan_hang] NVARCHAR(100) NOT NULL,
        [so_tai_khoan] NVARCHAR(50) NOT NULL,
        [ten_chu_tai_khoan] NVARCHAR(100) NOT NULL,
        [trang_thai] NVARCHAR(30) NOT NULL DEFAULT 'CHO_XU_LY' CHECK ([trang_thai] IN ('CHO_XU_LY', 'DA_CHUYEN_TIEN', 'TU_CHOI')),
        [ghi_chu_admin] NVARCHAR(MAX) NULL,
        [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        [ngay_xu_ly] DATETIME2 NULL,
        CONSTRAINT [fk_rut_tien_giang_vien] FOREIGN KEY ([giang_vien_id]) REFERENCES [ho_so_giang_vien] ([id])
    );
END
GO

-- 20. BẢNG LỊCH SỬ VÍ TIỀN (Sổ cái ghi nhận mọi biến động tiền của giảng viên)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='lich_su_vi_tien' AND xtype='U')
BEGIN
    CREATE TABLE [lich_su_vi_tien] (
        [id] BIGINT IDENTITY(1,1) PRIMARY KEY,
        [giang_vien_id] BIGINT NOT NULL,
        [so_tien_bien_dong] DECIMAL(15, 2) NOT NULL,
        [so_du_truoc] DECIMAL(15, 2) NOT NULL,
        [so_du_sau] DECIMAL(15, 2) NOT NULL,
        [loai_giao_dich] NVARCHAR(40) NOT NULL CHECK ([loai_giao_dich] IN ('TIEN_BAN_KHOA_HOC', 'TIEN_BAN_LOP_HOC', 'RUT_TIEN_VE_NGAN_HANG', 'HOAN_TIEN', 'DIEU_CHINH_HE_THONG')),
        [ma_tham_chieu] BIGINT NULL,
        [mo_ta_giao_dich] NVARCHAR(255) NULL,
        [ngay_tao] DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT [fk_vi_tien_giang_vien] FOREIGN KEY ([giang_vien_id]) REFERENCES [ho_so_giang_vien] ([id]) ON DELETE CASCADE
    );
END
GO

-- 21. BẢNG CẤU HÌNH HỆ THỐNG (Cấu hình sàn cho Admin)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='cau_hinh_he_thong' AND xtype='U')
BEGIN
    CREATE TABLE [cau_hinh_he_thong] (
        [ma_cau_hinh] NVARCHAR(100) PRIMARY KEY,
        [gia_tri_cau_hinh] NVARCHAR(MAX) NOT NULL,
        [mo_ta] NVARCHAR(255) NULL
    );
END
GO

-- ==============================================================================
-- DỮ LIỆU BAN ĐẦU (SEED DATA CHO SQL SERVER)
-- ==============================================================================
SET IDENTITY_INSERT [vai_tro] ON;
INSERT INTO [vai_tro] ([id], [ten_vai_tro], [mo_ta]) VALUES
(1, 'ROLE_HOC_VIEN', N'Vai trò học viên / khách đăng ký tài khoản'),
(2, 'ROLE_GIANG_VIEN', N'Vai trò giảng viên / gia sư tham gia dạy học'),
(3, 'ROLE_QUAN_TRI', N'Vai trò quản trị viên tối cao quản lý hệ thống');
SET IDENTITY_INSERT [vai_tro] OFF;
GO

SET IDENTITY_INSERT [danh_muc] ON;
INSERT INTO [danh_muc] ([id], [ten_danh_muc], [duong_dan_slug], [danh_muc_cha_id], [loai_danh_muc], [thu_tu_hien_thi], [kich_hoat]) VALUES
(1, N'Cấp 1 (Tiểu học)', 'cap-1', NULL, 'CAP_HOC_K12', 1, 1),
(2, N'Toán Lớp 5', 'toan-lop-5', 1, 'MON_HOC', 1, 1),
(3, N'Tiếng Việt Lớp 5', 'tieng-viet-lop-5', 1, 'MON_HOC', 2, 1),
(4, N'Cấp 2 (THCS)', 'cap-2', NULL, 'CAP_HOC_K12', 2, 1),
(5, N'Toán Lớp 9 Ôn Thi Vào 10', 'toan-lop-9-vao-10', 4, 'MON_HOC', 1, 1),
(6, N'Tiếng Anh Lớp 9', 'tieng-anh-lop-9', 4, 'MON_HOC', 2, 1),
(7, N'Cấp 3 (THPT)', 'cap-3', NULL, 'CAP_HOC_K12', 3, 1),
(8, N'Toán 12 - Luyện Thi THPT QG', 'toan-12-thpt-qg', 7, 'MON_HOC', 1, 1),
(9, N'Vật Lý 12', 'vat-ly-12', 7, 'MON_HOC', 2, 1),
(10, N'Hóa Học 12', 'hoa-hoc-12', 7, 'MON_HOC', 3, 1),
(11, N'Luyện Thi IELTS', 'luyen-thi-ielts', NULL, 'CHUNG_CHI_NGOAI_NGU', 4, 1),
(12, N'IELTS Cấp Tốc Target 6.5+', 'ielts-target-6-5', 11, 'MON_HOC', 1, 1),
(13, N'IELTS Speaking Master', 'ielts-speaking-master', 11, 'MON_HOC', 2, 1),
(14, N'Luyện Thi TOEIC', 'luyen-thi-toeic', NULL, 'CHUNG_CHI_NGOAI_NGU', 5, 1),
(15, N'TOEIC 4 Kỹ Năng 750+', 'toeic-4-ky-nang-750', 14, 'MON_HOC', 1, 1);
SET IDENTITY_INSERT [danh_muc] OFF;
GO

SET IDENTITY_INSERT [banner_quang_cao] ON;
INSERT INTO [banner_quang_cao] ([id], [tieu_de], [link_anh_banner], [link_chuyen_huong], [thu_tu_hien_thi], [kich_hoat]) VALUES
(1, N'Khai giảng Lớp Luyện Thi THPT QG 2026', 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=1200&q=80', '/khoa-hoc/toan-12-thpt-qg', 1, 1),
(2, N'Khóa Học IELTS 7.5+ Cùng Giảng Viên Chuyên Nghiệp', 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=1200&q=80', '/khoa-hoc/ielts-target-6-5', 2, 1);
SET IDENTITY_INSERT [banner_quang_cao] OFF;
GO

INSERT INTO [cau_hinh_he_thong] ([ma_cau_hinh], [gia_tri_cau_hinh], [mo_ta]) VALUES
('PHAN_TRAM_HOA_HONG_SAN', '15', N'Tỷ lệ % hoa hồng sàn giữ lại khi giảng viên bán khóa học / lớp học (VD: 15%)'),
('SO_TIEN_RUT_TOI_THIEU', '200000', N'Số tiền tối thiểu giảng viên được tạo lệnh rút tiền (VND)'),
('HOTLINE_HO_TRO', '1900 6868', N'Số điện thoại đường dây nóng hỗ trợ học viên và giảng viên');
GO

# 🗺️ KẾ HOẠCH TRIỂN KHAI GIAO DIỆN & API (THEO TỪNG TRANG)
> **Dự án:** Nền tảng kết nối học tập & gia sư trực tuyến - **StudyWithMe**  
> **Nguyên tắc ưu tiên:** Triển khai trước **Phân hệ Quản trị viên (Admin CMS)** và **Phân hệ Trang chủ / Khách vãng lai (Home & Public Pages)**.

---

## 📑 MỤC LỤC LỘ TRÌNH TRIỂN KHAI

* [GIAI ĐOẠN 1: NỀN TẢNG DỮ LIỆU & BẢO MẬT (Chuẩn bị chung)](#-giai-đoạn-1-nền-tảng-dữ-liệu--bảo-mật-bắt-buộc-làm-đầu-tiên)
* [GIAI ĐOẠN 2: PHÂN HỆ QUẢN TRỊ VIÊN (ADMIN CMS)](#-giai-đoạn-2-phân-hệ-quản-trị-viên-admin-cms-ưu-tiên-số-1)
  * [Trang A1: Admin Dashboard (Tổng quan & Thống kê)](#trang-a1-admin-dashboard-tổng-quan--thống-kê)
  * [Trang A2: Quản lý Banner Trang chủ (CMS Banner)](#trang-a2-quản-lý-banner-trang-chủ-cms-banner)
  * [Trang A3: Quản lý Cây Danh mục & Cấp học (Category Management)](#trang-a3-quản-lý-cây-danh-mục--cấp-học-category-management)
  * [Trang A4: Duyệt hồ sơ Giảng viên / Gia sư (Teacher KYC Approval)](#trang-a4-duyệt-hồ-sơ-giảng-viên--gia-sư-teacher-kyc-approval)
  * [Trang A5: Kiểm duyệt Khóa học (Course Moderation)](#trang-a5-kiểm-duyệt-khóa-học-course-moderation)
  * [Trang A6: Quản lý Tài khoản Người dùng (User Management)](#trang-a6-quản-lý-tài-khoản-người-dùng-user-management)
  * [Trang A7: Quản lý Tài chính & Lệnh rút tiền (Payout Approval) - ✅ HOÀN THÀNH 100%](#trang-a7-quản-lý-tài-chính--lệnh-rút-tiền-payout-approval---hoàn-thành-100)
* [GIAI ĐOẠN 3: PHÂN HỆ TRANG CHỦ & TRUYỀN THÔNG (HOME & PUBLIC PAGES)](#-giai-đoạn-3-phân-hệ-trang-chủ--khách-vãng-lai-home--public-ưu-tiên-số-2)
  * [Trang H1: Trang chủ (Home Page)](#trang-h1-trang-chủ-home-page)
  * [Trang H2: Trang Khám phá & Danh sách Khóa học (Course Catalog)](#trang-h2-trang-khám-phá--danh-sách-khóa-học-course-catalog)
  * [Trang H3: Trang Chi tiết Khóa học (Course Detail)](#trang-h3-trang-chi-tiết-khóa-học-course-detail)
  * [Trang H4: Trang Danh sách Gia sư / Giảng viên (Tutor Directory)](#trang-h4-trang-danh-sách-gia-sư--giảng-viên-tutor-directory)
  * [Trang H5: Trang Chi tiết Hồ sơ Giảng viên (Tutor Public Profile)](#trang-h5-trang-chi-tiết-hồ-sơ-giảng-viên-tutor-public-profile)
  * [Trang H6: Trang Đăng ký / Đăng nhập / Quên mật khẩu (Auth Portal) - ✅ HOÀN THÀNH 100%](#trang-h6-trang-đăng-ký--đăng-nhập--quên-mật-khẩu-auth-portal---hoàn-thành-100)
* [GIAI ĐOẠN 4: PHÂN HỆ HỌC VIÊN (STUDENT PORTAL)](#-giai-đoạn-4-phân-hệ-học-viên-student-portal)
* [GIAI ĐOẠN 5: PHÂN HỆ GIẢNG VIÊN (TEACHER STUDIO)](#-giai-đoạn-5-phân-hệ-giảng-viên-teacher-studio)
* [GIAI ĐOẠN 6: CHAT REAL-TIME, BẢO VỆ ĐỒ ÁN & TỐI ƯU](#-giai-đoạn-6-chat-real-time-bảo-vệ-đồ-án--tối-ưu)

---

## 🛠️ GIAI ĐOẠN 1: NỀN TẢNG DỮ LIỆU & BẢO MẬT (Bắt buộc làm đầu tiên)

Trước khi dựng giao diện cho Admin hay Trang chủ, Backend cần có sẵn hạ tầng Repositories, Security và JWT để lưu và trả dữ liệu.

- [x] **Bước 1.1: Tạo 18 Spring Data JPA Repositories** trong package `com.studywithme.repository`:
  - `NguoiDungRepository`, `VaiTroRepository`, `BannerQuangCaoRepository`, `DanhMucRepository`, `KhoaHocRepository`, `ChuongHocRepository`, `BaiHocRepository`, `HoSoGiangVienRepository`, `ChungChiGiangVienRepository`, `DanhGiaNhanXetRepository`, `DonHangRepository`, `ChiTietDonHangRepository`, `GhiDanhKhoaHocRepository`, `DangKyLopHocRepository`, `LopHocTrucTuyenRepository`, `DanhSachYeuThichRepository`, `YeuCauRutTienRepository`, `LichSuViTienRepository`.
- [x] **Bước 1.2: Thiết lập Security & JWT Authentication**:
  - `JwtTokenProvider`: Ký và giải mã token JWT (Payload gồm `userId`, `email`, danh sách quyền `ROLE_QUAN_TRI`, `ROLE_GIANG_VIEN`, `ROLE_HOC_VIEN`).
  - `JwtAuthenticationFilter`: Đọc token từ header `Authorization: Bearer <token>`.
  - `SecurityConfig`: Phân chia vùng truy cập:
    - `/api/public/**`: Cho phép tự do không cần đăng nhập.
    - `/api/admin/**`: Chỉ người có quyền `ROLE_QUAN_TRI`.
    - `/api/giang-vien/**`: Quyền `ROLE_GIANG_VIEN`.
    - `/api/hoc-vien/**`: Quyền `ROLE_HOC_VIEN`.
- [x] **Bước 1.3: Data Seeder khởi tạo dữ liệu mẫu ban đầu**:
  - Tạo sẵn tài khoản Admin mặc định: `admin@studywithme.vn` / `Admin@123456`.
  - Tạo 3 vai trò: `ROLE_QUAN_TRI`, `ROLE_GIANG_VIEN`, `ROLE_HOC_VIEN`.

---

## 👑 GIAI ĐOẠN 2: PHÂN HỆ QUẢN TRỊ VIÊN (ADMIN CMS) (Ưu tiên số 1)

> **Mục tiêu:** Xây dựng trung tâm điều khiển để tạo dữ liệu cho hệ thống (đăng Banner, tạo Danh mục môn học, tạo tài khoản thử nghiệm) giúp Trang chủ có dữ liệu phong phú để hiển thị.

---

### Trang A1: Admin Dashboard (Tổng quan & Thống kê)
* **Mục đích:** Màn hình đầu tiên khi Admin đăng nhập, nắm bắt toàn bộ sức khỏe nền tảng.
* **Đường dẫn Frontend dự kiến:** `/admin/dashboard`
* **Giao diện gồm:**
  - 4 Thẻ KPI trên cùng: Tổng doanh thu (VNĐ), Tổng người dùng, Tổng khóa học đang hoạt động, Lệnh rút tiền / Hồ sơ giảng viên đang chờ duyệt.
  - Biểu đồ đường (Line chart): Doanh thu theo tháng trong năm.
  - Biểu đồ tròn (Pie chart): Tỷ lệ học viên Cấp 1, Cấp 2, Cấp 3 và Ngoại ngữ (IELTS/TOEIC).
  - Bảng nhỏ: Top 5 khóa học bán chạy nhất & 5 hồ sơ giảng viên mới nộp gần nhất.
* **APIs Backend cần viết:**
  - `GET /api/admin/thong-ke/tong-quan`: Trả về số liệu 4 thẻ KPI.
  - `GET /api/admin/thong-ke/doanh-thu?nam=2026`: Trả về mảng doanh thu 12 tháng.
  - `GET /api/admin/thong-ke/ty-le-danh-muc`: Trả về tỷ lệ đăng ký theo từng nhóm môn học.

---

### Trang A2: Quản lý Banner Trang chủ (CMS Banner)
* **Mục đích:** Quản trị hình ảnh quảng cáo, sự kiện và chương trình khuyến mãi sẽ chạy ở Trang chủ.
* **Đường dẫn Frontend dự kiến:** `/admin/banners`
* **Giao diện gồm:**
  - Bảng danh sách Banner: Hình ảnh thumbnail, tiêu đề, link chuyển hướng, thứ tự hiển thị, công tắc Bật/Tắt (Toggle Active).
  - Nút "Thêm banner mới" mở Modal Popup:
    - Ô chọn/tải ảnh lên (Upload ảnh).
    - Ô nhập Tiêu đề, Link chuyển hướng khi click, Thứ tự sắp xếp.
  - Nút Sửa, Nút Xóa (kèm hộp thoại xác nhận).
* **APIs Backend cần viết:**
  - `GET /api/admin/banners`: Danh sách toàn bộ banner (kể cả đang ẩn).
  - `POST /api/admin/banners`: Thêm mới banner (hỗ trợ upload ảnh hoặc URL ảnh).
  - `PUT /api/admin/banners/{id}`: Cập nhật thông tin banner.
  - `PATCH /api/admin/banners/{id}/trang-thai`: Bật/Tắt trạng thái kích hoạt hiển thị.
  - `DELETE /api/admin/banners/{id}`: Xóa banner.
* **Entity liên quan:** `BannerQuangCao.java`.

---

### Trang A3: Quản lý Cây Danh mục & Cấp học (Category Management) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Tạo cơ cấu môn học giúp phân loại khóa học và lớp gia sư (Toán, Lý, Hóa Cấp 1-3, Luyện thi IELTS, TOEIC).
* **Đường dẫn Frontend:** `/admin/danh-muc`
* **Giao diện gồm:**
  - Bảng dạng cây (Tree View) nhóm theo Cấp học:
    - Cấp 1 (Tiểu học: Lớp 1 -> Lớp 5)
    - Cấp 2 (THCS: Lớp 6 -> Lớp 9)
    - Cấp 3 (THPT: Lớp 10 -> Lớp 12, Ôn thi THPT Quốc Gia)
    - Chứng chỉ quốc tế (IELTS Academic, General, TOEIC 4 kỹ năng)
  - 4 Thẻ KPI: Tổng nhóm gốc, Tổng môn học con, Đang kích hoạt, Khóa & lớp học liên kết.
  - Thao tác: Thêm danh mục cha/con, sửa tên, đổi biểu tượng (icon), sắp xếp thứ tự hiển thị, bật/tắt kích hoạt, xóa an toàn (ràng buộc khóa ngoại).
* **APIs Backend đã viết:**
  - `GET /api/admin/danh-muc`: Lấy toàn bộ cây danh mục cha-con.
  - `GET /api/admin/danh-muc/phang`: Lấy danh sách danh mục phẳng cho dropdown.
  - `GET /api/admin/danh-muc/{id}`: Lấy chi tiết danh mục.
  - `POST /api/admin/danh-muc`: Tạo danh mục mới.
  - `PUT /api/admin/danh-muc/{id}`: Cập nhật danh mục.
  - `PATCH /api/admin/danh-muc/{id}/trang-thai`: Đổi trạng thái kích hoạt.
  - `DELETE /api/admin/danh-muc/{id}`: Xóa danh mục (kiểm tra ràng buộc con & khóa học liên kết).
  - `GET /api/admin/danh-muc/thong-ke`: Thống kê nhanh.
* **Entity liên quan:** `DanhMuc.java`.

---

### Trang A4: Duyệt hồ sơ Giảng viên / Gia sư (Teacher KYC Approval) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Đảm bảo chất lượng đội ngũ giảng dạy trước khi cho phép mở lớp hay bán khóa học.
* **Đường dẫn Frontend:** `/admin/duyet-gia-su` (hoặc `/admin/tutors`)
* **Giao diện gồm:**
  - Bộ lọc tab: `Chờ duyệt (CHO_DUYET)`, `Đã duyệt (DA_DUYET)`, `Từ chối (TU_CHOI)`.
  - 4 Thẻ KPI: Hồ sơ chờ phê duyệt, Gia sư chính thức, Hồ sơ không đạt, Tổng hồ sơ nộp.
  - Danh sách thẻ ứng viên chờ duyệt: Avatar, tên, chuyên môn, tự giới thiệu, tags chứng chỉ đã nộp, link video dạy thử demo.
  - Modal xem chi tiết hồ sơ:
    - Bằng cấp sư phạm, chứng chỉ IELTS/TOEIC (kèm Lightbox phóng to xem ảnh chứng chỉ).
    - Trình xem video dạy thử demo (YouTube embed).
    - Thông tin tài khoản ngân hàng nhận thù lao (Ngân hàng, STK, Chủ tài khoản).
  - Modal Từ chối: Nhập lý do từ chối gửi về ứng viên.
  - Nút Phê duyệt: Tự động cấp vai trò `ROLE_GIANG_VIEN` và xác thực toàn bộ chứng chỉ.
* **APIs Backend đã viết ([AdminGiangVienApiController.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/java/com/studywithme/controller/api/admin/AdminGiangVienApiController.java)):**
  - `GET /api/admin/giang-vien/yeu-cau-duyet?trangThai=CHO_DUYET`: Danh sách hồ sơ lọc theo trạng thái.
  - `GET /api/admin/giang-vien/{id}/chi-tiet`: Xem toàn bộ thông tin bằng cấp, chứng chỉ.
  - `POST /api/admin/giang-vien/{id}/phe-duyet`: Chấp thuận và nâng quyền tài khoản lên `ROLE_GIANG_VIEN`.
  - `POST /api/admin/giang-vien/{id}/tu-choi`: Từ chối và lưu lại lý do.
  - `GET /api/admin/giang-vien/thong-ke`: Thống kê số lượng hồ sơ KYC.
* **Entities liên quan:** `HoSoGiangVien.java`, `ChungChiGiangVien.java`, `NguoiDung.java`, `VaiTro.java`.
* **Kiểm thử tích hợp:** [TeacherKycIntegrationTest.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/test/java/com/studywithme/TeacherKycIntegrationTest.java).

---

### Trang A5: Kiểm duyệt Khóa học (Course Moderation) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Thẩm định chất lượng video bài giảng, tính hoàn thiện của đề cương và kiểm tra bản quyền trước khi cho phép xuất bản lên sàn StudyWithMe.
* **Đường dẫn Frontend:** `/admin/khoa-hoc/kiem-duyet` (hoặc `/admin/courses/moderation`)
* **Giao diện gồm:**
  - 4 Thẻ KPI: Khóa học chờ duyệt, Đã xuất bản (Công khai), Bị từ chối (Cần sửa), Tổng số khóa học.
  - Bộ lọc tabs: `Chờ kiểm duyệt`, `Đã xuất bản`, `Bị từ chối` kèm số lượng badge tương ứng.
  - Lưới thẻ khóa học: Thumbnail, phân loại cấp học, môn học, giảng viên, giá niêm yết/khuyến mãi, số chương & bài học.
  - Modal xem chi tiết đề cương & bài học:
    - Danh sách chương/bài học dạng accordion chi tiết.
    - Trình phát video bài giảng xem thử (YouTube iframe / video player embed) tích hợp trực tiếp trong modal để kiểm tra chất lượng hình ảnh & âm thanh.
    - Thao tác phê duyệt xuất bản ngay trong modal.
  - Modal Từ chối khóa học:
    - Các chip gợi ý lý do nhanh (Video mờ, Âm thanh rè, Đề cương chưa đủ, Vi phạm bản quyền).
    - Textarea nhập nội dung chi tiết phản hồi về cho giảng viên.
* **APIs Backend đã viết ([AdminKhoaHocApiController.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/java/com/studywithme/controller/api/admin/AdminKhoaHocApiController.java)):**
  - `GET /api/admin/khoa-hoc/danh-sach?trangThai=CHO_DUYET`: Danh sách khóa học lọc theo trạng thái.
  - `GET /api/admin/khoa-hoc/{id}/chi-tiet`: Chi tiết đề cương giáo trình, chương & bài học.
  - `POST /api/admin/khoa-hoc/{id}/phe-duyet`: Duyệt xuất bản khóa học (`DA_XUAT_BAN`).
  - `POST /api/admin/khoa-hoc/{id}/tu-choi`: Từ chối khóa học (`TU_CHOI`) kèm lưu lý do.
  - `GET /api/admin/khoa-hoc/thong-ke`: Thống kê số lượng khóa học theo trạng thái.
* **Entities liên quan:** `KhoaHoc.java`, `ChuongHoc.java`, `BaiHoc.java`, `HoSoGiangVien.java`, `DanhMuc.java`.
* **Kiểm thử tích hợp:** [CourseModerationIntegrationTest.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/test/java/com/studywithme/CourseModerationIntegrationTest.java).

---

### Trang A6: Quản lý Tài khoản Người dùng (User Management) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Quản lý toàn bộ học viên, giảng viên và nhân sự vận hành trên nền tảng.
* **Đường dẫn Frontend:** `/admin/nguoi-dung`
* **Giao diện gồm:**
  - 3 Thẻ KPI: Tổng số tài khoản, Đang hoạt động, Đang tạm khóa.
  - Thanh tìm kiếm real-time lọc tức thì theo Họ tên, Email, Vai trò.
  - Bảng danh sách chi tiết: ID, Họ tên, Email, Số điện thoại, Vai trò (badge màu tím Admin, xanh ngọc Gia sư, xanh dương Học viên), Trạng thái (Hoạt động xanh / Tạm khóa đỏ), Ngày tạo.
  - Thao tác: Nút Khóa tài khoản / Mở khóa nhanh (Toggle Ban/Unban) kèm hộp thoại xác nhận.
* **Controller Backend đã viết ([AdminWebController.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/java/com/studywithme/controller/web/AdminWebController.java)):**
  - `GET /admin/nguoi-dung`: Nạp toàn bộ danh sách người dùng và thống kê.
  - `POST /admin/doi-trang-thai-nguoi-dung/{id}`: Đổi trạng thái hoạt động / tạm khóa.
* **Entities liên quan:** `NguoiDung.java`, `VaiTro.java`.

---

### Trang A7: Quản lý Tài chính & Lệnh rút tiền (Payout Approval) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Kiểm tra và xử lý các lệnh yêu cầu rút tiền từ số dư ví của giảng viên, xác nhận chuyển khoản và tự động hoàn tiền vào ví nếu từ chối.
* **Đường dẫn Frontend:** `/admin/tai-chinh/rut-tien` (hoặc `/admin/payouts`)
* **Giao diện gồm:**
  - 4 Thẻ KPI: Tổng tiền chờ giải ngân, Lệnh chờ xử lý, Đã giải ngân thành công, Lệnh bị từ chối.
  - Bộ lọc tabs: `Chờ xử lý (CHO_XU_LY)`, `Đã chuyển tiền (DA_CHUYEN_TIEN)`, `Bị từ chối (TU_CHOI)` kèm badge đếm số lượng.
  - Bảng lệnh rút tiền: Mã yêu cầu `#YC-ID`, tên & avatar giảng viên, email/sđt, số tiền muốn rút (VNĐ nổi bật), thông tin ngân hàng (logo, tên ngân hàng, STK, tên chủ tài khoản), số dư ví còn lại, ngày tạo.
  - Modal Xác nhận Chuyển tiền: Xem tóm tắt thông tin thụ hưởng, nhập mã biên lai giao dịch ngân hàng / ghi chú chuyển khoản.
  - Modal Từ chối: Các chip gợi ý lý do nhanh (Sai STK, Không khớp chủ thẻ, Ngân hàng bảo trì), textarea nhập lý do, tự động hoàn trả 100% số tiền về ví giảng viên.
* **APIs Backend đã viết ([AdminTaiChinhApiController.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/java/com/studywithme/controller/api/admin/AdminTaiChinhApiController.java)):**
  - `GET /api/admin/tai-chinh/yeu-cau-rut-tien`: Danh sách các lệnh rút tiền lọc theo trạng thái (`CHO_XU_LY`, `DA_CHUYEN_TIEN`, `TU_CHOI`).
  - `GET /api/admin/tai-chinh/thong-ke`: Thống kê tài chính & số lệnh theo từng trạng thái.
  - `GET /api/admin/tai-chinh/yeu-cau-rut-tien/{id}`: Xem chi tiết lệnh rút tiền.
  - `POST /api/admin/tai-chinh/yeu-cau-rut-tien/{id}/xac-nhan`: Xác nhận giải ngân chuyển tiền thành công (`DA_CHUYEN_TIEN`) và ghi nhận vào sổ cái ví (`LichSuViTien`).
  - `POST /api/admin/tai-chinh/yeu-cau-rut-tien/{id}/tu-choi`: Từ chối lệnh (`TU_CHOI`), hoàn tiền vào ví giảng viên (`soDuVi = soDuVi + soTienRut`) và ghi sổ cái `HOAN_TIEN`.
* **Entities liên quan:** `YeuCauRutTien.java`, `LichSuViTien.java`, `HoSoGiangVien.java`.
* **Kiểm thử tích hợp:** [PayoutApprovalIntegrationTest.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/test/java/com/studywithme/PayoutApprovalIntegrationTest.java) (6/6 tests passed).

---

## 🌟 GIAI ĐOẠN 3: PHÂN HỆ TRANG CHỦ & KHÁCH VÃNG LAI (HOME & PUBLIC) (Ưu tiên số 2)

> **Mục tiêu:** Xây dựng bộ mặt của nền tảng, nơi học viên và phụ huynh tiếp cận đầu tiên để tìm kiếm khóa học và gia sư uy tín.

---

### Trang H1: Trang chủ (Home Page) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Giới thiệu nền tảng, hiển thị banner chiến dịch, danh mục môn học và các khóa học / gia sư nổi bật.
* **Đường dẫn Frontend:** `/`
* **Giao diện gồm các khối (Sections):**
  1. **Header / Navbar**: Logo StudyWithMe, Menu điều hướng Cấp học & Chứng chỉ, Nhận diện phiên đăng nhập (Role Badge, Bàn học học viên, Cổng giảng viên, Quản trị).
  2. **Hero Banner Slider (`banner-slider.html`)**: Hiển thị danh sách banner đang bật từ CMS Banner của Admin (`BannerService.layBannerDangKichHoat()`), tự động chuyển slide mỗi 5s, pause khi rê chuột, nút mũi tên hiệu ứng kính mờ (glassmorphism), thanh dot indicator.
  3. **Smart Tutor Finder (`hero-section.html`)**: Bộ tìm kiếm gia sư 1-kèm-1 theo cấp lớp, môn học và hình thức học.
  4. **Khối Danh mục & Cấp học (`programs-section.html`)**: Phân bổ môn học trực quan (Tiểu học, THCS, THPT, Chứng chỉ quốc tế).
  5. **Khối Khóa học bán chạy & Nổi bật (`courses-section.html`)**: Lưới 8 khóa học hot nhất lấy từ DB, bộ lọc tab tức thì theo cấp (Tất cả, THPT, THCS, Tiểu học, IELTS & TOEIC), badge giảm giá, số sao, giảng viên, số học viên và nút xem khóa học.
  6. **Khối Giảng viên / Gia sư tiêu biểu (`tutors-section.html`)**: Hiển thị 4 gia sư tiêu biểu đã qua kiểm định chất lượng, hiển thị ảnh avatar, môn dạy, số sao 5.0.
  7. **Khối Đặc điểm nổi bật (`why-us-section.html`)**: 4 giá trị cốt lõi của nền tảng StudyWithMe.
  8. **Footer (`footer.html`)**: Thông tin liên hệ, giấy phép giáo dục, mạng xã hội.
* **APIs Backend đã viết (Public - Không cần Token):**
  - `GET /api/public/banners`: Lấy danh sách banner kích hoạt (`kichHoat = true`), sắp xếp theo thứ tự hiển thị.
  - `GET /api/public/danh-muc/noi-bat`: Lấy danh sách danh mục chính.
  - `GET /api/public/khoa-hoc/noi-bat`: Lấy top 8 khóa học nhiều học viên đăng ký nhất (`TopKhoaHocDto`).
  - `GET /api/public/giang-vien/tieu-bieu`: Lấy danh sách 4 gia sư tiêu biểu đã duyệt.
* **Dữ liệu mẫu (DataSeeder):** Khởi tạo 4 hồ sơ gia sư uy tín và 8 khóa học bản quyền phân bổ mọi cấp học kèm chương/bài học thử.

---

### Trang H2: Trang Khám phá & Danh sách Khóa học (Course Catalog) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Nơi học viên tìm kiếm và lọc khóa học theo nhu cầu cá nhân.
* **Đường dẫn Frontend:** `/khoa-hoc`
* **Giao diện gồm:**
  - Hero banner với quick filter chips phân loại cấp học (Tất cả, Tiểu học, THCS, THPT, IELTS, TOEIC).
  - Cột bộ lọc bên trái (Sidebar Filter): Lọc theo phân loại cấp học, môn học, khoảng giá tiền, đánh giá số sao.
  - Thanh tìm kiếm từ khóa, đếm số lượng kết quả và dropdown sắp xếp (Mới nhất, Giá tăng dần, Giá giảm dần, Học viên đông nhất, Đánh giá cao nhất).
  - Lưới thẻ khóa học responsive hiển thị ảnh đại diện, badge giảm giá, tên khóa học, giảng viên, số sao, số học viên và nút Xem khóa học.
  - Phân trang chuẩn (Pagination: 12 khóa học/trang).
* **APIs & Controller Backend đã viết:**
  - `GET /khoa-hoc`: `KhoaHocWebController` kết nối `KhoaHocService` và render `courses/index.html`.
  - `GET /api/public/khoa-hoc`: REST API tìm kiếm và lọc đa tiêu chí phân trang.

---

### Trang H3: Trang Chi tiết Khóa học (Course Detail) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Cung cấp đầy đủ thông tin để học viên quyết định mua khóa học.
* **Đường dẫn Frontend:** `/khoa-hoc/{slug}`
* **Giao diện gồm:**
  - Tiêu đề khóa học, mô tả chi tiết, số lượng học viên đã mua, số sao đánh giá trung bình.
  - Thẻ giá bên phải (Sticky Pricing Card): Giá bán, giá khuyến mãi, tỷ lệ giảm giá, nút "Đăng Ký Học Ngay", danh sách quyền lợi bảo hành.
  - **Khung xem thử bài giảng (Free Trial Preview)**: Trình phát video bài học thử YouTube modal/lightbox responsive.
  - **Khung Đề cương chi tiết (Curriculum)**: Accordion danh sách Chương học và Bài học kèm thời lượng từng bài và badge "Học thử miễn phí".
  - **Khối Thông tin Giảng viên**: Avatar, tên, chuyên môn, tiểu sử, số khóa học, số học viên.
  - **Khối Khóa học liên quan**: Gợi ý các khóa học cùng danh mục môn học.
* **APIs & Controller Backend đã viết:**
  - `GET /khoa-hoc/{slug}`: `KhoaHocWebController` nạp đầy đủ EntityGraph chương/bài học và render `courses/detail.html`.

---

### Trang H4: Trang Danh sách Gia sư / Giảng viên (Tutor Directory) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Tìm kiếm gia sư trực tuyến để học kèm 1-1 hoặc học theo nhóm nhỏ.
* **Đường dẫn Frontend:** `/gia-su`
* **Giao diện gồm:**
  - Hero banner kết nối đội ngũ gia sư & thủ khoa hàng đầu với thanh tìm kiếm đa năng.
  - Lọc nhanh theo chứng chỉ: Tất cả, IELTS 7.5 - 8.5+, Tốt nghiệp Sư Phạm & Bách Khoa, Chứng chỉ nghiệp vụ sư phạm.
  - Dropdown sắp xếp: Mới nhất, Đánh giá cao nhất (5.0 ⭐).
  - Lưới thẻ gia sư: Avatar sắc nét, huy hiệu Verified KYC xanh, tiêu đề chuyên môn, đánh giá 5.0 ⭐, tự giới thiệu, danh sách chứng chỉ đã kiểm định, nút "Xem Hồ Sơ & Lớp Học", nút "Xem Video Dạy Thử Demo" modal YouTube.
  - Phân trang chuẩn và trạng thái rỗng (Empty State) thân thiện.
* **APIs & Controller Backend đã viết:**
  - `GET /gia-su`: `GiaSuWebController` kết nối `GiaSuService` và render `tutors/index.html`.
  - `GET /api/public/giang-vien`: REST API tìm kiếm lọc gia sư phân trang.

---

### Trang H5: Trang Chi tiết Hồ sơ Giảng viên (Tutor Public Profile) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Xem năng lực chuyên môn, bằng cấp và các lớp gia sư / khóa học do giảng viên này giảng dạy.
* **Đường dẫn Frontend:** `/gia-su/{id}`
* **Giao diện gồm:**
  - Header hồ sơ giảng viên: Avatar lớn, huy hiệu KYC chính thức, chuyên môn, xếp hạng 5.0 ⭐, thống kê số lớp và học viên.
  - Khối Giới thiệu & Phương pháp giảng dạy chi tiết.
  - Khối Trình phát Video bài giảng dạy thử demo YouTube.
  - Khối Bằng cấp & Chứng chỉ quốc tế đã kiểm định kèm Lightbox phóng to ảnh chứng chỉ.
  - Khối Lớp học trực tuyến đang mở đăng ký tuyển sinh: Lịch học tuần, sĩ số tối đa, học phí, nút mở Modal Đăng ký tư vấn xếp lớp.
  - Khối Khóa học video đóng gói do giảng viên phát hành.
  - Khối Đánh giá & Phản hồi thực tế từ học sinh cũ và phụ huynh.
  - Cột bên phải: Sticky Box học phí, Hotline 1900 6868, cam kết học thử miễn phí và danh sách gia sư cùng chuyên môn tương tự.
* **APIs & Controller Backend đã viết:**
  - `GET /gia-su/{id}`: `GiaSuWebController` nạp đầy đủ thông tin gia sư, lớp trực tuyến, khóa học và render `tutors/detail.html`.
  - `GET /api/public/giang-vien/{id}`: REST API chi tiết hồ sơ gia sư.
  - `GET /api/public/giang-vien/{id}/lop-hoc`: REST API lấy các lớp học trực tuyến đang mở.
  - `GET /api/public/giang-vien/{id}/khoa-hoc`: REST API lấy các khóa học của gia sư.

---

### Trang H6: Trang Đăng ký / Đăng nhập / Quên mật khẩu (Auth Portal) - ✅ HOÀN THÀNH 100%
* **Mục đích:** Xác thực danh tính người dùng, phân quyền vào hệ thống (Quản trị viên, Gia sư, Học viên) và hỗ trợ khôi phục mật khẩu.
* **Đường dẫn Frontend:** `/login` (hoặc `/dang-nhap`), `/register` (hoặc `/dang-ky`)
* **Giao diện gồm:**
  - Form Đăng nhập (`login.html`): Email, Mật khẩu có nút ẩn/hiện, Ghi nhớ đăng nhập, Alert lỗi/thành công, tự động lưu JWT vào `localStorage` và `Cookie` và chuyển hướng theo Role (Admin -> `/admin/dashboard`, Tutor -> `/tutor/dashboard`, Student -> `/student/dashboard`).
  - Modal Quên mật khẩu: Mở popup từ trang đăng nhập, nhập email để gửi mã xác thực khôi phục tài khoản.
  - Form Đăng ký (`register.html`): Chuyển đổi vai trò linh hoạt (Tab Phụ huynh / Học viên hoặc Gia sư), Họ tên, Email, Số điện thoại, Mật khẩu, gán vai trò tự động.
* **APIs & Controller Backend đã viết ([AuthController.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/java/com/studywithme/controller/auth/AuthController.java)):**
  - `POST /api/auth/dang-nhap`: Xác thực email/password -> Cấp JWT accessToken + thông tin người dùng + danh sách vai trò.
  - `POST /api/auth/dang-ky`: Tạo tài khoản mới (gán `ROLE_HOC_VIEN`, nếu chọn làm gia sư sẽ đồng thời tạo hồ sơ `HoSoGiangVien` trạng thái `CHO_DUYET`).
  - `GET /api/auth/me`: Lấy thông tin phiên đăng nhập hiện tại từ JWT.

---

## 🎒 GIAI ĐOẠN 4: PHÂN HỆ HỌC VIÊN (STUDENT PORTAL)
*(Đang triển khai - Đã hoàn thành nền tảng & các trang cốt lõi)*

* **Trang S1: Giỏ hàng & Đặt mua (Cart & Checkout) - [Sẵn sàng tích hợp]**: Đã hoàn thiện cấu hình hạ tầng **VNPAY Sandbox** ([VnpayConfig.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/java/com/studywithme/config/VnpayConfig.java), [VnPayService.java](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/java/com/studywithme/service/VnPayService.java)) với chữ ký bảo mật SHA512, DTOs Cart & Checkout sẵn sàng.
* **Trang S2: Khóa học của tôi (My Enrolled Courses) - ✅ HOÀN THÀNH 100%**: Danh sách khóa học video đã ghi danh ([courses.html](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/resources/templates/student/courses.html)), thanh tiến độ học tập (% completed), liên kết vào bài giảng, tích hợp sidebar và dashboard.
* **Trang S3: Trình phát video học tập (Video Learning Player)**: Xem video bài học, đánh dấu hoàn thành, lưu giây dừng lại (`TienDoBaiHoc`).
* **Trang S4: Lớp học trực tuyến của tôi (My Live Classes) - ✅ HOÀN THÀNH 100%**: Danh sách lớp gia sư kèm trực tiếp, lịch học tuần, nút click mở phòng học ([classes.html](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/resources/templates/student/classes.html)).
* **Trang S5: Danh sách yêu thích (Wishlist) - ✅ HOÀN THÀNH 100%**: Quản lý các khóa học và gia sư đã thả tim để theo dõi ([wishlist.html](file:///c:/Users/Hieu/Documents/đatn/studywithme/src/main/resources/templates/student/wishlist.html)).
* **Trang S6: Viết đánh giá & Nhận xét**: Chấm điểm sao và gửi nhận xét sau khi hoàn thành khóa học.

---

## 👨‍🏫 GIAI ĐOẠN 5: PHÂN HỆ GIẢNG VIÊN (TEACHER STUDIO)
*(Thực hiện sau khi hoàn thành Phân hệ Học viên)*

* **Trang T1: Nộp hồ sơ xét duyệt giảng viên (Teacher KYC Application)**: Tải ảnh bằng cấp, chứng chỉ ngoại ngữ, upload video tự giới thiệu.
* **Trang T2: Studio Quản lý Khóa học (Course Management)**: Tạo khóa học mới, thiết lập giáo trình Chương/Bài học, upload video bài giảng, gửi khóa học chờ Admin duyệt.
* **Trang T3: Quản lý Lớp gia sư dạy kèm (Live Class Management)**: Mở lớp gia sư, thiết lập sĩ số tối đa, thời khóa biểu và học phí.
* **Trang T4: Thống kê Thu nhập & Ví tiền (Wallet & Earnings)**: Biểu đồ doanh thu từ bán khóa học và dạy gia sư, số dư khả dụng, tạo yêu cầu rút tiền về ngân hàng.

---

## 💬 GIAI ĐOẠN 6: CHAT REAL-TIME, BẢO VỆ ĐỒ ÁN & TỐI ƯU

* **Hộp thoại Chat 1-1 (WebSocket STOMP)**: Khung nhắn tin trực tiếp giữa học viên và gia sư để trao đổi bài học.
* **Hệ thống Chuông thông báo (Real-time Notification)**: Báo khi đơn hàng thành công, khi có bài giảng mới, khi hồ sơ được duyệt.
* **Tối ưu hiệu năng**: Tối ưu chỉ mục (Index) trên SQL Server, giải quyết N+1 query trong JPA.
* **Chuẩn bị bảo vệ Đồ án**: Xuất tài liệu Swagger API, vẽ đầy đủ biểu đồ UML (Use Case, Sequence, Class, ERD), slide báo cáo.

---

## 🎯 BẢNG TỔNG HỢP THỨ TỰ BẮT TAY VÀO CODE NGAY

| Bước | Phân hệ | Trang / Công việc cụ thể | File Code / API trọng tâm |
| :---: | :---: | :--- | :--- |
| **1** | Hạ tầng | Tạo 18 Spring Data JPA Repositories | `com.studywithme.repository.*` |
| **2** | Bảo mật | Cấu hình Security, JWT Provider & Filter | `SecurityConfig`, `JwtTokenProvider`, `JwtFilter` |
| **3** | Xác thực | API Đăng nhập, Đăng ký trả JWT token | `AuthController`, `AuthService`, `NguoiDung` |
| **4** | **Admin** | **Trang A2: Quản lý Banner Trang chủ** | `BannerQuangCaoRepository`, `AdminBannerController` |
| **5** | **Admin** | **Trang A3: Quản lý Cây Danh mục môn học** | `DanhMucRepository`, `AdminDanhMucApiController` (Đã hoàn thành 100%) |
| **6** | **Trang chủ** | **Trang H1: Trang chủ (Home Page)** | `HomeWebController`, `PublicApiController` (Đã hoàn thành 100%) |
| **7** | **Trang chủ** | **Trang H2: Danh mục & Tìm kiếm khóa học** | `PublicKhoaHocController` (Lọc đa tiêu chí & Phân trang) |
| **8** | **Trang chủ** | **Trang H3: Chi tiết Khóa học & Đề cương** | `PublicKhoaHocController` (Chi tiết, Bài học thử) |
| **9** | **Admin** | **Trang A4 & A5: Duyệt Hồ sơ Gia sư & Khóa học** | `AdminGiangVienController`, `AdminKhoaHocController` |
| **10** | **Admin** | **Trang A1: Admin Dashboard (Thống kê)** | `AdminThongKeController` |

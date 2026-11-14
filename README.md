# 🎓 StudyWithMe - Nền Tảng Học Trực Tuyến & Kết Nối Gia Sư 1-Kèm-1

<p align="center">
  <img src="src/main/resources/static/images/logo-icon.png" alt="StudyWithMe Logo" width="90" />
</p>

<p align="center">
  <strong>Hệ thống kết nối Gia sư 1-Kèm-1, Lớp học trực tuyến và Khóa học Video theo kiến trúc Dual Database (SQL Server + MongoDB Atlas)</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg" alt="Spring Boot 3.3.4" />
  <img src="https://img.shields.io/badge/SQL%20Server-2022-blue.svg" alt="SQL Server" />
  <img src="https://img.shields.io/badge/MongoDB-Atlas%20Cloud-green.svg" alt="MongoDB Atlas" />
  <img src="https://img.shields.io/badge/Thymeleaf-3.0-red.svg" alt="Thymeleaf" />
  <img src="https://img.shields.io/badge/TailwindCSS-v3-38B2AC.svg" alt="Tailwind CSS" />
  <img src="https://img.shields.io/badge/Payment-PayOS%20VietQR-informational.svg" alt="PayOS VietQR" />
  <img src="https://img.shields.io/badge/Tests-90%2F90%20Passed%20(100%25)-success.svg" alt="Tests" />
</p>

---

## 📖 1. Giới Thiệu Dự Án

**StudyWithMe** là nền tảng công nghệ giáo dục (EdTech) toàn diện được phát triển nhằm giải quyết bài toán kết nối giữa **Học viên/Phụ huynh** và đội ngũ **Gia sư/Giảng viên chất lượng cao**. Hệ thống hỗ trợ đa dạng hình thức học tập: từ học kèm 1-1 theo lịch rảnh linh hoạt, lớp học trực tuyến qua Google Meet/Zoom, cho đến các khóa học video bài giảng tự học 24/7.

Điểm nổi bật của hệ thống là việc ứng dụng kiến trúc **Dual Database (Cơ sở dữ liệu kép)**:
1. **Microsoft SQL Server (RDBMS):** Đảm bảo tính toàn vẹn giao dịch tài chính (ACID), quản lý người dùng (RBAC), hồ sơ chứng chỉ KYC, khóa học, đơn hàng, ví tiền và hợp đồng ký quỹ Escrow.
2. **MongoDB Atlas Cloud (NoSQL):** Tối ưu hóa hiệu năng lưu trữ và truy vấn phi cấu trúc cho phân hệ Chat & Tin nhắn thời gian thực giữa học viên và gia sư.

---

## 🏗️ 2. Kiến Trúc Công Nghệ (Tech Stack)

### Backend
- **Ngôn ngữ & Nền tảng:** Java 21 LTS, Spring Boot 3.3.4
- **Bảo mật:** Spring Security 6, JWT Token & Session Cookie, Phân quyền RBAC (`ADMIN`, `GIANG_VIEN`, `HOC_VIEN`)
- **Tương tác dữ liệu:**
  - `spring-boot-starter-data-jpa` (Hibernate ORM 6.x)
  - `spring-boot-starter-data-mongodb` (Spring Data MongoDB)
  - HikariCP Connection Pool tối ưu hiệu năng
- **Tích hợp thanh toán:** SDK & Webhook tự động **VietQR PayOS**

### Frontend
- **Công nghệ render:** Server-Side Rendering (SSR) với **Thymeleaf 3.0**
- **Giao diện & Trải nghiệm:** Tailwind CSS, Google Material Symbols, Responsive hoàn chỉnh trên Desktop & Mobile thiết bị cầm tay
- **Client Script:** Vanilla JavaScript (ES6+), Fetch API tương tác RESTful

### Cơ Sở Dữ Liệu
- **RDBMS:** Microsoft SQL Server (SQLEXPRESS / Docker SQL Server)
- **NoSQL:** MongoDB Atlas Cloud (Cluster M0 AWS)

---

## ✨ 3. Tính Năng Cốt Lõi Hệ Thống

### 👨‍🎓 Phân Hệ Học Viên & Khách Vãng Lai
- **Khám phá & Bộ lọc:** Tìm kiếm gia sư theo cấp lớp (Lớp 1-12, Đại học), môn chuyên, chứng chỉ (IELTS 7.5+, Tốt nghiệp Sư phạm...).
- **Xem video dạy thử (Demo Lesson):** Xem video phong cách giảng dạy trực quan trước khi đăng ký.
- **Weekly Slot Picker (Đặt ca học 1-1):** Xem ma trận lịch rảnh trong tuần của gia sư, lựa chọn khung giờ và gửi yêu cầu đặt lịch kèm 1-1.
- **Ký quỹ học phí Escrow:** Học phí được sàn đóng băng bảo đảm; gia sư chỉ nhận tiền khi buổi học hoàn thành 100%.
- **MongoDB Real-time Chat:** Nhắn tin tư vấn trực tiếp 1-1 với gia sư để trao đổi đề cương và tài liệu học.
- **Mua & Học khóa học Video:** Thanh toán tự động qua VietQR PayOS, theo dõi tiến độ hoàn thành bài giảng.
- **Ví tiền cá nhân:** Nạp tiền tự động qua QR Code PayOS, kiểm tra lịch sử biến động số dư minh bạch.

### 👩‍🏫 Phân Hệ Giảng Viên & Gia Sư (Tutor Portal)
- **Đăng ký & KYC Hồ sơ:** Nộp thông tin học vị, bằng cấp sư phạm, chứng chỉ quốc tế để Admin thẩm định.
- **Quản lý Lịch Dạy (Weekly Slot Management):** Thiết lập ma trận các khung giờ rảnh hàng tuần (Online 1-1, Offline), định giá học phí/giờ, tạm khóa/mở lại slot.
- **Điều phối Ca Học 1-1:** Tiếp nhận yêu cầu đặt ca học, duyệt hoặc từ chối có lý do, cập nhật link phòng học Google Meet, xác nhận hoàn tất ca học để nhận giải ngân vào ví.
- **Course Studio:** Tạo và xuất bản khóa học video cá nhân, quản lý đề cương chương/bài học, gắn video bài giảng.
- **Ví tiền gia sư:** Quản lý doanh thu từ khóa học và tiền dạy kèm 1-1, tạo lệnh yêu cầu rút tiền về tài khoản ngân hàng cá nhân.

### 🛡️ Phân Hệ Quản Trị Viên (Admin Dashboard)
- **Thống kê & Báo cáo:** Tổng quan doanh thu hệ thống, phí sàn thu được, biểu đồ tăng trưởng học viên và khóa học.
- **Kiểm duyệt hồ sơ KYC Gia sư:** Xem ảnh bằng cấp, đối soát thông tin và phê duyệt/từ chối cấp quyền giảng dạy.
- **Kiểm duyệt Khóa học:** Xem xét nội dung, video demo và phê duyệt khóa học trước khi mở bán công khai.
- **Duyệt lệnh rút tiền (Payout Approval):** Kiểm tra số dư ví gia sư, đối soát giao dịch và phê duyệt giải ngân tiền về ngân hàng.

---

## 📁 4. Cấu Trúc Thư Mục Dự Án

```
studywithme/
├── src/
│   ├── main/
│   │   ├── java/com/studywithme/
│   │   │   ├── chat/                        # MongoDB Document Entities (CuocTroChuyenMongo, TinNhanMongo)
│   │   │   ├── config/                      # Cấu hình Spring Security, JPA/Mongo Repository, PayOS
│   │   │   ├── controller/
│   │   │   │   ├── api/                     # REST API Controllers (Chat, Đặt lịch 1-1, Lịch dạy, PayOS...)
│   │   │   │   └── web/                     # Web MVC Controllers (Home, GiaSu, KhoaHoc, Admin, Tutor...)
│   │   │   ├── dto/                         # Data Transfer Objects (Chat, Tutor, Booking, Course...)
│   │   │   ├── entity/                      # SQL Server JPA Entities (NguoiDung, GiangVien, DatLich1k1...)
│   │   │   │   └── enums/                   # Các enum nghiệp vụ (ThuTrongTuan, TrangThaiCaHoc...)
│   │   │   ├── repository/
│   │   │   │   ├── jpa/                     # 22 JPA Repositories (SQL Server)
│   │   │   │   └── mongo/                   # 2 Mongo Repositories (MongoDB Atlas)
│   │   │   └── service/                     # Service Interfaces & Implementations
│   │   └── resources/
│   │       ├── static/                      # CSS, JS, Images, Logo
│   │       ├── templates/                   # Thymeleaf HTML Templates
│   │       │   ├── admin/                   # Giao diện Quản trị viên
│   │       │   ├── components/              # Các component dùng chung (header, footer, sections)
│   │       │   ├── tutor/                   # Giao diện Cổng giảng viên (Lịch dạy, Ca học, Studio...)
│   │       │   ├── tutors/                  # Danh sách & Chi tiết hồ sơ gia sư
│   │       │   └── ...
│   │       └── application.properties       # Cấu hình Datasource, MongoDB, PayOS, Port
│   └── test/java/com/studywithme/           # 15 Test Suites kiểm thử tự động toàn diện
├── build.gradle                             # Cấu hình thư viện phụ thuộc Gradle
└── README.md
```

---

## 🚀 5. Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### 5.1. Yêu Cầu Môi Trường
- **Java Development Kit (JDK):** Phiên bản 21 trở lên.
- **Cơ sở dữ liệu SQL Server:** SQL Server 2019/2022 hoặc SQL Server Express đang chạy tại cổng mặc định `1433`.
- **Cơ sở dữ liệu MongoDB:** Kết nối trực tiếp tới cụm **MongoDB Atlas Cloud** (đã được cấu hình sẵn trong project).

### 5.2. Cấu Hình `application.properties`
Dự án đã chuẩn bị sẵn file mẫu [application.properties.example](file:///src/main/resources/application.properties.example).
Bạn sao chép thành `src/main/resources/application.properties` và cấu hình các thông số phù hợp:

- **1. Microsoft SQL Server:**
  - Cập nhật thông tin `spring.datasource.url`, `username`, `password` tương ứng với cơ sở dữ liệu trên máy bạn.
- **2. MongoDB (Dual Database Chat):**
  - Cập nhật `spring.data.mongodb.uri` với đường dẫn kết nối MongoDB cục bộ (`mongodb://localhost:27017/studywithme_chat`) hoặc chuỗi kết nối từ cụm MongoDB Atlas Cloud của bạn.
- **3. Cổng thanh toán VietQR PayOS:**
  - Điền các khóa `payos.client-id`, `payos.api-key`, `payos.checksum-key` được cung cấp từ trang quản trị PayOS.
  *(Lưu ý: File `application.properties` chứa cấu hình riêng đã được đưa vào `.gitignore` để tránh rò rỉ thông tin nhạy cảm).*

### 5.3. Khởi Chạy Hệ Thống
Mở Terminal tại thư mục gốc của dự án:

```powershell
# Chạy toàn bộ Test Suites kiểm thử
.\gradlew.bat test

# Khởi chạy ứng dụng Spring Boot
.\gradlew.bat bootRun
```

Sau khi khởi chạy thành công, truy cập trình duyệt tại địa chỉ:
👉 **`http://localhost:8080`**

---

## 🧪 6. Kết Quả Kiểm Thử (Automated Test Report)

Dự án áp dụng phương pháp kiểm thử tích hợp tự động (Integration Testing) với độ bao phủ cao. Toàn bộ **90/90 test cases** thuộc **15 test suites** đều đạt kết quả xuất sắc:

| Test Suite | Nội Dung Kiểm Thử | Số Tests | Kết Quả |
| :--- | :--- | :---: | :---: |
| `AdminCoreEnhancementsIntegrationTest` | Dashboard KPI, Phê duyệt KYC, Khóa tài khoản, Báo cáo | 9 | ✅ PASS |
| `AuthValidationLimitsIntegrationTest` | Đăng ký, Đăng nhập, Giới hạn độ dài, Ràng buộc mật khẩu | 11 | ✅ PASS |
| `BecomeTutorIntegrationTest` | Nộp hồ sơ ứng tuyển làm giảng viên, Tải lên bằng cấp | 3 | ✅ PASS |
| `ChatMongoIntegrationTest` | Kết nối MongoDB Atlas, Gửi nhận tin nhắn, Lấy lịch sử chat | 1 | ✅ PASS |
| `CourseModerationIntegrationTest` | Quy trình kiểm duyệt khóa học, Từ chối kèm lý do | 6 | ✅ PASS |
| `PaymentPayOsIntegrationTest` | Tạo giao dịch VietQR PayOS, Xử lý Webhook nạp tiền | 3 | ✅ PASS |
| `PayoutApprovalIntegrationTest` | Rút tiền ví gia sư, Kiểm tra số dư, Admin đối soát | 6 | ✅ PASS |
| `ProfileAndUserManagementIntegrationTest` | Cập nhật hồ sơ cá nhân, Đổi mật khẩu, Avatar | 6 | ✅ PASS |
| `PublicFlowIntegrationTest` | Trang chủ, Tìm kiếm gia sư, Xem chi tiết, Khóa học video | 22 | ✅ PASS |
| `StudentLearningIntegrationTest` | Học khóa học video, Ghi nhận tiến độ xem bài giảng | 2 | ✅ PASS |
| `StudywithmeApiApplicationTests` | Khởi tạo Spring Application Context & Bean Mapping | 1 | ✅ PASS |
| `TeacherKycIntegrationTest` | Quy trình đối soát giấy tờ bằng cấp, Cấp quyền gia sư | 4 | ✅ PASS |
| `TutorCourseStudioIntegrationTest` | Tạo khóa học, Thêm chương/bài học, Định dạng video | 7 | ✅ PASS |
| `Wbs2TutorSlotAndBookingIntegrationTest` | Ma trận lịch rảnh tuần, Đặt ca 1-1, Ký quỹ Escrow & Giải ngân | 7 | ✅ PASS |
| `Wbs6EntitiesIntegrationTest` | Kiểm thử thực thể kép Dual Database JPA & Mongo Document | 2 | ✅ PASS |
| **TỔNG CỘNG** | **Toàn bộ hệ thống kiểm thử tự động** | **90 / 90** | **100% PASSED** |

---

## 👥 7. Phân Quyền & Tài Khoản Mẫu (Demo Accounts)

| Vai trò | Email đăng nhập | Mật khẩu | Chức năng truy cập |
| :--- | :--- | :---: | :--- |
| **Quản trị viên (Admin)** | `admin@studywithme.vn` | `123456` | Cổng Quản trị (`/admin`) |
| **Gia sư (Giảng viên)** | `giangvien@studywithme.vn` | `123456` | Cổng Giảng viên (`/tutor/dashboard`), Lịch dạy (`/tutor/schedule`), Ca học (`/tutor/ca-hoc`) |
| **Học viên (Student)** | `hocvien@studywithme.vn` | `123456` | Đặt lịch kèm 1-1, Mua khóa học, Nạp ví, Nhắn tin chat |

---

## 📜 8. Bản Quyền & Giấy Phép (License)
Dự án được xây dựng phục vụ Đồ án Tốt nghiệp (ĐATN) chuyên ngành Kỹ thuật Phần mềm / Công nghệ Thông tin.  
Bản quyền thuộc về nhóm phát triển **StudyWithMe © 2025 - 2026**.

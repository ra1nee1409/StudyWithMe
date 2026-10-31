# 💳 TÀI LIỆU HƯỚNG DẪN TÍCH HỢP CỔNG THANH TOÁN VIETQR PAYOS

> **Dự án:** Nền tảng kết nối học tập & gia sư trực tuyến - **StudyWithMe**  
> **Tham chiếu chuẩn:** Mã nguồn chính thức [`payOSHQ/payos-demo-java-spring`](https://github.com/payOSHQ/payos-demo-java-spring)  
> **Thư viện chính thức:** `vn.payos:payos-java:2.0.1`  
> **Mục tiêu:** Thay thế sandbox giả lập truyền thống bằng cổng thanh toán **VietQR chuẩn NAPAS 24/7**, cho phép học viên quét mã QR bằng mọi app ngân hàng (VCB, MB, Techcombank...) và hệ thống tự động ghi nhận học phí qua **Webhook IPN tức thì (1-3s)**.

---

## 1. TẠI SAO CHỌN PAYOS THAY CHO SANDBOX GIẢ LẬP?

| Tiêu chí | Sandbox cũ (VNPAY Sandbox) | Cổng thanh toán VietQR payOS |
| :--- | :--- | :--- |
| **Phương thức thanh toán** | Nhập số thẻ ngân hàng test ảo, OTP giả lập | **Quét mã VietQR động** trên bất kỳ app ngân hàng thật |
| **Thời gian kích hoạt** | Cần nhập nhiều bước trên web cổng thanh toán | **1 chạm quét QR**, tự điền chính xác số tiền & nội dung |
| **Cơ chế xác nhận đơn** | Chờ redirect trình duyệt của học viên | **Webhook IPN phía máy chủ** (bắn ngay khi tài khoản nhận tiền) |
| **Tính bảo mật** | Hash SHA256 thủ công | SDK hỗ trợ sẵn **chữ ký Checksum HMAC-SHA256** chống giả mạo |
| **Trải nghiệm chấm đồ án** | Hội đồng phải copy số thẻ test dài dòng | Hội đồng / Giảng viên có thể mở app ngân hàng thật test 2.000đ - 10.000đ |

---

## 2. KHAI BÁO THƯ VIỆN & CẤU HÌNH HỆ THỐNG

### 2.1. Thêm dependency vào `build.gradle`
```groovy
dependencies {
    // payOS Java SDK chính thức
    implementation 'vn.payos:payos-java:2.0.1'
}
```

### 2.2. Khai báo biến cấu hình trong `application.properties`
Lấy 3 thông số định danh từ trang quản trị [my.payos.vn](https://my.payos.vn):
```properties
# ==================== PAYOS PAYMENT GATEWAY ====================
payos.client-id=${PAYOS_CLIENT_ID:your_client_id_here}
payos.api-key=${PAYOS_API_KEY:your_api_key_here}
payos.checksum-key=${PAYOS_CHECKSUM_KEY:your_checksum_key_here}

# Domain ứng dụng (Local dùng ngrok, Production dùng domain thực)
app.base-url=${APP_BASE_URL:http://localhost:8080}
```

### 2.3. Khởi tạo Spring Bean `PayOS`
Tạo file cấu hình `com.studywithme.config.PayOSConfig.java`:
```java
package com.studywithme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    @Bean
    public PayOS payOS(
            @Value("${payos.client-id}") String clientId,
            @Value("${payos.api-key}") String apiKey,
            @Value("${payos.checksum-key}") String checksumKey
    ) {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
```

---

## 3. KIẾN TRÚC LUỒNG THANH TOÁN (PAYMENT FLOW)

```
┌──────────────┐          ┌────────────────┐          ┌─────────────────┐          ┌──────────────┐
│   Học viên   │          │  StudyWithMe   │          │  payOS Gateway  │          │ App Ngân hàng│
└──────┬───────┘          └───────┬────────┘          └────────┬────────┘          └──────┬───────┘
       │                          │                            │                          │
       │ 1. Bấm "Thanh Toán"      │                            │                          │
       ├─────────────────────────>│                            │                          │
       │                          │ 2. Tạo đơn & PaymentLink   │                          │
       │                          ├───────────────────────────>│                          │
       │                          │    (orderCode, amount...)  │                          │
       │                          │<───────────────────────────┤                          │
       │                          │ 3. Trả checkoutUrl         │                          │
       │<─────────────────────────┤                            │                          │
       │ 4. Chuyển hướng sang QR  │                            │                          │
       ├──────────────────────────────────────────────────────>│                          │
       │                          │                            │                          │
       │ 5. Mở App ngân hàng quét mã VietQR                    │                          │
       ├─────────────────────────────────────────────────────────────────────────────────>│
       │ 6. Xác nhận chuyển khoản thành công                   │                          │
       │                          │                            │<─────────────────────────┤
       │                          │ 7. Bắn Webhook tức thì     │                          │
       │                          │<───────────────────────────┤                          │
       │                          │ 8. verifyPaymentWebhookData│                          │
       │                          │    UPDATE DonHang          │                          │
       │                          │    INSERT GhiDanhKhoaHoc   │                          │
       │                          │    +80% ví giảng viên      │                          │
       │                          ├───────────────────────────>│ (Trả HTTP 200)           │
       │                          │                            │                          │
       │ 9. Redirect kết quả      │                            │                          │
       │<─────────────────────────┴────────────────────────────┘                          │
```

---

## 4. CODE MẪU TÍCH HỢP (DỰA TRÊN PAYOS-DEMO-JAVA-SPRING)

### 4.1. Tạo Payment Link (`PaymentService.java`)
```java
// Mã đơn hàng phải là số nguyên dương duy nhất (Long)
long orderCode = System.currentTimeMillis() / 1000;

// Nội dung chuyển khoản tối đa 25 ký tự không dấu
String moTa = "SWM" + donHang.getId();

ItemData item = ItemData.builder()
        .name(khoaHoc.getTieuDe())
        .quantity(1)
        .price(khoaHoc.getGiaHienTai().intValue())
        .build();

PaymentData paymentData = PaymentData.builder()
        .orderCode(orderCode)
        .amount(donHang.getTongTienThanhToan().intValue())
        .description(moTa)
        .returnUrl(baseUrl + "/checkout/result?orderId=" + donHang.getId())
        .cancelUrl(baseUrl + "/checkout?cancel=true")
        .item(item)
        .build();

CheckoutResponseData result = payOS.createPaymentLink(paymentData);
String checkoutUrl = result.getCheckoutUrl(); // Trả về link trang quét mã VietQR
```

### 4.2. Xử lý Webhook IPN tự động (`PaymentApiController.java`)
```java
@PostMapping("/api/payment/payos_transfer_handler")
public ResponseEntity<?> handlePayOSWebhook(@RequestBody ObjectNode body) {
    try {
        // Xác thực chữ ký số bằng Checksum Key
        WebhookData data = payOS.verifyPaymentWebhookData(body);
        
        long orderCode = data.getOrderCode();
        // 1. Tìm đơn hàng tương ứng trong cơ sở dữ liệu
        DonHang donHang = donHangRepository.findByOrderCodePayos(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn: " + orderCode));

        if (donHang.getTrangThaiThanhToan() != TrangThaiDonHang.DA_THANH_TOAN) {
            // 2. Cập nhật trạng thái đơn hàng
            donHang.setTrangThaiThanhToan(TrangThaiDonHang.DA_THANH_TOAN);
            donHang.setNgayThanhToan(LocalDateTime.now());
            donHang.setMaGiaoDichPayos(data.getReference());
            donHangRepository.save(donHang);

            // 3. Kích hoạt quyền học tập (GhiDanhKhoaHoc)
            for (ChiTietDonHang ct : donHang.getDanhSachChiTiet()) {
                ghiDanhKhoaHocRepository.save(GhiDanhKhoaHoc.builder()
                        .nguoiDung(donHang.getNguoiDung())
                        .khoaHoc(ct.getKhoaHoc())
                        .donHang(donHang)
                        .phanTramTienDo(0)
                        .build());

                // 4. Cộng 80% học phí vào ví Giảng viên
                hoSoGiangVienService.congTienBanKhoaHoc(ct.getKhoaHoc().getGiangVien(), ct.getGiaMua());
            }
        }

        // Bắt buộc trả về HTTP 200 để payOS biết hệ thống đã nhận thông tin
        return ResponseEntity.ok(Map.of("success", true, "message", "Webhook verified"));
    } catch (Exception e) {
        log.error("Lỗi xác thực webhook payOS: ", e);
        return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
    }
}
```

---

## 5. HƯỚNG DẪN TEST THANH TOÁN & WEBHOOK (DÀNH CHO DEV VÀ BẢO VỆ ĐỒ ÁN)

### Cách 1: Quét mã VietQR thật qua Ngrok (Môi trường Localhost)
1. **Khởi động ứng dụng Spring Boot:** Chạy cổng `8080`.
2. **Khởi chạy Ngrok:**
   ```bash
   ngrok http 8080
   ```
   Ngrok sẽ cấp một địa chỉ công khai, ví dụ: `https://abcdef.ngrok-free.app`.
3. **Cấu hình Webhook trên trang quản trị payOS:**
   - Đăng nhập [my.payos.vn](https://my.payos.vn) -> Chọn Kênh thanh toán -> Mục **Webhook**.
   - Dán URL: `https://abcdef.ngrok-free.app/api/payment/payos_transfer_handler`.
   - Bấm nút **Xác nhận Webhook** (hoặc gọi API `POST /api/payment/dang-ky-webhook?webhookUrl=https://abcdef.ngrok-free.app/api/payment/payos_transfer_handler`).
4. **Kiểm thử thanh toán:**
   - Mở ứng dụng StudyWithMe -> Chọn khóa học -> Bấm Thanh toán.
   - Quét mã QR bằng App ngân hàng thật bất kỳ (chuyển 2.000đ hoặc thanh toán test).
   - Kiểm tra log Spring Boot: Webhook bắn về ngay lập tức, đơn hàng chuyển thành công và khóa học mở trong tích tắc!

### Cách 2: Chế độ Mô phỏng Demo (Cực kỳ tiện lợi khi bảo vệ trước Hội đồng chấm Đồ án)
Khi thuyết trình trước Hội đồng hoặc khi mạng internet trường học không ổn định, bạn có thể demo luồng thanh toán mà không cần chuyển tiền thật:
1. Tạo đơn hàng và chuyển sang cổng VietQR payOS để thầy cô quan sát mã QR động VietQR chuyên nghiệp.
2. Quay lại trang kết quả đơn hàng (`/payment/payos-callback?orderCode=...`).
3. Bấm nút màu xanh lá **"⚡ Mô Phỏng Chuyển Khoản Thành Công (Demo Hội Đồng)"** có sẵn trên giao diện.
4. Hệ thống sẽ tự động gọi API `POST /api/payment/demo/kich-hoat/{orderCode}` giả lập tín hiệu Webhook chuẩn từ ngân hàng:
   - Tự động kích hoạt khóa học vào Bàn Học Của Tôi.
   - Tự động trích xuất 80% học phí vào ví tiền của Giảng viên.
   - Tự động lưu vết biến động số dư trong Lịch sử ví tiền.
   - Trang web tự động tải lại hiển thị trạng thái **"Thanh Toán Thành Công"** cùng danh sách khóa học sẵn sàng học!

---

## 6. DANH SÁCH CÁC FILE CẦN THAO TÁC KHI TRIỂN KHAI

1. `build.gradle`: Thêm `implementation 'vn.payos:payos-java:2.0.1'`.
2. `src/main/resources/application.properties`: Khai báo 3 key payOS.
3. `src/main/java/com/studywithme/config/PayOSConfig.java`: Tạo Bean `PayOS`.
4. `src/main/java/com/studywithme/service/impl/PaymentServiceImpl.java`: Cập nhật logic tạo payment link qua SDK `PayOS`.
5. `src/main/java/com/studywithme/controller/api/PaymentApiController.java`: Thêm handler `@PostMapping("/api/payment/payos_transfer_handler")`.
6. `src/main/java/com/studywithme/config/SecurityConfig.java`: Cấu hình permitAll endpoint webhook `/api/payment/payos_transfer_handler`.
7. `src/main/resources/templates/checkout/index.html`: Cập nhật giao diện thanh toán sang thương hiệu và trải nghiệm quét VietQR.


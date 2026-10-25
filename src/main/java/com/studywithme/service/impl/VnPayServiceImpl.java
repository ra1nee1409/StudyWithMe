package com.studywithme.service.impl;

import com.studywithme.config.VnpayConfig;
import com.studywithme.entity.DonHang;
import com.studywithme.service.VnPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class VnPayServiceImpl implements VnPayService {

    private final VnpayConfig vnpayConfig;

    public VnPayServiceImpl(VnpayConfig vnpayConfig) {
        this.vnpayConfig = vnpayConfig;
    }

    @Override
    public String createPaymentUrl(DonHang donHang, String clientIp) {
        String vnp_Version = VnpayConfig.VNP_VERSION;
        String vnp_Command = VnpayConfig.VNP_COMMAND;
        String vnp_TmnCode = vnpayConfig.getVnpTmnCode();

        long amount = donHang.getTongTienThanhToan().multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", VnpayConfig.VNP_CURR_CODE);
        vnp_Params.put("vnp_TxnRef", donHang.getMaDonHang());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang #" + donHang.getMaDonHang() + " - StudyWithMe");
        vnp_Params.put("vnp_OrderType", VnpayConfig.VNP_ORDER_TYPE);
        vnp_Params.put("vnp_Locale", VnpayConfig.VNP_LOCALE);
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", (clientIp != null && !clientIp.isBlank()) ? clientIp : "127.0.0.1");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VnpayConfig.hmacSHA512(vnpayConfig.getVnpHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnpayConfig.getVnpPayUrl() + "?" + queryUrl;
        log.info("Đã tạo URL thanh toán VNPAY thành công cho đơn hàng: {}", donHang.getMaDonHang());
        return paymentUrl;
    }

    @Override
    public boolean validateCallback(Map<String, String> queryParams) {
        String vnp_SecureHash = queryParams.get("vnp_SecureHash");
        if (vnp_SecureHash == null || vnp_SecureHash.isBlank()) {
            return false;
        }

        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if ((key != null) && (value != null) && (!key.equalsIgnoreCase("vnp_SecureHash")) && (!key.equalsIgnoreCase("vnp_SecureHashType"))) {
                fields.put(key, value);
            }
        }

        String signValue = VnpayConfig.hashAllFields(fields, vnpayConfig.getVnpHashSecret());
        return signValue.equalsIgnoreCase(vnp_SecureHash);
    }
}

package com.studywithme.config;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
@Getter
public class PayOSConfig {

    private static final Logger log = LoggerFactory.getLogger(PayOSConfig.class);

    @Value("${payos.client-id:}")
    private String clientId;

    @Value("${payos.api-key:}")
    private String apiKey;

    @Value("${payos.checksum-key:}")
    private String checksumKey;

    @Value("${payos.return-url:http://localhost:8080/payment/payos-callback}")
    private String returnUrl;

    @Value("${payos.cancel-url:http://localhost:8080/checkout?cancel=true}")
    private String cancelUrl;

    @Bean
    public PayOS payOS() {
        String effectiveClientId = (clientId != null && !clientId.isBlank()) ? clientId.trim() : "dummy-client-id";
        String effectiveApiKey = (apiKey != null && !apiKey.isBlank()) ? apiKey.trim() : "dummy-api-key";
        String effectiveChecksumKey = (checksumKey != null && !checksumKey.isBlank())
                ? checksumKey.trim()
                : "dummy-checksum-key-minimum-32-chars-long";

        if ("dummy-client-id".equals(effectiveClientId)) {
            log.warn(">>> PayOS chưa được cấu hình key thực tế (sử dụng dummy credentials). Hãy điền client-id, api-key, checksum-key vào application.properties khi kết nối thật.");
        } else {
            log.info(">>> PayOS SDK đã sẵn sàng kết nối với Client ID: {}...", effectiveClientId.substring(0, Math.min(6, effectiveClientId.length())));
        }

        return new PayOS(effectiveClientId, effectiveApiKey, effectiveChecksumKey);
    }
}

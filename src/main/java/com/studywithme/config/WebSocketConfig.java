package com.studywithme.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Cấu hình WebSocket STOMP cho phân hệ Nhắn tin Realtime 0s delay (WBS 4.1)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Kích hoạt Simple In-Memory Broker cho các channel /topic (nhóm/phòng chat) và /queue (cá nhân)
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix cho các message gửi từ client lên server (@MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
        // Prefix cho các message riêng biệt gửi tới từng user cụ thể
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint kết nối WebSocket cho Web STOMP client
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Cho phép kết nối WebSocket chuẩn không qua SockJS nếu cần
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*");
    }
}

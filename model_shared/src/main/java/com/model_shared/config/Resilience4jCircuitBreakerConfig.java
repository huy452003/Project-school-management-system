package com.model_shared.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Circuit Breaker Configuration cho toàn bộ project
 * 
 * Config này được đặt trong model_shared vì đây là nơi tập trung các config chung.
 * Tất cả các module sử dụng model_shared sẽ tự động có Circuit Breaker config này.
 * 
 * LƯU Ý: Với Resilience4j Spring Boot 3, cách tốt nhất vẫn là cấu hình trong application.properties
 * của module sử dụng. Config này chỉ là fallback/default values.
 * 
 * Module sử dụng có thể override bằng cách thêm vào application.properties:
 * resilience4j.circuitbreaker.instances.security-service.failureRateThreshold=30
 */
@Configuration
public class Resilience4jCircuitBreakerConfig {

    /**
     * Tạo CircuitBreakerConfig với default values cho security-service
     * 
     * Được sử dụng bởi SecurityService trong security_shared module
     * khi gọi Security module API
     * 
     * Lưu ý: Config này chỉ hoạt động nếu không có config trong application.properties
     * Resilience4j Spring Boot 3 ưu tiên config từ properties file
     */
    @Bean
    @ConditionalOnMissingBean(name = "securityServiceCircuitBreakerConfig")
    public CircuitBreakerConfig securityServiceCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                // Sliding window: Tính failure rate dựa trên 10 requests gần nhất
                .slidingWindowSize(10)
                
                // Phải có ít nhất 5 calls mới tính failure rate (tránh false positive khi có ít requests)
                .minimumNumberOfCalls(5)
                
                // Cho phép 3 calls trong HALF_OPEN state để test service đã recover chưa
                .permittedNumberOfCallsInHalfOpenState(3)
                
                // Tự động chuyển từ OPEN sang HALF_OPEN sau waitDurationInOpenState
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                
                // Đợi 60s trước khi chuyển từ OPEN sang HALF_OPEN (thử lại)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                
                // 50% failures trong sliding window → Circuit chuyển sang OPEN
                .failureRateThreshold(50f)
                
                // 100% slow calls → Circuit chuyển sang OPEN
                .slowCallRateThreshold(100f)
                
                // Call > 2s được tính là slow call
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                
                // Record exceptions được tính là failures
                .recordExceptions(
                    java.net.ConnectException.class,
                    java.net.SocketTimeoutException.class,
                    java.util.concurrent.TimeoutException.class,
                    org.springframework.web.client.ResourceAccessException.class,
                    org.springframework.web.client.HttpServerErrorException.class
                )
                
                .build();
    }

    /**
     * Register Circuit Breaker với tên "security-service"
     * 
     * Circuit Breaker này sẽ được sử dụng bởi @CircuitBreaker(name = "security-service")
     * 
     * Lưu ý: Với Resilience4j Spring Boot 3, cách tốt nhất vẫn là cấu hình trong application.properties
     * Config này chỉ là fallback nếu không có config trong properties
     */
    @Bean
    @ConditionalOnMissingBean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig defaultConfig = securityServiceCircuitBreakerConfig();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(defaultConfig);
        // Register với tên "security-service"
        registry.circuitBreaker("security-service", defaultConfig);
        return registry;
    }

    /**
     * Có thể thêm các Circuit Breaker config khác cho các service khác ở đây
     * Ví dụ: payment-service, notification-service, etc.
     */
    // @Bean
    // public CircuitBreakerConfig paymentServiceCircuitBreakerConfig() {
    //     return CircuitBreakerConfig.custom()
    //         .slidingWindowSize(10)
    //         .minimumNumberOfCalls(5)
    //         .failureRateThreshold(50f)
    //         .waitDurationInOpenState(Duration.ofSeconds(60))
    //         .build();
    // }
}

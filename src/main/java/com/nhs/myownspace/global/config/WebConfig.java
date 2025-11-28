package com.nhs.myownspace.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebConfig {
    private final String allowedOrigin;

    public WebConfig(@Value("${cors.allowed-origin}") String allowedOrigin ){
        this.allowedOrigin = allowedOrigin;
    }

    /**
     * 전역 CORS 설정
     * - 모든 컨트롤러(@RestController)에 자동 적용됨
     * - SecurityConfig의 cors() 설정과 함께 작동
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        log.info("CORS 설정 초기화 - 허용 Origin: {}", allowedOrigin);

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // 프론트엔드 도메인 허용
                        .allowedOrigins(allowedOrigin)
                        // 허용할 HTTP 메서드
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // 클라이언트 쿠키/ 인증정보 전송 허용
                        .allowCredentials(true)
                        // 모든 헤더 허용
                        .allowedHeaders("*")
                        // 응답 헤더 중 노출할 항목
                        .exposedHeaders("Set-Cookie", "Authorization")
                        // Pre-flight 캐시 유지 시간(초 단위)
                        .maxAge(3600);
            }
        };
    }
}

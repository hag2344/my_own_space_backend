package com.nhs.myownspace.global.config;

import com.nhs.myownspace.auth.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("=== Spring Security 초기화 시작 ===");

        http
                // CSRF 비활성화 (React에서 직접 요청 처리)
                .csrf(csrf -> csrf.disable())
                // WebConfig의 CORS 설정을 Security에서도 활성화
                .cors(cors -> {})
                // 인증/인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로
                        // 로그인, 카카오 인증 등 허용
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/oauth/**"
                        ).permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 기본 로그인/세션 기능 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 로그아웃 (React에서 쿠키 삭제로 처리)
                .logout(logout -> logout.disable());

        log.info("=== Spring Security 초기화 완료 ===");

        return http.build();
    }
}
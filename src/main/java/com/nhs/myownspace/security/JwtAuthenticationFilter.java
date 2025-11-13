package com.nhs.myownspace.security;

import com.nhs.myownspace.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected  void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
        throws ServletException, IOException{

        try{
            // 쿠키에서 JWT 토큰 추출
            String jwt = extractJwtFromCookies(request);

            // JWT 유효성 검증 및 사용자 식별
            if(jwt!=null && jwtService.validateJwt(jwt)){
                String providerId = jwtService.extractUserId(jwt);
                String provider = jwtService.extractClaim(jwt, "provider");

                if (providerId != null && provider != null){

                    // principal을 Map 형태로 구성
                    var principal = Map.of(
                            "provider", provider,
                            "providerId", providerId
                    );

                    // 권한은 빈 리스트라도 넣는 게 안전
                    var auth = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of()
                    );

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // SecurityContext에 저장
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("JWT 인증 성공: provider: {}, providerId: {}", provider, providerId);
                }else{
                    log.warn("JWT claim 누락 - provider or providerId == null");
                }
            }

        }catch (Exception e){
            log.warn("JWT 인증 필터 처리 중 오류 발생: {}", e.getMessage(), e);
        }
        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 쿠키에서 JWT(token) 추출
     */
    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}

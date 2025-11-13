package com.nhs.myownspace.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@Slf4j
public class AuthUtil {

    /**
     * SecurityContext에서 provider, providerId 추출
     * @return 정상일 경우 { provider, providerId } Map 반환
     *         실패 시 null 반환 → Controller에서 401 처리
     */
    public static Map<String, String> getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        Object principal = auth.getPrincipal();

        // anonymousUser 차단
        if (principal instanceof String && principal.equals("anonymousUser")) {
            return null;
        }

        // principal이 우리가 저장한 Map인지 확인
        if (!(principal instanceof Map<?, ?> userAuth)) {
            log.warn("잘못된 Principal 형식: {}", principal);
            return null;
        }

        String providerName = (String) userAuth.get("provider");
        String providerId = (String) userAuth.get("providerId");

        if (providerName == null || providerId == null) {
            log.warn("Principal 정보 부족");
            return null;
        }

        return Map.of(
                "provider", providerName,
                "providerId", providerId
        );
    }
}
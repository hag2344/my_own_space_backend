package com.nhs.myownspace.auth;

import com.nhs.myownspace.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoService kakaoService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * 카카오 로그인 처리
     */
    public ResponseEntity<?> loginWithKakao(String code) {

        // 1. 카카오 사용자 정보 조회
        Map<String, Object> kakaoInfo = kakaoService.getUserInfoFromCode(code);
        if (kakaoInfo == null) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", "카카오 사용자 정보 조회 실패"));
        }

        String providerId = (String) kakaoInfo.get("providerId");
        String nickname = (String) kakaoInfo.get("nickname");
        String profileImage = (String) kakaoInfo.get("profileImage");

        // 2. DB 조회 or 자동 회원가입
        User user = userRepository.findByProviderAndProviderId(Provider.KAKAO, providerId)
                .map(exist -> {
                    boolean modified = false;

                    if (!exist.getNickname().equals(nickname)) {
                        exist.setNickname(nickname);
                        modified = true;
                    }

                    if (profileImage != null && !profileImage.equals(exist.getProfileImage())) {
                        exist.setProfileImage(profileImage);
                        modified = true;
                    }

                    if (modified) {
                        userRepository.save(exist);
                        log.info("기존 사용자 정보 업데이트 완료");
                    }

                    return exist;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .provider(Provider.KAKAO)
                            .providerId(providerId)
                            .nickname(nickname)
                            .profileImage(profileImage)
                            .build();
                    return userRepository.save(newUser);
                });

        // 3. JWT 생성
        String accessToken = jwtService.createAccessToken(user.getProvider().name(), user.getProviderId());
        String refreshToken = jwtService.createRefreshToken(user.getProvider().name(), user.getProviderId());

        // 4. 쿠키 생성
        ResponseCookie accessCookie = ResponseCookie.from("token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofHours(1))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofDays(14))
                .build();

        // 5. 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("message", "login success"));
    }

    /**
     * JWT 인증 여부 확인
     */
    public ResponseEntity<?> checkLogin(String token) {
        if (token != null && jwtService.validateJwt(token)) {
            return ResponseEntity.ok(Map.of("auth", true));
        }
        return ResponseEntity.status(401).body(Map.of("auth", false));
    }

    /**
     * Access Token 재발급
     */
    public ResponseEntity<?> refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtService.validateJwt(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("message", "refresh token expired"));
        }

        String providerId = jwtService.extractUserId(refreshToken);
        String provider = jwtService.extractClaim(refreshToken, "provider");

        if (providerId == null || provider == null) {
            return ResponseEntity.status(401).body(Map.of("message", "invalid refresh token"));
        }

        // 새 Access Token 생성
        String newAccess = jwtService.createAccessToken(provider, providerId);

        // HttpOnly + secure 쿠키 발급 (accessToken)
        ResponseCookie newCookie = ResponseCookie.from("token", newAccess)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofHours(1))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                .body(Map.of("message", "refreshed"));
    }

    /**
     * 로그아웃 처리
     */
    public ResponseEntity<?> logout() {

        ResponseCookie deleteAccess = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();

        ResponseCookie deleteRefresh = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .body(Map.of("message", "logout success"));
    }
}

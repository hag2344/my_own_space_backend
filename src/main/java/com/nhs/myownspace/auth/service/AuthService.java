package com.nhs.myownspace.auth.service;

import com.nhs.myownspace.auth.dto.AuthResult;
import com.nhs.myownspace.auth.dto.KakaoUserInfoDto;
import com.nhs.myownspace.user.*;
import com.nhs.myownspace.user.entity.User;
import com.nhs.myownspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

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
    public AuthResult loginWithKakao(String code) {

        // 1. 카카오 사용자 정보 조회
        KakaoUserInfoDto kakaoInfo = kakaoService.getUserInfoFromCode(code);
        if (kakaoInfo == null) {
            return AuthResult.fail("카카오 사용자 정보 조회 실패");
        }

        log.info("카카오 사용자 정보 조회 성공 - kakaoId: {}", kakaoInfo.getProviderId());

        String providerId = kakaoInfo.getProviderId();
        String nickname = kakaoInfo.getNickname();
        String profileImage = kakaoInfo.getProfileImage();

        // 2. DB 조회 or 자동 회원가입
        User user = userRepository.findByProviderAndProviderId(Provider.KAKAO, providerId)
                .map(exist -> {
                    boolean modified = false;

                    if (!Objects.equals(exist.getNickname(), nickname)) {
                        exist.setNickname(nickname);
                        modified = true;
                    }

                    if (!Objects.equals(exist.getProfileImage(), profileImage)) {
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

        return AuthResult.success(accessToken, refreshToken);
    }

    /**
     * JWT 인증 여부 확인
     */
    public boolean isValidToken(String token) {
        return token != null && jwtService.validateJwt(token);
    }

    /**
     * Refresh Token을 통해 Access Token 재발급
     */
    public AuthResult refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtService.validateJwt(refreshToken)) {
            return AuthResult.fail("refresh token expired");
        }

        String providerId = jwtService.extractUserId(refreshToken);
        String provider = jwtService.extractClaim(refreshToken, "provider");

        if (providerId == null || provider == null) {
            return AuthResult.fail("invalid refresh token");
        }

        // 새 Access Token 생성
        String newAccess = jwtService.createAccessToken(provider, providerId);

        return new AuthResult(true, "refreshed", newAccess, null);
    }

}

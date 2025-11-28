package com.nhs.myownspace.auth.controller;

import com.nhs.myownspace.auth.dto.AuthResult;
import com.nhs.myownspace.auth.service.AuthService;
import com.nhs.myownspace.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Kakao 로그인 시 Access, Refresh Token 발급 + 최초 로그인 시 자동 회원가입
     */
    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code){
        AuthResult result = authService.loginWithKakao(code);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(result.getMessage()));
        }

        // 4. 쿠키 생성
        ResponseCookie accessCookie = ResponseCookie.from("token", result.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofHours(1))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofDays(14))
                .build();

        // 5. 응답
        return ResponseEntity.ok()
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                    h.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                })
                .body(ApiResponse.ok(null,"login success"));

    }

    /**
     * 로그인 인증 확인 (JWT 검증)
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkLogin(@CookieValue(value = "token", required = false) String token){

        boolean valid = authService.isValidToken(token);

        if(!valid){
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("invalid or expired access token"));
        }

        return ResponseEntity.ok(ApiResponse.ok(null));

    }

    /**
     * Access Token 만료 시 Refresh Token으로 재발급
     */
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refresh_token", required = false) String refreshToken){

        AuthResult result = authService.refreshToken(refreshToken);

        if (!result.isSuccess()){
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(result.getMessage()));
        }

        // 새 Access token 쿠키 생성
        ResponseCookie newCookie = ResponseCookie.from("token", result.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofHours(1))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                .body(ApiResponse.ok(null, "refreshed"));
    }

    /**
     * 로그아웃 (쿠키 즉시 만료)
     */
    @PostMapping("/logout")
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
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, deleteAccess.toString());
                    h.add(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
                })
                .body(ApiResponse.ok(null, "logout success"));
    }

    /**
     *  현재 서버 ON/OFF 조회
     */
    @GetMapping("/server/state")
    public String serverState() {
        return "ON";
    }

    /**
     *  현재 서버 수면 상태인지 조회 (cron으로 주기적으로 보냄)
     *  render 무료 클라우드 서버는 15분동안 요청 없으면 수면상태
     *  현재 cron으로 12분마다 요청 보내어 수면상태 방지 중
     *  cron 사이트 (cron-job.org)
     */
    @GetMapping("/server/alive")
    public ResponseEntity<?> keepAlive() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "status", "alive"
        ));
    }

}


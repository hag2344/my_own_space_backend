package com.nhs.myownspace.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.Map;

import com.nhs.myownspace.user.User;
import com.nhs.myownspace.user.UserRepository;
import com.nhs.myownspace.user.Provider;

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
        return authService.loginWithKakao(code);
    }

    /**
     * 로그인 인증 확인 (JWT 검증)
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkLogin(@CookieValue(value = "token", required = false) String token){
        return authService.checkLogin(token);
    }

    /**
     * Access Token 만료 시 Refresh Token으로 재발급
     */
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refresh_token", required = false) String refreshToken){
        return authService.refreshToken(refreshToken);
    }

    /**
     * 로그아웃 (쿠키 즉시 만료)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return authService.logout();
    }

    /**
     *  현재 서버 ON/OFF 조회
     */
    @GetMapping("/server/state")
    public String serverState() {
        return "ON";
    }

    @GetMapping("/server/alive")
    public ResponseEntity<?> keepAlive() {
        return ResponseEntity.ok(Map.of("status", "alive"));
    }

}


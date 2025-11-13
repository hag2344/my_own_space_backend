package com.nhs.myownspace.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     *  내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo() {
        try {
            Map<String, Object> result = userService.getMyInfo();

            if (result == null) {
                return ResponseEntity.status(401).body(Map.of("message", "unauthorized"));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("내 정보 조회 중 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("message", "get user info failed", "error", e.getMessage()));
        }
    }
}

package com.nhs.myownspace.user.controller;

import com.nhs.myownspace.global.dto.ApiResponse;
import com.nhs.myownspace.user.dto.UserResponseDto;
import com.nhs.myownspace.user.service.UserService;
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
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyInfo() {
        try {
            UserResponseDto result = userService.getMyInfo();

            if (result == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("조회 실패: 로그인 정보 없음 또는 사용자 정보 없음"));
            }

            return ResponseEntity.ok(ApiResponse.ok(result));

        } catch (Exception e) {
            log.error("내 정보 조회 중 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

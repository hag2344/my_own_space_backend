package com.nhs.myownspace.home.controller;

import com.nhs.myownspace.global.dto.ApiResponse;
import com.nhs.myownspace.home.dto.HomeSummaryResponseDto;
import com.nhs.myownspace.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<HomeSummaryResponseDto>> summary() {

        HomeSummaryResponseDto res = homeService.getSummary();

        if (res == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

}

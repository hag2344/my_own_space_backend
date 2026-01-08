package com.nhs.myownspace.schedule.controller;

import com.nhs.myownspace.schedule.service.ScheduleService;
import com.nhs.myownspace.schedule.dto.ScheduleRequestDto;
import com.nhs.myownspace.schedule.dto.ScheduleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nhs.myownspace.global.dto.ApiResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 전체 일정 조회 (내 일정)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> getSchedules() {
        List<ScheduleResponseDto> schedules = scheduleService.getMySchedules();

        if(schedules == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(schedules));
    }

    /**
     * 일정 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> create(@RequestBody ScheduleRequestDto req) {
        ScheduleResponseDto created = scheduleService.create(req);

        if(created == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("생성 실패: 로그인 정보 없음 또는 잘못된 요청"));
        }

        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    /**
     * 일정 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> update(
            @PathVariable Long id,
            @RequestBody ScheduleRequestDto req
    ) {
        ScheduleResponseDto updated = scheduleService.update(id, req);

        if(updated == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("수정 실패: 권한 없음 또는 해당 일정이 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    /**
     * 일정 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        boolean deleted = scheduleService.delete(id);

        if (!deleted){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("삭제 실패: 권한 없음 또는 이미 삭제된 일정입니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
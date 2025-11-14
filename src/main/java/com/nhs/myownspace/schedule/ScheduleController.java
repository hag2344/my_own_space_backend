package com.nhs.myownspace.schedule;

import com.nhs.myownspace.schedule.dto.ScheduleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 전체 일정 조회
     */
    @GetMapping
    public ResponseEntity<?> getSchedules() {
        var schedules = scheduleService.getMySchedules();
        return ResponseEntity.ok(schedules);
    }

    /**
     * 일정 생성
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ScheduleRequest req) {
        var saved = scheduleService.create(req);
        return ResponseEntity.ok(saved);
    }

    /**
     * 일정 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ScheduleRequest req
    ) {
        var updated = scheduleService.update(id, req);
        return ResponseEntity.ok(updated);
    }

    /**
     * 일정 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean ok = scheduleService.delete(id);
        return ResponseEntity.ok(Map.of("deleted", ok));
    }
}
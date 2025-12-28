package com.nhs.myownspace.mymemory.controller;

import com.nhs.myownspace.global.dto.ApiResponse;
import com.nhs.myownspace.global.dto.PagingResponse;
import com.nhs.myownspace.mymemory.dto.MyMemoryRequestDto;
import com.nhs.myownspace.mymemory.dto.MyMemoryResponseDto;
import com.nhs.myownspace.mymemory.service.MyMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/mymemory")
@RequiredArgsConstructor
public class MyMemoryController {

    private final MyMemoryService myMemoryService;

    /**
     * 내 추억 조회 (내 추억)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagingResponse<MyMemoryResponseDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(value = "q", required = false) String q
    ) {
        Page<MyMemoryResponseDto> res = myMemoryService.getMyMemories(page, size, q);

        if (res == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(PagingResponse.of(res)));
    }

    /**
     * id로 내 추억 조회 (내 추억)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MyMemoryResponseDto>> detail(@PathVariable Long id) {
        MyMemoryResponseDto dto = myMemoryService.getMyMemory(id);

        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("조회 실패: 로그인 필요/권한 없음/데이터 없음"));
        }

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    /**
     * 내 추억 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MyMemoryResponseDto>> create(@RequestBody MyMemoryRequestDto req) {
        MyMemoryResponseDto created = myMemoryService.create(req);

        if (created == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("생성 실패: 로그인 정보 없음 또는 잘못된 요청"));
        }

        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    /**
     * 내 추억 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MyMemoryResponseDto>> update(
            @PathVariable Long id,
            @RequestBody MyMemoryRequestDto req
    ) {
        MyMemoryResponseDto updated = myMemoryService.update(id, req);

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("수정 실패: 권한 없음 또는 해당 글이 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    /**
     * 내 추억 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        boolean ok = myMemoryService.delete(id);

        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("삭제 실패: 권한 없음 또는 이미 삭제된 글입니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
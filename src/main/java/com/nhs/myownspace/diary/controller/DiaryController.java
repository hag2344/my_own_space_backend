package com.nhs.myownspace.diary.controller;

import com.nhs.myownspace.diary.service.DiaryService;
import com.nhs.myownspace.diary.dto.DiaryRequestDto;
import com.nhs.myownspace.diary.dto.DiaryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nhs.myownspace.global.dto.ApiResponse;
import com.nhs.myownspace.global.dto.PagingResponse;

@Slf4j
@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 일기 조회 (내 일기)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagingResponse<DiaryResponseDto>>> getDiaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q
    ) {
        Page<DiaryResponseDto> diaries = diaryService.getMyDiaries(page, size, q);

        if(diaries == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(PagingResponse.of(diaries)));
    }

    /**
     * id로 일기 조회 (내 일기)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryResponseDto>> getDiary(@PathVariable Long id) {
        DiaryResponseDto diary = diaryService.getMyDiary(id);

        if (diary == null) {
            // 여기서 401/403/404를 세분화 못하는 게 단점이라,
            // 최소 변경이면 그냥 404 or 400 중 하나로 통일하는 걸 추천.
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("조회 실패: 로그인 필요/권한 없음/데이터 없음"));
        }

        return ResponseEntity.ok(ApiResponse.ok(diary));
    }

    /**
     * 일기 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DiaryResponseDto>> create(@RequestBody DiaryRequestDto req) {
        DiaryResponseDto created = diaryService.create(req);

        if(created == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("생성 실패: 로그인 정보 없음 또는 잘못된 요청"));
        }

        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    /**
     * 일기 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DiaryResponseDto>> update(
            @PathVariable Long id,
            @RequestBody DiaryRequestDto req
    ) {
        DiaryResponseDto updated = diaryService.update(id, req);

        if(updated == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("수정 실패: 권한 없음 또는 해당 일기가 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    /**
     * 일기 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        boolean deleted = diaryService.delete(id);

        if (!deleted){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("삭제 실패: 권한 없음 또는 이미 삭제된 일기입니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}

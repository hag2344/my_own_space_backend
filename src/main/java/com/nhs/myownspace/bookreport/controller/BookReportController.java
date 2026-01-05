package com.nhs.myownspace.bookreport.controller;

import com.nhs.myownspace.global.dto.ApiResponse;
import com.nhs.myownspace.global.dto.PagingResponse;
import com.nhs.myownspace.bookreport.dto.BookReportRequestDto;
import com.nhs.myownspace.bookreport.dto.BookReportResponseDto;
import com.nhs.myownspace.bookreport.service.BookReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/bookreport")
@RequiredArgsConstructor
public class BookReportController {

    private final BookReportService bookReportService;

    /**
     * 독서 기록 조회 (내 독서 기록)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagingResponse<BookReportResponseDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String q
    ){
        Page<BookReportResponseDto> res = bookReportService.getBookReports(page, size, q);

        if (res == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요합니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(PagingResponse.of(res)));
    }

    /**
     * id로 독서 기록 조회 (내 독서 기록)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookReportResponseDto>> detail(@PathVariable Long id) {
        BookReportResponseDto dto = bookReportService.getBookReport(id);

        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("조회 실패: 로그인 필요/권한 없음/데이터 없음"));
        }

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    /**
     * 독서 기록 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookReportResponseDto>> create(@RequestBody BookReportRequestDto req) {
        BookReportResponseDto created = bookReportService.create(req);

        if (created == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("생성 실패: 로그인 정보 없음 또는 잘못된 요청"));
        }

        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    /**
     * 독서 기록 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookReportResponseDto>> update(
            @PathVariable Long id,
            @RequestBody BookReportRequestDto req
    ){
        BookReportResponseDto updated = bookReportService.update(id, req);

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("수정 실패: 권한 없음 또는 해당 글이 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    /**
     * 독서 기록 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        boolean ok = bookReportService.delete(id);

        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("삭제 실패: 권한 없음 또는 이미 삭제된 글입니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}

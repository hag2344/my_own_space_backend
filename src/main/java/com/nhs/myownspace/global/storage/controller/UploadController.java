package com.nhs.myownspace.global.storage.controller;

import com.nhs.myownspace.global.dto.ApiResponse;
import com.nhs.myownspace.global.storage.dto.UploadResponseDto;
import com.nhs.myownspace.global.storage.model.UploadFolder;
import com.nhs.myownspace.global.storage.service.UploadManagerService;
import com.nhs.myownspace.global.storage.util.FileValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadManagerService uploadManagerService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadResponseDto>> uploadImage(
            @RequestParam("folder") UploadFolder folder,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            // 1) 이미지 유효성 검증
            FileValidateUtil.validateImage(file);

            // 2) Supabase 업로드 + uploaded_file 메타 저장
            var result = uploadManagerService.uploadTemp(file, folder);

            log.info("[Upload] ok folder={}, name={}, size={}, path={}",
                    folder, file.getOriginalFilename(), file.getSize(), result.path());

            // 3) 프론트 unwrapResponse 에 맞는 응답
            return ResponseEntity.ok(ApiResponse.ok(
                    UploadResponseDto.builder()
                            .path(result.path())
                            .url(result.url())
                            .build()
            ));

        } catch (IllegalArgumentException e) {
            log.warn("[Upload] bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));

        }catch (RuntimeException e) {
            log.warn("[Upload] runtime error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(ApiResponse.error(e.getMessage()));

        }  catch (Exception e) {
            log.error("[Upload] fail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("이미지 업로드 실패"));
        }
    }
}
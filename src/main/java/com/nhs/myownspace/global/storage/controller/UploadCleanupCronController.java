package com.nhs.myownspace.global.storage.controller;

import com.nhs.myownspace.global.storage.model.UploadedFile;
import com.nhs.myownspace.global.storage.service.UploadManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cron")
@RequiredArgsConstructor
public class UploadCleanupCronController {

    private final UploadManagerService uploadManagerService;

    @Value("${cron.cleanup-token}")
    private String cleanupToken;

    /**
     * 이틀 이상 지난 used=false 이미지 정리
     * cron-job.org 에서 이 URL을 주기적으로 호출
     */
    @PostMapping("/cleanup-unused-uploads")
    public ResponseEntity<String> cleanupUnusedUploads(
            @RequestHeader(name = "X-CRON-TOKEN", required = false) String token
    ) {
        if (token == null || !token.equals(cleanupToken)) {
            log.warn("[CronCleanup] invalid token: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("invalid token");
        }

        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(2);
            log.info("[CronCleanup] start cutoff={}", cutoff);

            List<UploadedFile> targets = uploadManagerService.findUnusedBefore(cutoff);
            int total = targets.size();
            log.info("[CronCleanup] found {} unused files before {}", total, cutoff);

            targets.forEach(uploadManagerService::deletePhysicalAndMeta);

            log.info("[CronCleanup] done deleted={}", total);
            return ResponseEntity.ok("deleted=" + total);

        } catch (Exception e) {
            log.error("[CronCleanup] unexpected failure", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("cron failed: " + e.getMessage());
        }
    }
}
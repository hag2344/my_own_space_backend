package com.nhs.myownspace.global.storage.media;

import com.nhs.myownspace.global.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThumbnailUrlResolver {

    private final StorageService storageService;

    public String resolveOrNull(String path) {
        if (path == null || path.isBlank()) return null;

        try {
            return storageService.createSignedUrl(path);
        } catch (Exception e) {
            log.warn("[Thumbnail] signed url skipped. path={}", path);
            return null;
        }
    }

    public String resolveFirstOrNull(List<String> paths) {
        if (paths == null || paths.isEmpty()) return null;

        return paths.stream()
                .filter(p -> p != null && !p.isBlank())
                .map(this::resolveOrNull)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}

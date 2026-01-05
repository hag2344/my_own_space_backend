package com.nhs.myownspace.global.storage.service;

import com.nhs.myownspace.global.storage.model.UploadFolder;
import com.nhs.myownspace.global.storage.model.UploadedFile;
import com.nhs.myownspace.global.storage.repository.UploadedFileRepository;
import com.nhs.myownspace.global.util.AuthUtil;
import com.nhs.myownspace.user.Provider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadManagerService {

    private final SupabaseStorageService storageService;
    private final UploadedFileRepository uploadedFileRepository;

    public record UploadResult(String path, String url) {}

    private ProviderContext getUser() {
        var user = AuthUtil.getLoginUserOrNull();
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        return new ProviderContext(user.provider(), user.providerId());
    }

    @Data
    public static class ProviderContext {
        private final Provider provider;
        private final String providerId;
    }

    // 임시 업로드
    public UploadResult uploadTemp(MultipartFile file, UploadFolder folder) throws Exception {
        var user = getUser();

        String path = storageService.uploadPrivate(folder, file);
        String url = storageService.createSignedUrl(path);

        UploadedFile meta = UploadedFile.builder()
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .path(path)
                .used(false)
                .build();

        uploadedFileRepository.save(meta);
        return new UploadResult(path, url);
    }

    // 글 저장 시 사용 확정 (String path 일 경우 오버로딩)
    public void markUsed(String refType, Long refId, String path) {
        if (path == null || path.isBlank()) return;

        markUsed(refType, refId, List.of(path));
    }

    // 글 저장 시 사용 확정
    public void markUsed(String refType, Long refId, Collection<String> paths) {
        if (paths == null || paths.isEmpty()) return;

        var user = getUser();

        List<UploadedFile> files =
                uploadedFileRepository.findByProviderAndProviderIdAndPathIn(
                        user.getProvider(),
                        user.getProviderId(),
                        paths
                );

        LocalDateTime now = LocalDateTime.now();
        files.forEach(f -> {
            f.setUsed(true);
            f.setRefType(refType);
            f.setRefId(refId);
            f.setUsedAt(now);
        });

        uploadedFileRepository.saveAll(files);
    }

    // 수정 시 빠진 이미지 삭제
    public void deleteRemovedOnUpdate(
            String refType,
            Long refId,
            Collection<String> oldPaths,
            Collection<String> newPaths
    ) {
        if (oldPaths == null || oldPaths.isEmpty()) return;

        Set<String> removed = new HashSet<>(oldPaths);
        if (newPaths != null) removed.removeAll(newPaths);
        if (removed.isEmpty()) return;

        var user = getUser();

        List<UploadedFile> files =
                uploadedFileRepository.findByProviderAndProviderIdAndPathIn(
                        user.getProvider(),
                        user.getProviderId(),
                        removed
                );

        files.stream()
                .filter(f -> refType.equals(f.getRefType())
                        && Objects.equals(refId, f.getRefId()))
                .forEach(this::deletePhysicalAndMeta);
    }

    // 아직 미사용 + 오래된 파일 정리용
    public List<UploadedFile> findUnusedBefore(LocalDateTime cutoff) {
        return uploadedFileRepository.findByUsedFalseAndCreatedAtBefore(cutoff);
    }

    // 공통 삭제 처리
    public void deletePhysicalAndMeta(UploadedFile file) {
        try {
            storageService.delete(file.getPath());
        } catch (Exception e) {
            log.warn("[UploadManager] Supabase delete 실패 path={}", file.getPath(), e);
        }
        uploadedFileRepository.delete(file);
    }

    // 글 삭제 시 이미지 전체 삭제
    public void deleteByRef(String refType, Long refId) {
        var user = getUser();

        List<UploadedFile> files =
                uploadedFileRepository.findByRefTypeAndRefIdAndProviderAndProviderId(
                        refType,
                        refId,
                        user.getProvider(),
                        user.getProviderId()
                );

        files.forEach(this::deletePhysicalAndMeta);
    }

}
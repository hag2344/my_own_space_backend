package com.nhs.myownspace.global.storage.repository;

import com.nhs.myownspace.global.storage.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    // 특정 사용자가 올린 파일들 중, path 목록에 포함되는 것들
    List<UploadedFile> findByUser_IdAndPathIn(
            Long userId,
            Collection<String> paths
    );

    // 아직 사용처(refType/refId) 안 묶인, 오래된 임시 업로드 정리용
    List<UploadedFile> findByUsedFalseAndCreatedAtBefore(LocalDateTime cutoff);

    // 특정 글(refType/refId)에 묶인 이미지들 찾기 (삭제 시 등)
    List<UploadedFile> findByRefTypeAndRefIdAndUser_Id(
            String refType,
            Long refId,
            Long userId
    );
}
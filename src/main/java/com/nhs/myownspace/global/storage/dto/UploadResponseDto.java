package com.nhs.myownspace.global.storage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadResponseDto {
    private String path; // DB 저장용
    private String url;  // 미리보기용 signed URL
}
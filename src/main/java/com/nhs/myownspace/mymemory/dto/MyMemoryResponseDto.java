package com.nhs.myownspace.mymemory.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyMemoryResponseDto {

    private Long id;
    private String title;

    // Toast UI HTML
    private String contentHtml;

    // 이 글에서 사용하는 이미지 path들 (프론트에서 필요하면 씀)
    private List<String> imagePaths;

    // 리스트용 썸네일 URL (첫 이미지 기준, 없으면 null)
    private String thumbnailUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
package com.nhs.myownspace.mymemory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyMemoryRequestDto {

    @NotBlank
    private String title;

    // Toast UI Editor 에서 넘어온 HTML
    @NotBlank
    private String contentHtml;

    // 이 글에서 실제로 사용하는 이미지 path 들
    @NotNull
    private List<String> imagePaths;
}
package com.nhs.myownspace.mymemory.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhs.myownspace.mymemory.entity.MyMemory;
import com.nhs.myownspace.user.Provider;

import java.util.Collections;
import java.util.List;

public class MyMemoryMapper {

    private static final ObjectMapper om = new ObjectMapper();

    /**
     * List<String> -> JSON
     */
    public static String toJson(List<String> paths) {
        try {
            if (paths == null) return "[]";
            return om.writeValueAsString(paths);
        } catch (Exception e) {
            throw new RuntimeException("imagePaths 직렬화 실패", e);
        }
    }

    /**
     * JSON -> List<String>
     */
    public static List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return om.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("imagePaths 역직렬화 실패", e);
        }
    }

    /**
     * Entity → Response DTO
     * 기본 버전 (contentHtml은 entity에 들어있는 그대로 사용)
     */
    public static MyMemoryResponseDto responseDto(MyMemory myMemory,
                                                  List<String> imagePaths,
                                                  String thumbnailUrl) {
        return responseDto(myMemory, imagePaths, thumbnailUrl, myMemory.getContentHtml());
    }

    /**
     * contentHtml을 외부에서 주입하고 싶을 때 사용하는 버전
     * - 상세 조회에서 "새로 signed URL로 바꾼 HTML"을 넣을 때 사용
     */
    public static MyMemoryResponseDto responseDto(MyMemory myMemory,
                                                  List<String> imagePaths,
                                                  String thumbnailUrl,
                                                  String contentHtml) {
        return MyMemoryResponseDto.builder()
                .id(myMemory.getId())
                .title(myMemory.getTitle())
                .contentHtml(contentHtml)
                .imagePaths(imagePaths)
                .thumbnailUrl(thumbnailUrl)
                .createdAt(myMemory.getCreatedAt())
                .updatedAt(myMemory.getUpdatedAt())
                .build();
    }

    /**
     * Request DTO → Entity (생성 시)
     */
    public static MyMemory createEntity(MyMemoryRequestDto req, Provider provider, String providerId) {
        if (req == null) return null;

        return MyMemory.builder()
                .provider(provider)
                .providerId(providerId)
                .title(req.getTitle())
                .contentHtml(req.getContentHtml())
                .imagePathsJson(toJson(req.getImagePaths()))
                .build();
    }

    /**
     * Request DTO → Entity 적용 (수정 시)
     */
    public static void updateEntity(MyMemory myMemory, MyMemoryRequestDto req) {
        if (myMemory == null || req == null) return;

        myMemory.setTitle(req.getTitle());
        myMemory.setContentHtml(req.getContentHtml());
        myMemory.setImagePathsJson(toJson(req.getImagePaths()));
    }
}
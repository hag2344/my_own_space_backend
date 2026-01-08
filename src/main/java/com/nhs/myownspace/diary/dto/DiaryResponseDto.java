package com.nhs.myownspace.diary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiaryResponseDto {

    private Long id;

    private String title;

    private String todayDate;
    private int weatherId;
    private String wakeUpTime;
    private String sleepTime;

    private String  content;

    private String todayImportantWork;
    private String todayKindWork;
    private String todaySelfReflection;
    private String tomorrowWork;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.nhs.myownspace.diary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DiaryRequestDto {
    @NotBlank
    private String title;

    // "2025-12-06"
    @NotBlank
    private String todayDate;

    private int weatherId;
    private String wakeUpTime;
    private String sleepTime;

    @NotBlank
    private String  content;

    private String todayImportantWork;
    private String todayKindWork;
    private String todaySelfReflection;
    private String tomorrowWork;
}

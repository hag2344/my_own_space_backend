package com.nhs.myownspace.schedule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponseDto {

    private Long id;

    private String title;

    // 프론트에서 쓰기 쉽게 String으로 내려줄 수도 있고,
    // LocalDateTime 그대로 내려도 됨. 여기서는 String으로 통일 예시.
    private String startDate;
    private String endDate;
    private boolean allDay;

    private String location;
    private String color;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
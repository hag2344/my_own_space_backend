package com.nhs.myownspace.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ScheduleRequestDto {
    @NotBlank
    private String title;

    // "2025-11-16T12:00" 또는 "2025-11-16"
    @NotBlank
    private String startDate;

    // "2025-11-16T13:00" 또는 "2025-11-16"
    @NotBlank
    private String  endDate;

    private boolean allDay;

    private String location;
    private String color;
    private String description;
}
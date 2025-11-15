package com.nhs.myownspace.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ScheduleRequest {
    private String title;
    private String startDate;
    private String  endDate;
    private boolean allDay;
    private String location;
    private String color;
    private String description;
}
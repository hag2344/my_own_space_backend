package com.nhs.myownspace.schedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ScheduleRequest {
    private String title;
    private String start;
    private String end;
    private boolean allDay;
    private String location;
    private String color;
    private String description;
}
package com.nhs.myownspace.schedule.dto;


import com.nhs.myownspace.global.util.DateFormatUtil;
import com.nhs.myownspace.schedule.entity.Schedule;
import com.nhs.myownspace.user.Provider;

public class ScheduleMapper {

    /**
     * Entity → Response DTO
     */
    public static ScheduleResponseDto responseDto(Schedule schedule) {
        if (schedule == null) return null;

        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .startDate(schedule.getStartDate() != null ? schedule.getStartDate().toString() : null)
                .endDate(schedule.getEndDate() != null ? schedule.getEndDate().toString() : null)
                .allDay(schedule.isAllDay())
                .location(schedule.getLocation())
                .color(schedule.getColor())
                .description(schedule.getDescription())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }

    /**
     * Request DTO → Entity (생성 시)
     */
    public static Schedule createEntity(ScheduleRequestDto req, Provider provider, String providerId) {
        if (req == null) return null;

        return Schedule.builder()
                .provider(provider)
                .providerId(providerId)
                .title(req.getTitle())
                .startDate(DateFormatUtil.toDateTime(req.getStartDate(), req.isAllDay(), false))
                .endDate(DateFormatUtil.toDateTime(req.getEndDate(), req.isAllDay(), true))
                .allDay(req.isAllDay())
                .location(req.getLocation())
                .color(req.getColor())
                .description(req.getDescription())
                .build();
    }

    /**
     * Request DTO → Entity 적용 (수정 시)
     */
    public static void updateEntity(Schedule schedule, ScheduleRequestDto req) {
        if (schedule == null || req == null) return;

        schedule.setTitle(req.getTitle());
        schedule.setStartDate(DateFormatUtil.toDateTime(req.getStartDate(), req.isAllDay(), false));
        schedule.setEndDate(DateFormatUtil.toDateTime(req.getEndDate(), req.isAllDay(), true));
        schedule.setAllDay(req.isAllDay());
        schedule.setLocation(req.getLocation());
        schedule.setColor(req.getColor());
        schedule.setDescription(req.getDescription());
    }
}
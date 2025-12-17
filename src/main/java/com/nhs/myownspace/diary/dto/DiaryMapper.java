package com.nhs.myownspace.diary.dto;

import com.nhs.myownspace.global.util.DateFormatUtil;
import com.nhs.myownspace.diary.entity.Diary;
import com.nhs.myownspace.user.Provider;

public class DiaryMapper {

    /**
     * Entity → Response DTO
     */
    public static DiaryResponseDto responseDto(Diary diary) {
        if (diary == null) return null;

        return DiaryResponseDto.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .todayDate(diary.getTodayDate() != null ? diary.getTodayDate().toString() : null)
                .weatherId(diary.getWeatherId())
                .wakeUpTime(diary.getWakeUpTime() != null ? diary.getWakeUpTime().toString() : null)
                .sleepTime(diary.getSleepTime() != null ? diary.getSleepTime().toString() : null)
                .content(diary.getContent())
                .todayImportantWork(diary.getTodayImportantWork())
                .todayKindWork(diary.getTodayKindWork())
                .todaySelfReflection(diary.getTodaySelfReflection())
                .tomorrowWork(diary.getTomorrowWork())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .build();
    }

    /**
     * Request DTO → Entity (생성 시)
     */
    public static Diary createEntity(DiaryRequestDto req, Provider provider, String providerId) {
        if (req == null) return null;

        return Diary.builder()
                .provider(provider)
                .providerId(providerId)
                .title(req.getTitle())
                .todayDate(DateFormatUtil.toDate(req.getTodayDate()))
                .weatherId(req.getWeatherId())
                .wakeUpTime(DateFormatUtil.toTime(req.getWakeUpTime()))
                .sleepTime(DateFormatUtil.toTime(req.getSleepTime()))
                .content(req.getContent())
                .todayImportantWork(req.getTodayImportantWork())
                .todayKindWork(req.getTodayKindWork())
                .todaySelfReflection(req.getTodaySelfReflection())
                .tomorrowWork(req.getTomorrowWork())
                .build();
    }

    /**
     * Request DTO → Entity 적용 (수정 시)
     */
    public static void updateEntity(Diary diary, DiaryRequestDto req) {
        if (diary == null || req == null) return;

        diary.setTitle(req.getTitle());
        diary.setTodayDate(DateFormatUtil.toDate(req.getTodayDate()));
        diary.setWeatherId(req.getWeatherId());
        diary.setWakeUpTime(DateFormatUtil.toTime(req.getWakeUpTime()));
        diary.setSleepTime(DateFormatUtil.toTime(req.getSleepTime()));
        diary.setContent(req.getContent());
        diary.setTodayImportantWork(req.getTodayImportantWork());
        diary.setTodayKindWork(req.getTodayKindWork());
        diary.setTodaySelfReflection(req.getTodaySelfReflection());
        diary.setTomorrowWork(req.getTomorrowWork());
    }
}

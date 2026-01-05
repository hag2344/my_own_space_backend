package com.nhs.myownspace.home.dto;

import com.nhs.myownspace.bookreport.entity.BookReport;
import com.nhs.myownspace.diary.entity.Diary;
import com.nhs.myownspace.schedule.entity.Schedule;
import com.nhs.myownspace.mymemory.entity.MyMemory;

public class HomeMapper {

    public static HomeScheduleItemDto toScheduleDto(Schedule s) {
        return HomeScheduleItemDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .startDate(s.getStartDate())
                .build();
    }

    public static HomeDiaryItemDto toDiaryDto(Diary d) {
        return HomeDiaryItemDto.builder()
                .id(d.getId())
                .title(d.getTitle())
                .todayDate(d.getTodayDate())
                .build();
    }

    public static HomeMyMemoryItemDto toMemoryDto(MyMemory m, String thumbnailUrl) {
        return HomeMyMemoryItemDto.builder()
                .id(m.getId())
                .title(m.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .createdAt(m.getCreatedAt())
                .build();
    }

    public static HomeBookReportItemDto toBookDto(BookReport b, String thumbnailUrl) {
        return HomeBookReportItemDto.builder()
                .id(b.getId())
                .bookName(b.getBookName())
                .thumbnailUrl(thumbnailUrl)
                .createdAt(b.getCreatedAt())
                .build();
    }


}

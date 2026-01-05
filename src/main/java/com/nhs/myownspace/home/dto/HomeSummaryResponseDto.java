package com.nhs.myownspace.home.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class HomeSummaryResponseDto {

    private int todayScheduleCount;
    private List<HomeScheduleItemDto> todaySchedules; // 최대 3개

    private HomeDiaryItemDto todayDiary; // 없으면 null

    private List<HomeMyMemoryItemDto> latestMemories; // 최대 3개
    private List<HomeBookReportItemDto> latestBookReports; // 최대 3개
}

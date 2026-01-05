package com.nhs.myownspace.home.service;

import com.nhs.myownspace.global.storage.media.ThumbnailUrlResolver;
import com.nhs.myownspace.home.dto.*;
import com.nhs.myownspace.auth.dto.LoginUser;
import com.nhs.myownspace.global.util.AuthUtil;
import com.nhs.myownspace.mymemory.dto.MyMemoryMapper;
import com.nhs.myownspace.user.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nhs.myownspace.bookreport.entity.BookReport;
import com.nhs.myownspace.bookreport.repository.BookReportRepository;
import com.nhs.myownspace.diary.entity.Diary;
import com.nhs.myownspace.diary.repository.DiaryRepository;
import com.nhs.myownspace.mymemory.entity.MyMemory;
import com.nhs.myownspace.mymemory.repository.MyMemoryRepository;
import com.nhs.myownspace.schedule.entity.Schedule;
import com.nhs.myownspace.schedule.repository.ScheduleRepository;
import com.nhs.myownspace.home.dto.HomeMapper;
import com.nhs.myownspace.global.storage.service.StorageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final ScheduleRepository scheduleRepository;
    private final DiaryRepository diaryRepository;
    private final MyMemoryRepository myMemoryRepository;
    private final BookReportRepository bookReportRepository;
    private final StorageService storageService;
    private final ThumbnailUrlResolver thumbnailUrlResolver;

    /**
     * 홈 화면 메뉴 요약 정보 조회
     */
    public HomeSummaryResponseDto getSummary() {

        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청 - 홈 요약 정보 조회");
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        try {

            Provider provider = userInfo.provider();
            String providerId = userInfo.providerId();

            // 1) 오늘 일정: start <= today <= end
            int todayCount = scheduleRepository.countOverlappingDay(provider,providerId,startOfDay, endOfDay);
            List<Schedule> todayTop3 = scheduleRepository.findTodayTop3(provider,providerId,startOfDay, endOfDay, PageRequest.of(0, 3));
            // 2) 오늘 일기(있으면 1개) - 너 구조에 맞춰 메서드명 조정
            Diary todayDiary = diaryRepository.findTopByProviderAndProviderIdAndTodayDateOrderByCreatedAtDesc(provider, providerId,today).orElse(null);

            // 3) 최신 내추억 3개
            List<MyMemory> memories = myMemoryRepository.findTop3ByProviderAndProviderIdOrderByCreatedAtDesc(provider, providerId);

            // 4) 최신 독서기록 3개
            List<BookReport> books = bookReportRepository.findTop3ByProviderAndProviderIdOrderByCreatedAtDesc(provider, providerId);

            return HomeSummaryResponseDto.builder()
                    .todayScheduleCount(todayCount)
                    .todaySchedules(todayTop3.stream().map(HomeMapper::toScheduleDto).toList())
                    .todayDiary(todayDiary != null ? HomeMapper.toDiaryDto(todayDiary) : null)
                    .latestMemories(
                            memories.stream()
                                    .map(m -> {
                                        String thumbnailUrl = extractMemoryThumbnail(m);
                                        return HomeMapper.toMemoryDto(m, thumbnailUrl);
                                    })
                                    .toList()
                            )
                    .latestBookReports(
                            books.stream()
                                    .map(b -> {
                                        String thumbnailUrl = extractBookThumbnail(b);
                                        return HomeMapper.toBookDto(b, thumbnailUrl);
                                    })
                                    .toList()
                    )
                    .build();
        } catch (Exception e) {
            log.error("홈 요약 정보 조회 실패", e);
            throw new RuntimeException("홈 요약 정보 조회 실패");
        }
    }

    private String extractMemoryThumbnail(MyMemory m) {
        List<String> paths = MyMemoryMapper.fromJson(m.getImagePathsJson());
        if (paths == null || paths.isEmpty()) return null;
        return thumbnailUrlResolver.resolveFirstOrNull(paths);
    }

    private String extractBookThumbnail(BookReport b) {
        String path = b.getImagePath();
        return thumbnailUrlResolver.resolveOrNull(path);
    }


}

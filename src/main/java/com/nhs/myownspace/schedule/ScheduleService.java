package com.nhs.myownspace.schedule;

import com.nhs.myownspace.common.util.AuthUtil;
import com.nhs.myownspace.schedule.dto.ScheduleRequest;
import com.nhs.myownspace.user.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /**
     *  내 일정 전체 조회
     */
    public List<Schedule> getMySchedules() {

        var userInfo = AuthUtil.getLoginUser();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청");
            return null;
        }

        Provider provider = Provider.valueOf(userInfo.get("provider").toUpperCase());
        String providerId = userInfo.get("providerId");

        try {
            return scheduleRepository.findByProviderAndProviderId(provider, providerId);
        } catch (Exception e) {
            log.error("일정 조회 실패", e);
            throw new RuntimeException("일정 조회 실패");
        }
    }

    /**
     * 일정 생성
     */
    public Schedule create(ScheduleRequest req) {

        var userInfo = AuthUtil.getLoginUser();
        if (userInfo == null) return null;

        Provider provider = Provider.valueOf(userInfo.get("provider").toUpperCase());
        String providerId = userInfo.get("providerId");

        try {
            Schedule schedule = Schedule.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .title(req.getTitle())
                    .startDate(req.getStart())
                    .endDate(req.getEnd())
                    .allDay(req.isAllDay())
                    .location(req.getLocation())
                    .color(req.getColor())
                    .description(req.getDescription())
                    .build();

            return scheduleRepository.save(schedule);
        } catch (Exception e) {
            log.error("일정 생성 실패", e);
            throw new RuntimeException("일정 생성 실패");
        }
    }

    /**
     * 일정 수정
     */
    public Schedule update(Long id, ScheduleRequest req) {
        try {
            Schedule schedule = scheduleRepository.findById(id).orElse(null);

            if (schedule == null) {
                log.warn("수정할 일정 없음 - id={}", id);
                return null;
            }

            schedule.setTitle(req.getTitle());
            schedule.setStartDate(req.getStart());
            schedule.setEndDate(req.getEnd());
            schedule.setAllDay(req.isAllDay());
            schedule.setLocation(req.getLocation());
            schedule.setColor(req.getColor());
            schedule.setDescription(req.getDescription());

            return scheduleRepository.save(schedule);

        } catch (Exception e) {
            log.error("일정 수정 실패", e);
            throw new RuntimeException("일정 수정 실패");
        }
    }

    /**
     * 일정 삭제
     */
    public boolean delete(Long id) {
        try {
            if (!scheduleRepository.existsById(id)) {
                log.warn("삭제할 일정 없음 - id={}", id);
                return false;
            }

            scheduleRepository.deleteById(id);
            return true;

        } catch (Exception e) {
            log.error("일정 삭제 실패", e);
            throw new RuntimeException("일정 삭제 실패");
        }
    }
}

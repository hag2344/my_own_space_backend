package com.nhs.myownspace.schedule.service;

import com.nhs.myownspace.global.util.AuthUtil;
import com.nhs.myownspace.schedule.entity.Schedule;
import com.nhs.myownspace.schedule.dto.ScheduleRequestDto;
import com.nhs.myownspace.schedule.dto.ScheduleResponseDto;
import com.nhs.myownspace.schedule.dto.ScheduleMapper;
import com.nhs.myownspace.schedule.repository.ScheduleRepository;
import com.nhs.myownspace.user.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /**
     *  내 일정 전체 조회
     */
    public List<ScheduleResponseDto> getMySchedules() {

        var userInfo = AuthUtil.getLoginUser();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청 - 일정 조회");
            return null;
        }

        Provider provider = Provider.valueOf(userInfo.get("provider").toUpperCase());
        String providerId = userInfo.get("providerId");

        try {
            List<Schedule> result = scheduleRepository.findByProviderAndProviderId(provider, providerId);
            return result.stream()
                    .map(ScheduleMapper::responseDto)
                    .toList();
        } catch (Exception e) {
            log.error("일정 조회 실패", e);
            throw new RuntimeException("일정 조회 실패");
        }
    }

    /**
     * 일정 생성
     */
    public ScheduleResponseDto  create(ScheduleRequestDto req) {
        var userInfo = AuthUtil.getLoginUser();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 일정 생성 실패");
            return null;
        }

        Provider provider = Provider.valueOf(userInfo.get("provider").toUpperCase());
        String providerId = userInfo.get("providerId");

        try {
            Schedule schedule = ScheduleMapper.createEntity(req, provider, providerId);
            Schedule saved = scheduleRepository.save(schedule);
            return ScheduleMapper.responseDto(saved);
        } catch (Exception e) {
            log.error("일정 생성 실패", e);
            throw new RuntimeException("일정 생성 실패");
        }
    }

    /**
     * 일정 수정
     */
    public ScheduleResponseDto  update(Long id, ScheduleRequestDto req) {
        try {

            Schedule schedule = scheduleRepository.findById(id).orElse(null);

            if (schedule == null) {
                log.warn("수정할 일정 없음 - id={}", id);
                return null;
            }

            Boolean rightsCheck = AuthUtil.writerRightsCheck("update",schedule.getProviderId(), schedule.getProvider());

            if (!rightsCheck){
                log.warn("일정 수정 권한 없음 - id={}", id);
                return null;
            }

            // Request DTO → Entity 반영
            ScheduleMapper.updateEntity(schedule, req);

            Schedule updated = scheduleRepository.save(schedule);
            return ScheduleMapper.responseDto(updated);

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

            Schedule schedule = scheduleRepository.findById(id).orElse(null);

            if (schedule == null) {
                log.warn("삭제할 일정 없음 - id={}", id);
                return false;
            }

            Boolean rightsCheck = AuthUtil.writerRightsCheck("delete",schedule.getProviderId(), schedule.getProvider());

            if (!rightsCheck){
                log.warn("일정 삭제 권한 없음 - id={}", id);
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

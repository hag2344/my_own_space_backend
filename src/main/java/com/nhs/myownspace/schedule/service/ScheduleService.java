package com.nhs.myownspace.schedule.service;

import com.nhs.myownspace.auth.dto.LoginUser;
import com.nhs.myownspace.global.util.AuthUtil;
import com.nhs.myownspace.schedule.entity.Schedule;
import com.nhs.myownspace.schedule.dto.ScheduleRequestDto;
import com.nhs.myownspace.schedule.dto.ScheduleResponseDto;
import com.nhs.myownspace.schedule.dto.ScheduleMapper;
import com.nhs.myownspace.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /**
     *  내 일정 전체 조회
     */
    public List<ScheduleResponseDto> getMySchedules() {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청 - 일정 조회");
            return null;
        }

        try {
            List<Schedule> result = scheduleRepository.findByUser_Id(userInfo.userId());
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
    @Transactional
    public ScheduleResponseDto create(ScheduleRequestDto req) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 일정 생성 실패");
            return null;
        }

        try {
            Schedule schedule = ScheduleMapper.createEntity(req, userInfo.userId());
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
    @Transactional
    public ScheduleResponseDto  update(Long id, ScheduleRequestDto req) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 일정 수정 실패");
            return null;
        }

        try {
            Schedule schedule = scheduleRepository
                    .findByIdAndUser_Id(id, userInfo.userId())
                    .orElse(null);

            if (schedule == null) {
                log.warn("수정 실패(없음/권한없음) - id={}", id);
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
    @Transactional
    public boolean delete(Long id) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 일정 삭제 실패");
            return false;
        }

        try {
            Schedule schedule = scheduleRepository
                    .findByIdAndUser_Id(id, userInfo.userId())
                    .orElse(null);

            if (schedule == null) {
                log.warn("삭제 실패(없음/권한없음) - id={}", id);
                return false;
            }

            scheduleRepository.delete(schedule);

            return true;

        } catch (Exception e) {
            log.error("일정 삭제 실패", e);
            throw new RuntimeException("일정 삭제 실패");
        }
    }
}

package com.nhs.myownspace.diary.service;

import com.nhs.myownspace.auth.dto.LoginUser;
import com.nhs.myownspace.global.util.AuthUtil;
import com.nhs.myownspace.diary.entity.Diary;
import com.nhs.myownspace.diary.dto.DiaryRequestDto;
import com.nhs.myownspace.diary.dto.DiaryResponseDto;
import com.nhs.myownspace.diary.dto.DiaryMapper;
import com.nhs.myownspace.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;

    /**
     *  내 일기 조회
     */
    public Page<DiaryResponseDto> getMyDiaries(int page, int size, String q) {

        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청 - 일기 조회");
            return null;
        }

        try {
            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(
                    Sort.Order.desc("todayDate"),
                            Sort.Order.desc("updatedAt"),
                            Sort.Order.desc("createdAt")
                    )
            );

            String keyword = (q==null) ? "" : q.trim();

            Page<Diary> result;
            if(keyword.isEmpty()){
                result = diaryRepository.findByUser_Id(userInfo.userId(), pageable);
            }else {
                result = diaryRepository.findByUser_IdAndTitleContainingIgnoreCase(
                        userInfo.userId(), keyword, pageable
                );
            }

            return result.map(DiaryMapper::responseDto);
        } catch (Exception e) {
            log.error("일기 조회 실패", e);
            throw new RuntimeException("일기 조회 실패");
        }
    }

    /**
     * 일기 id로 조회
     */
    public DiaryResponseDto getMyDiary(Long id) {
        try {
            LoginUser userInfo = AuthUtil.getLoginUserOrNull();
            if (userInfo == null) {
                log.warn("인증되지 않은 사용자 요청 - 일기 단건 조회");
                return null;
            }

            Diary diary = diaryRepository.findByIdAndUser_Id(id,userInfo.userId()).orElse(null);
            if (diary == null) {
                log.warn("조회할 일기 없음 - id={}", id);
                return null; // 없음
            }

            return DiaryMapper.responseDto(diary);

        } catch (Exception e) {
            log.error("일기 단건 조회 실패 - id={}", id, e);
            throw new RuntimeException("일기 단건 조회 실패");
        }
    }

    /**
     * 일기 생성
     */
    @Transactional
    public DiaryResponseDto  create(DiaryRequestDto req) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 일기 생성 실패");
            return null;
        }

        try {
            Diary diary = DiaryMapper.createEntity(req, userInfo.userId());
            Diary saved = diaryRepository.save(diary);
            return DiaryMapper.responseDto(saved);
        } catch (Exception e) {
            log.error("일기 생성 실패", e);
            throw new RuntimeException("일기 생성 실패");
        }
    }

    /**
     * 일기 수정
     */
    @Transactional
    public DiaryResponseDto  update(Long id, DiaryRequestDto req) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 일기 수정 실패");
            return null;
        }

        try {
            Diary diary = diaryRepository
                    .findByIdAndUser_Id(id, userInfo.userId())
                    .orElse(null);

            if (diary == null) {
                log.warn("수정 실패(없음/권한없음) - id={}", id);
                return null;
            }

            // Request DTO → Entity 반영
            DiaryMapper.updateEntity(diary, req);

            Diary updated = diaryRepository.save(diary);
            return DiaryMapper.responseDto(updated);

        } catch (Exception e) {
            log.error("일기 수정 실패", e);
            throw new RuntimeException("일기 수정 실패");
        }
    }

    /**
     * 일정 삭제
     */
    @Transactional
    public boolean delete(Long id) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 일기 삭제 실패");
            return false;
        }

        try {

            Diary diary = diaryRepository
                    .findByIdAndUser_Id(id, userInfo.userId())
                    .orElse(null);

            if (diary == null) {
                log.warn("삭제 실패(없음/권한없음) - id={}", id);
                return false;
            }

            diaryRepository.delete(diary);

            return true;

        } catch (Exception e) {
            log.error("일기 삭제 실패", e);
            throw new RuntimeException("일기 삭제 실패");
        }
    }
}

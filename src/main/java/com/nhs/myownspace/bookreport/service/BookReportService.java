package com.nhs.myownspace.bookreport.service;

import com.nhs.myownspace.auth.dto.LoginUser;
import com.nhs.myownspace.global.storage.service.StorageService;
import com.nhs.myownspace.global.storage.service.UploadManagerService;
import com.nhs.myownspace.global.util.AuthUtil;
import com.nhs.myownspace.bookreport.dto.BookReportMapper;
import com.nhs.myownspace.bookreport.dto.BookReportRequestDto;
import com.nhs.myownspace.bookreport.dto.BookReportResponseDto;
import com.nhs.myownspace.bookreport.entity.BookReport;
import com.nhs.myownspace.bookreport.repository.BookReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookReportService {

    private static final String REF_TYPE = "BOOKREPORT";

    private final BookReportRepository bookReportRepository;
    private final StorageService storageService;
    private final UploadManagerService uploadManagerService;

    /**
     *  독서 기록 조회
     */
    public Page<BookReportResponseDto> getBookReports(int page, int size, String q) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청 - 독서 기록 조회");
            return null;
        }
        try{
            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(
                            Sort.Order.desc("createdAt"),
                            Sort.Order.desc("updatedAt")
                    )
            );

            String keyword = (q==null) ? "" : q.trim();

            Page<BookReport> result;
            if (keyword.isEmpty()){
                result = bookReportRepository.findByProviderAndProviderId(
                        userInfo.provider(),
                        userInfo.providerId(),
                        pageable
                );
            }else{
                result = bookReportRepository.findByProviderAndProviderIdAndBookNameContainingIgnoreCase(
                        userInfo.provider(),
                        userInfo.providerId(),
                        keyword,
                        pageable
                );
            }

            return result.map(b -> {
                String path = b.getImagePath();
                String thumbnailUrl = (path != null && !path.isBlank())
                        ? storageService.createSignedUrl(path) // 매 조회마다 새 signed URL 발급
                        : null;
                return  BookReportMapper.responseDto(b, thumbnailUrl);
            });

        }catch (Exception e){
            log.error("독서 기록 조회 실패", e);
            throw new RuntimeException("독서 기록 조회 실패");
        }
    }

    /**
     * 독서 기록 id로 조회
     */
    public BookReportResponseDto getBookReport(Long id) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청 - 독서 기록 단건 조회");
            return null;
        }

        try {
            BookReport bookReport = bookReportRepository.findByIdAndProviderAndProviderId(
                            id, userInfo.provider(), userInfo.providerId())
                    .orElse(null);

            if (bookReport == null) {
                log.warn("조회할 독서 기록 없음 id={}", id);
                return null;
            }

            String path = bookReport.getImagePath();

            String thumbnailUrl = (path != null && !path.isBlank())
                    ? storageService.createSignedUrl(path)
                    : null ;

            return  BookReportMapper.responseDto(bookReport, thumbnailUrl);

        }catch (Exception e){
            log.error("독서 기록 단건 조회 실패 id={}", id, e);
            throw new RuntimeException("독서 기록 단건 조회 실패");
        }
    }

    /**
     * 독서 기록 생성
     */
    @Transactional
    public BookReportResponseDto create(BookReportRequestDto req) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 독서 기록 생성 실패");
            return null;
        }

        try{
            BookReport bookReport = BookReportMapper.createEntity(req, userInfo.provider(), userInfo.providerId());
            BookReport saved = bookReportRepository.save(bookReport);
            log.info("독서 기록 생성 완료 id={}", saved.getId());

            uploadManagerService.markUsed(REF_TYPE, saved.getId(), req.getImagePath());

            return getBookReport(saved.getId());

        }catch (Exception e){
            log.error("독서 기록 생성 실패", e);
            throw new RuntimeException("독서 기록 생성 실패");
        }
    }

    /**
     * 독서 기록 수정
     */
    @Transactional
    public BookReportResponseDto update(Long id, BookReportRequestDto req){
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 독서 기록 수정 실패");
            return null;
        }

        try{
            BookReport bookReport = bookReportRepository.findByIdAndProviderAndProviderId(
                            id, userInfo.provider(), userInfo.providerId())
                    .orElse(null);

            if(bookReport == null){
                log.warn("수정 실패(없음/권한없음) id={}", id);
                return null;
            }

            String oldPath = bookReport.getImagePath();

            BookReportMapper.updateEntity(bookReport, req);

            BookReport updated = bookReportRepository.save(bookReport);
            log.info("독서 기록 수정 완료 id={}", updated.getId());

            uploadManagerService.markUsed(REF_TYPE, updated.getId(), req.getImagePath());

            uploadManagerService.deleteRemovedOnUpdate(
                    REF_TYPE,
                    updated.getId(),
                    oldPath == null || oldPath.isBlank() ? List.of() : List.of(oldPath),
                    req.getImagePath() == null || req.getImagePath().isBlank() ? List.of() : List.of(req.getImagePath())
            );

            return  getBookReport(updated.getId());

        }catch (Exception e){
            log.error("독서 기록 수정 실패 id={}", id, e);
            throw new RuntimeException("독서 기록 수정 실패");
        }
    }

    /**
     * 독서 기록 삭제
     */
    @Transactional
    public boolean delete(Long id){
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 독서 기록 삭제 실패");
            return false;
        }

        try{
            BookReport bookReport = bookReportRepository.findByIdAndProviderAndProviderId(
                            id, userInfo.provider(), userInfo.providerId())
                    .orElse(null);

            if(bookReport == null){
                log.warn("삭제 실패(없음/권한없음) id={}", id);
                return false;
            }

            uploadManagerService.deleteByRef(REF_TYPE, bookReport.getId());

            bookReportRepository.delete(bookReport);
            log.info("독서 기록 삭제 성공 id={}", id);
            return true;

        }catch (Exception e){
            log.error("독서 기록 삭제 실패 id={}", id, e);
            throw new RuntimeException("독서 기록 삭제 실패");
        }
    }
}

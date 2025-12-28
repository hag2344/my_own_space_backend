package com.nhs.myownspace.mymemory.service;

import com.nhs.myownspace.auth.dto.LoginUser;
import com.nhs.myownspace.global.storage.service.StorageService;
import com.nhs.myownspace.global.storage.service.UploadManagerService;
import com.nhs.myownspace.global.storage.html.SupabaseImageHtmlRewriter;
import com.nhs.myownspace.global.util.AuthUtil;
import com.nhs.myownspace.mymemory.dto.MyMemoryMapper;
import com.nhs.myownspace.mymemory.dto.MyMemoryRequestDto;
import com.nhs.myownspace.mymemory.dto.MyMemoryResponseDto;
import com.nhs.myownspace.mymemory.entity.MyMemory;
import com.nhs.myownspace.mymemory.repository.MyMemoryRepository;
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
public class MyMemoryService {

    private static final String REF_TYPE = "MYMEMORY";

    private final MyMemoryRepository myMemoryRepository;
    private final StorageService storageService;      // 썸네일용 signed URL
    private final UploadManagerService uploadManagerService; // 이미지 메타/정리
    private final SupabaseImageHtmlRewriter supabaseImageHtmlRewriter;

    /**
     *  내 추억 조회
     */
    public Page<MyMemoryResponseDto> getMyMemories(int page, int size, String q) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청 - 내 추억 조회");
            return null;
        }

        try {
            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(
                            Sort.Order.desc("createdAt"),
                            Sort.Order.desc("updatedAt")
                    )
            );

            String keyword = (q==null) ? "" : q.trim();

            Page<MyMemory> result;
            if (keyword.isEmpty()) {
                // 검색어 없으면 기존처럼 전체 조회
                result = myMemoryRepository.findByProviderAndProviderId(
                        userInfo.provider(),
                        userInfo.providerId(),
                        pageable
                );
            } else {
                // 검색어 있으면 제목/내용 검색
                result = myMemoryRepository.searchByProviderAndProviderIdAndKeyword(
                        userInfo.provider(),
                        userInfo.providerId(),
                        keyword,
                        pageable
                );
            }

            // imagePathsJson → List<String> + 썸네일 Signed URL 생성
            return result.map(m -> {
                List<String> paths = MyMemoryMapper.fromJson(m.getImagePathsJson());

                // 첫 이미지 기준 썸네일 URL
                String thumbnailUrl = paths.stream()
                        .filter(p -> p != null && !p.isBlank())
                        .findFirst()
                        .map(storageService::createSignedUrl) // 매 조회마다 새 signed URL 발급
                        .orElse(null);

                return MyMemoryMapper.responseDto(m, paths, thumbnailUrl);
            });

        } catch (Exception e) {
            log.error("내 추억 조회 실패", e);
            throw new RuntimeException("내 추억 조회 실패");
        }
    }

    /**
     * 내 추억 id로 조회
     * - DB contentHtml 안에 있는 img src를 모두 fresh signed URL로 교체해서 내려준다.
     */
    public MyMemoryResponseDto getMyMemory(Long id) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청 - 내 추억 단건 조회");
            return null;
        }

        try {
            MyMemory myMemory = myMemoryRepository.findByIdAndProviderAndProviderId(
                            id, userInfo.provider(), userInfo.providerId())
                    .orElse(null);

            if (myMemory == null) {
                log.warn("조회할 내 추억 없음 id={}", id);
                return null;
            }

            // DB에 저장된 path 목록
            List<String> paths = MyMemoryMapper.fromJson(myMemory.getImagePathsJson());

            // 썸네일용(리스트에서 쓰는 것과 동일)
            String thumbnailUrl = paths.stream()
                    .filter(p -> p != null && !p.isBlank())
                    .findFirst()
                    .map(storageService::createSignedUrl) // 상세에서도 썸네일용 fresh URL
                    .orElse(null);

            // HTML 안의 img src를 모두 fresh signed URL로 갈아끼움
            String originalHtml = myMemory.getContentHtml();
            String signedHtml = supabaseImageHtmlRewriter.rewriteImgSrcWithSignedUrl(originalHtml);

            // Mapper에 "재작성된 HTML"을 넘겨서 DTO 생성
            return MyMemoryMapper.responseDto(myMemory, paths, thumbnailUrl, signedHtml);

        } catch (Exception e) {
            log.error("내 추억 단건 조회 실패 id={}", id, e);
            throw new RuntimeException("추억 단건 조회 실패");
        }
    }

    /**
     * 내 추억 생성
     */
    @Transactional
    public MyMemoryResponseDto create(MyMemoryRequestDto req) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 내 추억 생성 실패");
            return null;
        }

        try {
            MyMemory myMemory = MyMemoryMapper.createEntity(req, userInfo.provider(), userInfo.providerId());
            MyMemory saved = myMemoryRepository.save(myMemory);
            log.info("내 추억 생성 완료 id={}", saved.getId());

            // 이 글에서 사용하는 이미지들 used=true + refType/refId 매핑
            uploadManagerService.markUsed(REF_TYPE, saved.getId(), req.getImagePaths());

            return getMyMemory(saved.getId());
        } catch (Exception e) {
            log.error("내 추억 생성 실패", e);
            throw new RuntimeException("내 추억 생성 실패");
        }
    }

    /**
     * 내 추억 수정
     */
    @Transactional
    public MyMemoryResponseDto update(Long id, MyMemoryRequestDto req) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 내 추억 수정 실패");
            return null;
        }

        try {
            MyMemory myMemory = myMemoryRepository.findByIdAndProviderAndProviderId(
                            id, userInfo.provider(), userInfo.providerId())
                    .orElse(null);

            if (myMemory == null) {
                log.warn("수정 실패(없음/권한없음) id={}", id);
                return null;
            }

            // 기존 이미지 목록
            List<String> oldPaths = MyMemoryMapper.fromJson(myMemory.getImagePathsJson());

            // Request DTO → Entity 반영
            MyMemoryMapper.updateEntity(myMemory, req);

            MyMemory updated = myMemoryRepository.save(myMemory);
            log.info("내 추억 수정 완료 id={}", updated.getId());

            // 새로 사용되는 이미지들 마킹
            uploadManagerService.markUsed(REF_TYPE, updated.getId(), req.getImagePaths());
            // 더 이상 쓰이지 않는 이미지 삭제
            uploadManagerService.deleteRemovedOnUpdate(REF_TYPE, updated.getId(), oldPaths, req.getImagePaths());

            return getMyMemory(updated.getId());

        } catch (Exception e) {
            log.error("내 추억 수정 실패 id={}", id, e);
            throw new RuntimeException("내 추억 수정 실패");
        }
    }

    /**
     * 내 추억 삭제
     */
    @Transactional
    public boolean delete(Long id) {
        LoginUser userInfo = AuthUtil.getLoginUserOrNull();
        if (userInfo == null) {
            log.warn("인증 정보 없음 - 내 추억 삭제 실패");
            return false;
        }

        try {
            MyMemory myMemory = myMemoryRepository.findByIdAndProviderAndProviderId(
                            id, userInfo.provider(), userInfo.providerId())
                    .orElse(null);

            if (myMemory == null) {
                log.warn("삭제 실패(없음/권한없음) id={}", id);
                return false;
            }

            // 이 글에 매핑된 이미지 전부 삭제
            uploadManagerService.deleteByRef(REF_TYPE, myMemory.getId());

            myMemoryRepository.delete(myMemory);
            log.info("내 추억 삭제 성공 id={}", id);
            return true;

        } catch (Exception e) {
            log.error("내 추억 삭제 실패 id={}", id, e);
            throw new RuntimeException("내 추억 삭제 실패");
        }
    }
}
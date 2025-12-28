package com.nhs.myownspace.mymemory.repository;


import com.nhs.myownspace.mymemory.entity.MyMemory;
import com.nhs.myownspace.user.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MyMemoryRepository extends JpaRepository<MyMemory, Long> {
    Page<MyMemory> findByProviderAndProviderId(
            Provider provider,
            String providerId,
            Pageable pageable
    );

    Optional<MyMemory> findByIdAndProviderAndProviderId(
            Long id,
            Provider provider,
            String providerId
    );

    // 제목/내용에 keyword 포함 검색
    @Query("""
        select m
        from MyMemory m
        where m.provider = :provider
          and m.providerId = :providerId
          and (
            lower(m.title) like lower(concat('%', :keyword, '%'))
            or lower(m.contentHtml) like lower(concat('%', :keyword, '%'))
          )
        """)
    Page<MyMemory> searchByProviderAndProviderIdAndKeyword(
            @Param("provider") Provider provider,
            @Param("providerId") String providerId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
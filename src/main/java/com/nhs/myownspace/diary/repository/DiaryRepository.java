package com.nhs.myownspace.diary.repository;

import com.nhs.myownspace.diary.entity.Diary;
import com.nhs.myownspace.user.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Page<Diary> findByProviderAndProviderId(Provider provider, String providerId, Pageable pageable);

    Page<Diary> findByProviderAndProviderIdAndTitleContainingIgnoreCase(
            Provider provider, String providerId, String title, Pageable pageable
    );

    Optional<Diary> findByIdAndProviderAndProviderId(Long id, Provider provider, String providerId);
}

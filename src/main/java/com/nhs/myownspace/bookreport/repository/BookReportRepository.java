package com.nhs.myownspace.bookreport.repository;

import com.nhs.myownspace.bookreport.entity.BookReport;
import com.nhs.myownspace.user.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {
    Page<BookReport> findByProviderAndProviderId(
            Provider provider,
            String providerId,
            Pageable pageable
    );

    Optional<BookReport> findByIdAndProviderAndProviderId(
            Long id,
            Provider provider,
            String providerId
    );

    Page<BookReport> findByProviderAndProviderIdAndBookNameContainingIgnoreCase(
            Provider provider, String providerId, String bookName, Pageable pageable
    );

    List<BookReport> findTop3ByProviderAndProviderIdOrderByCreatedAtDesc(Provider provider, String providerId);

}

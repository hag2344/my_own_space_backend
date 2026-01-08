package com.nhs.myownspace.bookreport.repository;

import com.nhs.myownspace.bookreport.entity.BookReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {
    Page<BookReport> findByUser_Id(
            Long userId,
            Pageable pageable
    );

    Optional<BookReport> findByIdAndUser_Id(
            Long id,
            Long userId
    );

    Page<BookReport> findByUser_IdAndBookNameContainingIgnoreCase(
            Long userId, String bookName, Pageable pageable
    );

    List<BookReport> findTop3ByUser_IdOrderByCreatedAtDesc(Long userId);

}

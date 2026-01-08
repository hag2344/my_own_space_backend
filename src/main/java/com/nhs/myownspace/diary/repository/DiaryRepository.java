package com.nhs.myownspace.diary.repository;

import com.nhs.myownspace.diary.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Page<Diary> findByUser_Id(Long userId, Pageable pageable);

    Page<Diary> findByUser_IdAndTitleContainingIgnoreCase(
            Long userId, String title, Pageable pageable
    );

    Optional<Diary> findByIdAndUser_Id(Long id, Long userId);

    Optional<Diary> findTopByUser_IdAndTodayDateOrderByCreatedAtDesc(Long userId, LocalDate todayDate);
}

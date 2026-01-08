package com.nhs.myownspace.mymemory.repository;


import com.nhs.myownspace.mymemory.entity.MyMemory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MyMemoryRepository extends JpaRepository<MyMemory, Long> {
    Page<MyMemory> findByUser_Id(
            Long userId,
            Pageable pageable
    );

    Optional<MyMemory> findByIdAndUser_Id(
            Long id,
            Long userId
    );

    // 제목/내용에 keyword 포함 검색
    @Query("""
        select m
        from MyMemory m
        where m.user.Id = :userId
          and (
            lower(m.title) like lower(concat('%', :keyword, '%'))
            or lower(m.contentHtml) like lower(concat('%', :keyword, '%'))
          )
        """)
    Page<MyMemory> searchByUser_IdAndKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    List<MyMemory> findTop3ByUser_IdOrderByCreatedAtDesc(Long userId);
}
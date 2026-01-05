package com.nhs.myownspace.schedule.repository;

import com.nhs.myownspace.schedule.entity.Schedule;
import com.nhs.myownspace.user.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<Schedule> findByIdAndProviderAndProviderId(Long id, Provider provider, String providerId);

    @Query("""
        select count(s)
        from Schedule s
        where s.provider = :provider
          and s.providerId = :providerId
          and s.startDate <= :endOfDay
          and s.endDate >= :startOfDay
    """)
    int countOverlappingDay(
            @Param("provider") Provider provider,
            @Param("providerId") String providerId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
        select s
        from Schedule s
        where s.provider = :provider
          and s.providerId = :providerId
          and s.startDate <= :endOfDay
          and s.endDate >= :startOfDay
        order by s.startDate asc
    """)
    List<Schedule> findTodayTop3(
            @Param("provider") Provider provider,
            @Param("providerId") String providerId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable
    );
}
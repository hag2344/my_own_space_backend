package com.nhs.myownspace.schedule.repository;

import com.nhs.myownspace.schedule.entity.Schedule;
import com.nhs.myownspace.user.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<Schedule> findByIdAndProviderAndProviderId(Long id, Provider provider, String providerId);
}
package com.nhs.myownspace.schedule;

import com.nhs.myownspace.user.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByProviderAndProviderId(Provider provider, String providerId);
}
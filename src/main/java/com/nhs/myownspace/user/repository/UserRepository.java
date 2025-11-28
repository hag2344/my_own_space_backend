package com.nhs.myownspace.user.repository;

import com.nhs.myownspace.user.Provider;
import com.nhs.myownspace.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
}
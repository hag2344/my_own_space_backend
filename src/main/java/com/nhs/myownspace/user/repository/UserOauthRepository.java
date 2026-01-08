package com.nhs.myownspace.user.repository;

import com.nhs.myownspace.user.Provider;
import com.nhs.myownspace.user.entity.UserOauth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauth, Long>{
    Optional<UserOauth> findByProviderAndProviderId(Provider provider, String providerId);
}

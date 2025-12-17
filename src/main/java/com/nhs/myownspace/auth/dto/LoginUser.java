package com.nhs.myownspace.auth.dto;

import com.nhs.myownspace.user.Provider;

public record LoginUser(
        Provider provider,
        String providerId
) {}
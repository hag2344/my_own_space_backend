package com.nhs.myownspace.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserInfoDto {
    private String providerId;
    private String nickname;
    private String profileImage;
}
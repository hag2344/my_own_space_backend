package com.nhs.myownspace.user.dto;

import com.nhs.myownspace.user.entity.User;

public class UserMapper {
    /**
     * Entity → Response DTO
     */
    public static UserResponseDto responseDto(User user) {
        if (user == null) return null;

        return UserResponseDto.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .provider(user.getProvider().name())
                .build();
    }
}

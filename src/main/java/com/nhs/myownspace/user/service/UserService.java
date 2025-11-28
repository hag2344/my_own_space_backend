package com.nhs.myownspace.user.service;

import com.nhs.myownspace.global.util.AuthUtil;
import com.nhs.myownspace.user.Provider;
import com.nhs.myownspace.user.dto.UserMapper;
import com.nhs.myownspace.user.entity.User;
import com.nhs.myownspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.nhs.myownspace.user.dto.UserResponseDto;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * 로그인된 사용자 정보를 기반으로 DB 조회
     */
    public UserResponseDto getMyInfo() {

        // SecurityContext에서 로그인 정보 가져오기
        var userInfo = AuthUtil.getLoginUser();
        if (userInfo == null) {
            log.warn("인증되지 않은 사용자 요청");
            return null;
        }

        Provider provider = Provider.valueOf(userInfo.get("provider").toUpperCase());
        String providerId = userInfo.get("providerId");

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElse(null);

        if (user == null) {
            log.warn("DB에서 사용자 정보 없음 - provider: {}, providerId: {}", provider, providerId);
            return null;
        }

        log.info("사용자 정보 조회 성공 - providerId: {}", providerId);

        return UserMapper.responseDto(user);
    }
}

package com.nhs.myownspace.user;

import com.nhs.myownspace.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * 로그인된 사용자 정보를 기반으로 DB 조회
     */
    public Map<String, Object> getMyInfo() {

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

        return Map.of(
                "nickname", user.getNickname(),
                "profileImage", user.getProfileImage(),
                "provider", provider.name()
        );
    }
}

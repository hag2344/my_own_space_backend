package com.nhs.myownspace.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import com.nhs.myownspace.global.util.UrlUtil;
import com.nhs.myownspace.auth.dto.KakaoUserInfoDto;

@Slf4j
@Service
public class KakaoService {

    private final String KAKAO_REST_KEY;
    private final String CORS_ALLOWED_ORIGIN;

    public KakaoService(@Value("${kakao.rest.key}") String kakao_rest_key,
                        @Value("${cors.allowed-origin}") String cors_allowed_origin ){
        this.KAKAO_REST_KEY = kakao_rest_key;
        this.CORS_ALLOWED_ORIGIN = cors_allowed_origin;
    }

    /**
     * 인가 코드로 카카오 사용자 정보 조회
     */
    public KakaoUserInfoDto getUserInfoFromCode(String code){
        try {
            log.info("카카오 로그인 요청 시작 - code: {}", code);
            String accessToken = getAccessToken(code);
            if(accessToken == null || accessToken.isEmpty()){
                log.warn("Access Token 발급 실패 - code: {}", code);
                return null;
            }

            return getUserProfile(accessToken);

        } catch (Exception e){
            log.error("카카오 사용자 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 인가 코드로 Access Token 발급
     */
    @SuppressWarnings("unchecked")
    private String getAccessToken(String code){
        try {
            RestTemplate restTemplate = new RestTemplate();
            String tokenUrl = "https://kauth.kakao.com/oauth/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String body = "grant_type=authorization_code"
                    + "&client_id="+KAKAO_REST_KEY
                    + "&redirect_uri="+CORS_ALLOWED_ORIGIN+"/oauth/kakao"
                    + "&code="+code;

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            Map<String, Object> response = restTemplate.postForObject(tokenUrl,entity,Map.class);

            if(response == null || !response.containsKey("access_token")){
                log.warn("카카오 Access Token 응답이 비정상입니다: {}", response);
                return null;
            }

            String token = (String) response.get("access_token");
            log.debug("카카오 Access Token 발급 성공");
            return token;

        } catch (HttpClientErrorException e) {
            log.error("카카오 토큰 발급 요청 실패 (4xx): {}", e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("카카오 토큰 발급 중 통신 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("카카오 Access Token 발급 중 알 수 없는 오류: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Access Token 으로 사용자 프로필 조회
     */
    @SuppressWarnings("unchecked")
    private KakaoUserInfoDto getUserProfile(String accessToken){
        try{
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("카카오 사용자 조회 응답 상태 비정상: {}", response.getStatusCode());
                return null;
            }

            Map<String, Object> body = (Map<String, Object>) response.getBody();
            if (body == null){
                log.warn("카카오 API 응답이 비어 있음");
                return null;
            }

            String providerId = String.valueOf(body.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
            Map<String, Object> profile = kakaoAccount != null
                    ? (Map<String, Object>) kakaoAccount.get("profile")
                    : null;

            String nickname = profile != null ? (String) profile.get("nickname") : "사용자";
            String profileImage = null;

            if (profile != null){
                // 최신 카카오 프로필 키
                profileImage = UrlUtil.forceHttps((String) profile.get("profile_image_url"));
                // 구버전 카카오 프로필 키
                if (profileImage == null){
                    profileImage = UrlUtil.forceHttps((String) profile.get("profile_image"));
                }
                if (profileImage == null){
                    profileImage = UrlUtil.forceHttps((String) profile.get("thumbnail_image_url"));
                }
            }

            log.debug("카카오 사용자 정보 파싱 완료 - providerId: {}", providerId);
            return new KakaoUserInfoDto(providerId, nickname, profileImage);

        } catch (HttpClientErrorException e) {
            log.error("카카오 사용자 정보 요청 실패 (4xx): {}", e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("카카오 사용자 정보 통신 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("카카오 사용자 ID 조회 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
        return null;
    }
}

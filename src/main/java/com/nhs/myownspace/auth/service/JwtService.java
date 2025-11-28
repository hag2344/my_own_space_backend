package com.nhs.myownspace.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

    private final Key key;

    public JwtService(@Value("${jwt.secret}") String secret_key){
        this.key = Keys.hmacShaKeyFor(secret_key.getBytes());
    }

    /**
     * Access Token (1시간 유효)
     * @param provider  각 소셜 로그인 제공 사이트
     * @param providerId 각 provider 내에서 사용자 고유 ID
     */
    public String createAccessToken(String provider, String providerId){
        try{
            Claims claims = Jwts.claims();
            claims.setSubject(providerId);
            claims.put("provider", provider);

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis()+ 3600000)) // 1시간
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            log.debug("Access Token 생성 완료 (provider: {}, providerId: {})",provider ,providerId);
            return token;
        }catch (Exception e){
            log.error("Access Token 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Access Token 생성 중 오류 발생", e);
        }
    }

    /**
     * Refresh Token (2주 유효)
     * @param provider  각 소셜 로그인 제공 사이트
     * @param providerId 각 provider 내에서 사용자 고유 ID
     */
    public String createRefreshToken(String provider, String providerId){
        try {
            Claims claims = Jwts.claims();
            claims.setSubject(providerId);
            claims.put("provider", provider);

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis()+1000L*60*60*24*14)) // 14일
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            log.debug("Refresh Token 생성 완료 (provider: {}, providerId: {})", provider, providerId);
            return token;
        }catch (Exception e){
            log.error("Refresh Token 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Refresh Token 생성 중 오류 발생", e);
        }
    }

    /**
     * JWT 유효성 검증
     * @param token JWT 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateJwt(String token){
        try {
            if(token == null || token.isEmpty()){
                log.warn("JWT 검증 실패: 토큰이 비어 있음");
                return false;
            }

            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT 만료됨: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 형식: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT 구조가 올바르지 않음: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT 일반 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 검증 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * JWT에서 사용자 ID 추출
     * @param token JWT 문자열
     * @return JWT subject (providerId)
     */
    public String extractUserId(String token){
        try{
            if (token == null || token.isEmpty()) {
                log.warn("토큰이 비어 있어 사용자 ID 추출 불가");
                return null;
            }

            String userId = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // Subject에 Provider 사용자 ID 저장됨

            log.debug("JWT에서 사용자 ID 추출 완료: {}", userId);
            return userId;
        }catch (ExpiredJwtException e) {
            log.warn("JWT 만료로 인해 사용자 ID 추출 실패: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT 파싱 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 파싱 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * JWT에서 provider 같은 claim 값 추출
     * @param token JWT 문자열
     * @return JWT Claim (provider)
     */
    public String extractClaim(String token, String keyName) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("토큰이 비어 있어 claims 추출 불가");
                return null;
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return (String) claims.get(keyName);
        } catch (Exception e) {
            log.warn("JWT claim 추출 실패: {}", e.getMessage());
            return null;
        }
    }
}

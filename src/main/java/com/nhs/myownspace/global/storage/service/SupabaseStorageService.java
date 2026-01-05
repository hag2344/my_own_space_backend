package com.nhs.myownspace.global.storage.service;

import com.nhs.myownspace.global.storage.model.UploadFolder;
import io.netty.channel.ChannelOption;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SupabaseStorageService implements StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.sign-expires:3600}")
    private int expiresIn;

    // 나중에 HttpClient를 붙여서 생성할 거라 final 제거
    private WebClient webClient;

    @PostConstruct
    public void init() {
        // 설정값 로그로 한 번 확인
        log.info("[Supabase] url={}, bucket={}, expiresIn={}", supabaseUrl, bucket, expiresIn);

        // DNS queryTimeout 등을 명시적으로 지정한 HttpClient
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30))              // 응답 타임아웃 30초
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);  // 연결 타임아웃 5초

        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl) // ex) https://xuclrzujtnkekvdsldeh.supabase.co
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Override
    public String uploadPrivate(UploadFolder folder, MultipartFile file) throws Exception {
        String ext = safeExt(file.getOriginalFilename());
        String path = folder.name().toLowerCase() + "/" + UUID.randomUUID() + ext;

        String objectPath = String.format("/storage/v1/object/%s/%s", bucket, path);
        log.info("[Storage] upload start bucket={}, path={}, size={}", bucket, path, file.getSize());
        try{
            webClient.post()
                    .uri(objectPath)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(30)); // 전체 HTTP 요청 타임아웃도 명시

            log.info("[Storage] upload success path={}", path);
            return path; // DB 에는 이 path만 저장 (예: "mymemory/uuid.png")
        }
        catch (Exception e){
            // Timeout 계열이면 메시지 구분해서 래핑
            if (e.getCause() instanceof java.util.concurrent.TimeoutException) {
                throw new RuntimeException("업로드 서버 응답이 지연되었습니다. 잠시 후 다시 시도해 주세요.", e);
            }
            throw e;
        }
    }

    @Override
    public String createSignedUrl(String path) {
        String signPath = String.format("/storage/v1/object/sign/%s/%s", bucket, path);
        log.info("[Storage] sign start path={}, expiresIn={}", path, expiresIn);

        try {
            Map<String, Object> res = webClient.post()
                    .uri(signPath)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("expiresIn", expiresIn))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(Duration.ofSeconds(30));

            if (res == null) {
                log.error("[Storage] sign response null");
                throw new RuntimeException("signed url 발급 실패: 응답이 null");
            }

            Object signed = res.get("signedURL");
            if (signed == null) {
                signed = res.get("signedUrl");
            }

            if (signed == null) {
                log.error("[Storage] sign response missing signed url: {}", res);
                throw new RuntimeException("signed url 발급 실패");
            }

            String signedPath = String.valueOf(signed); // 보통 "/storage/v1/object/sign/..." 형태

            // 1) 이미 풀 URL이면 그대로 사용
            // 프론트에서 바로 <img src="...">로 쓸 수 있게 절대 URL로 만들어서 반환
            if (signedPath.startsWith("http://") || signedPath.startsWith("https://")) {
                return signedPath;
            }

            String normalized = signedPath.startsWith("/") ? signedPath.substring(1) : signedPath;

            // 3) storage/v1 prefix 가 없으면 붙여준다
            if (!normalized.startsWith("storage/v1/")) {
                normalized = "storage/v1/" + normalized;
            }

            // 4) 최종 절대 URL
            String finalUrl = supabaseUrl + "/" + normalized;
            log.info("[Storage] signed url generated: {}", finalUrl);
            return finalUrl;

        } catch (Exception e) {
            log.error("[Storage] sign error path={}", path, e);
            throw new RuntimeException("signed url 발급 실패", e);
        }
    }

    @Override
    public void delete(String path) throws Exception {
        String objectPath = String.format("/storage/v1/object/%s/%s", bucket, path);
        log.info("[Storage] delete start bucket={}, path={}", bucket, path);

        webClient.delete()
                .uri(objectPath)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(30));

        log.info("[Storage] delete success path={}", path);
    }

    private String safeExt(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0) return "";
        return filename.substring(idx).toLowerCase();
    }

    public String safeSignedUrl(String path) {
        if (path == null || path.isBlank()) return null;

        try {
            return createSignedUrl(path);
        } catch (Exception e) {
            log.warn("[Home] signed url skipped. path={}", path);
            return null;
        }
    }
}
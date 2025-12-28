package com.nhs.myownspace.global.storage.html;

import com.nhs.myownspace.global.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * HTML 안의 Supabase 이미지(<img src="...">)들을 새로 발급한 signed URL로 바꿔주는 공용 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SupabaseImageHtmlRewriter {

    private final StorageService storageService;

    /**
     * HTML 안의 <img src="..."> 중 Supabase Storage 이미지만
     * fresh signed URL 로 교체해서 반환
     */
    public String rewriteImgSrcWithSignedUrl(String html) {
        if (html == null || html.isBlank()) return html;

        try {
            Document doc = Jsoup.parseBodyFragment(html);

            for (Element img : doc.select("img[src]")) {
                String src = img.attr("src");
                if (src == null || src.isBlank()) continue;

                // Supabase signed URL 이면 object path 추출
                String objectPath = extractSupabaseObjectPath(src);
                if (objectPath == null || objectPath.isBlank()) {
                    // 우리 스토리지 아닌 외부 URL이면 그대로 둔다
                    continue;
                }

                try {
                    String freshSignedUrl = storageService.createSignedUrl(objectPath);
                    img.attr("src", freshSignedUrl);
                } catch (Exception e) {
                    log.warn("[SupabaseImageHtmlRewriter] signed url 생성 실패 path={}", objectPath, e);
                }
            }

            // body 안쪽 HTML만 반환 (Toast UI에서 그대로 렌더 가능)
            return doc.body().html();

        } catch (Exception e) {
            log.warn("[SupabaseImageHtmlRewriter] HTML 이미지 src 재작성 실패", e);
            // 파싱 실패하면 원본 그대로 반환
            return html;
        }
    }

    /**
     * Supabase signed URL에서 object path(mymemory/uuid.png)를 추출
     *
     * 예)
     *   https://xxx.supabase.co/storage/v1/object/sign/<bucket>/mymemory/xxx.png?token=...
     *   → "mymemory/xxx.png"
     */
    private String extractSupabaseObjectPath(String src) {
        if (src == null || src.isBlank()) return null;
        try {
            URI uri = new URI(src);

            // data:, blob: 같은 스킴은 path가 null 이거나 우리가 원하는 형식이 아님
            String path = uri.getPath(); // /storage/v1/object/sign/<bucket>/mymemory/xxx.png
            if (path == null || path.isBlank()) {
                return null;
            }

            // "/storage/v1/object/sign/" 뒤부터 "<bucket>/objectPath"
            String prefix = "/storage/v1/object/sign/";
            int idx = path.indexOf(prefix);
            if (idx < 0) {
                // 혹시 "/object/sign/" 형식이면 그것도 허용
                prefix = "/object/sign/";
                idx = path.indexOf(prefix);
                if (idx < 0) return null;
            }

            String after = path.substring(idx + prefix.length()); // "<bucket>/objectPath"
            int slashIdx = after.indexOf('/');
            if (slashIdx < 0) return null;

            // "mymemory/xxx.png" (URL 인코딩 되어 있을 수 있음)
            String encoded = after.substring(slashIdx + 1);
            if (encoded.isBlank()) return null;

            return URLDecoder.decode(encoded, StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.warn("[SupabaseImageHtmlRewriter] Supabase URL 파싱 실패 src={}", src, e);
            return null;
        }
    }
}
package com.nhs.myownspace.common.util;

public class UrlUtil {
    private UrlUtil() {
        // 유틸 클래스는 객체 생성 방지
    }

    /**
     * URL 내 모든 http:// 를 https:// 로 변환
     */
    public static String forceHttps(String url) {
        if (url == null) return null;
        return url.replace("http://", "https://");
    }
}

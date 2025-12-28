package com.nhs.myownspace.global.storage.util;

import org.springframework.web.multipart.MultipartFile;

public class FileValidateUtil {

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비었습니다.");
        }
        // Supabase free 업로드 제한 등 고려(보수적으로 50MB) :contentReference[oaicite:3]{index=3}
        if (file.getSize() > 50L * 1024 * 1024) {
            throw new IllegalArgumentException("파일이 너무 큽니다(최대 50MB).");
        }

        String name = file.getOriginalFilename();
        String ext = (name == null) ? "" : name.substring(name.lastIndexOf('.') + 1).toLowerCase();

        boolean ok = ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("webp") || ext.equals("gif");
        if (!ok) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다(png/jpg/jpeg/webp/gif).");
        }
    }
}
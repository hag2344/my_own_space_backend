package com.nhs.myownspace.global.storage.service;

import com.nhs.myownspace.global.storage.model.UploadFolder;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadPrivate(UploadFolder folder, MultipartFile file) throws Exception;
    String createSignedUrl(String path);
    void delete(String path) throws Exception;
}
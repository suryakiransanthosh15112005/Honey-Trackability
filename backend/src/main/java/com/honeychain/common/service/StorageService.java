package com.honeychain.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String storeFile(MultipartFile file, String subDirectory);

    void deleteFile(String fileUrl);
}

package com.honeychain.common.service.impl;

import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class LocalStorageServiceImpl implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStorageServiceImpl.class);

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp",
            "application/pdf"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Value("${app.upload.base-dir:uploads}")
    private String baseUploadDir;

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Invalid file type. Only PDF, JPEG, PNG, and WEBP files are allowed.");
        }

        try {
            Path targetDir = Paths.get(baseUploadDir, subDirectory).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = UUID.randomUUID().toString() + extension;
            Path targetLocation = targetDir.resolve(filename);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Saved file: {}", targetLocation);

            return "/uploads/" + subDirectory + "/" + filename;
        } catch (IOException ex) {
            logger.error("Failed to store file", ex);
            throw new BadRequestException("Could not store file: " + ex.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        try {
            String relativePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Path filePath = Paths.get(relativePath).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
            logger.info("Deleted file: {}", filePath);
        } catch (IOException ex) {
            logger.warn("Could not delete file: {}", fileUrl);
        }
    }
}

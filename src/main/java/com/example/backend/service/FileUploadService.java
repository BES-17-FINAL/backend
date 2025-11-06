package com.example.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadService {

    @Value("${file.upload-dir:uploads/images}")
    private String uploadDir;

    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        if (!isValidImageExtension(extension)) {
            throw new RuntimeException("이미지 파일만 업로드 가능합니다 (jpg, jpeg, png, gif)");
        }

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(uploadDir, uniqueFilename);

        try {
            Files.copy(file.getInputStream(), filePath);
            log.info("파일 업로드 성공: {}", uniqueFilename);

            return "/images/" + uniqueFilename;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드에 실패했습니다");
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        
        if (filename == null || filename.isEmpty()) {
            log.warn("잘못된 이미지 URL입니다: {}", imageUrl);
            return;
        }
        
        Path filePath = Paths.get(uploadDir, filename);

        try {
            if (Files.deleteIfExists(filePath)) {
                log.info("파일 삭제 성공: {}", filename);
            } else {
                log.warn("삭제할 파일이 존재하지 않습니다: {}", filename);
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filename, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex);
    }

    private boolean isValidImageExtension(String extension) {
        return extension.equalsIgnoreCase(".jpg") ||
                extension.equalsIgnoreCase(".jpeg") ||
                extension.equalsIgnoreCase(".png") ||
                extension.equalsIgnoreCase(".gif");
    }
}
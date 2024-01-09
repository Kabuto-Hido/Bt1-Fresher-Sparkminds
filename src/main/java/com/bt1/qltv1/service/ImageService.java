package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.book.UploadImageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public interface ImageService {
    Path saveImageToStorage(String fileName, String uploadDir, MultipartFile file);
    void deleteImage(String imageName, String imageDir);
    String getDirImage(String fileName, String imageDirectory);
}

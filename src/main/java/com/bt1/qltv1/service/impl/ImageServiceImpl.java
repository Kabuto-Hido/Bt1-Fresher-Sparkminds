package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.entity.Book;
import com.bt1.qltv1.entity.User;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.BookRepository;
import com.bt1.qltv1.repository.UserRepository;
import com.bt1.qltv1.service.ImageService;
import com.bt1.qltv1.util.Global;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Component
@Log4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Override
    public Path saveImageToStorage(String fileName, String uploadDir,
                                   MultipartFile file) {
        if (!isSupportedImageType(Objects.requireNonNull(file.getContentType()))) {
            throw new BadRequest("Just support JPG, PNG file!!!!",
                    "image.type.invalid");
        }

        fileName += getFileExtension(file);

        Path uploadPath = Path.of(uploadDir);
        Path filePath = uploadPath.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequest(e.getMessage(), "upload-image.error");
        }

        return filePath.toAbsolutePath();
    }


    private static String getFileExtension(MultipartFile filePath) {
        String fileName = filePath.getOriginalFilename();

        int dotIndex = 0;
        if (fileName != null) {
            dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
        }

        return null;
    }

    private boolean isSupportedImageType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }

    @Override
    public void deleteImage(String imageName, String imageDir) {

        Path imagePath = Path.of(imageDir, imageName);

        try {
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
            }
        } catch (IOException e) {
            throw new BadRequest(e.getMessage(), "delete-image.error");
        }
    }

    @Override
    public String getDirImage(String fileName, String imageDirectory) {
        if (fileName.equals(Global.DEFAULT_AVATAR) || (fileName.equals(Global.DEFAULT_IMAGE))) {
            return fileName;
        }
        Path imagePath = Path.of(imageDirectory, fileName);
        return imagePath.toAbsolutePath().toString();

    }
}

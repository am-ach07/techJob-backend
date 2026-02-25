package com.techJob.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techJob.exception.image.ImageException;


@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file, String folder) {

        if (file == null || file.isEmpty()) {
            throw new ImageException("File is empty");
        }

        try {

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir).resolve(folder);

            System.out.println("UPLOAD PATH: " + uploadPath.toAbsolutePath());
            
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            
            System.out.println("===============FILE PATH: " + filePath.toAbsolutePath());

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + folder + "/" + fileName;

        } catch (Exception e) {
            throw new ImageException("File upload failed");
        }
    }
    public void deleteFile(String fileUrl) {

        try {

            String relativePath = fileUrl.replaceFirst("^/uploads/", "");

            Path path = Paths.get(uploadDir).resolve(relativePath).normalize();

            if (Files.exists(path) && Files.isRegularFile(path)) {
                Files.delete(path);
            }

        } catch (Exception e) {
            throw new ImageException("File deletion failed");
        }
    }
   
}

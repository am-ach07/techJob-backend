package com.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file, String folder) {

        try {

            String fileName =
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path uploadPath =
                    Paths.get(uploadDir + "/" + folder);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath =
                    uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + folder + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
    }
    public void deleteFile(String filePath) {
		try {
			Path path = Paths.get(uploadDir + filePath.replace("/uploads", ""));
			Files.deleteIfExists(path);
		} catch (Exception e) {
			throw new RuntimeException("File deletion failed");
		}
	}
    public String updateFile(MultipartFile file, String folder, String existingFilePath) {
    			deleteFile(existingFilePath);
		return saveFile(file, folder);
    }
}

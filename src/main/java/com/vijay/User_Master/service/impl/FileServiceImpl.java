package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.service.FileService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    
    private static final String UPLOAD_DIR = "uploads/";
    
    @Override
    @Tool(name = "uploadFile", description = "Upload file to server storage with folder organization")
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR + folder);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
    
    @Override
    public String uploadUserImage(MultipartFile image) {
        return uploadFile(image, "users");
    }
    
    @Override
    public String uploadWorkerImage(MultipartFile image) {
        return uploadFile(image, "workers");
    }
    
    @Override
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public boolean deleteFile(String fileName, String path) {
        try {
            Path filePath = Paths.get(path, fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public String getFileUrl(String fileName) {
        return "/uploads/" + fileName;
    }
    
    @Override
    public InputStream getResource(String path, String fileName) {
        try {
            Path filePath = Paths.get(path, fileName);
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
    }
}
package com.vijay.User_Master.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface FileService {
    String uploadFile(MultipartFile file, String folder);
    String uploadUserImage(MultipartFile image);
    String uploadWorkerImage(MultipartFile image);
    boolean deleteFile(String fileName);
    boolean deleteFile(String fileName, String path);
    String getFileUrl(String fileName);
    InputStream getResource(String path, String fileName);
}
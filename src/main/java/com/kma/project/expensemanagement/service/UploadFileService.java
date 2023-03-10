package com.kma.project.expensemanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadFileService {

    String uploadFileCloud(MultipartFile file);
}

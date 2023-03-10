package com.kma.project.expensemanagement.service.impl;

import com.cloudinary.Cloudinary;
import com.kma.project.expensemanagement.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    Cloudinary cloudinary;

    @Override
    public String uploadFileCloud(MultipartFile file) {
        Map map = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("folder", "my_folder"); // Optional: specify a folder for the uploaded files
        params.put("use_filename", "true"); // Optional: use the original filename of the uploaded file
        params.put("unique_filename", "true"); // Optional: allow files with the same filename to be uploaded
        params.put("resource_type", "auto"); // Optional: let Cloudinary determine the resource type automatically
//        params.put("overwrite", "true"); // Optional: overwrite files with the same public ID
        try {
            map = cloudinary.uploader().upload(file.getBytes(), params);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
//        String url = cloudinary.url().transformation(new Transformation().width(100).height(150).crop("fill")).generate("olympic_flag");
        return (String) map.get("secure_url");
    }
}

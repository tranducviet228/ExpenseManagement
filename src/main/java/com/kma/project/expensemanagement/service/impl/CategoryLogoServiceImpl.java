package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.response.CategoryLogoOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.entity.CategoryLogoEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.repository.CategoryLogoRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.service.CategoryLogoService;
import com.kma.project.expensemanagement.service.UploadFileService;
import com.kma.project.expensemanagement.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class CategoryLogoServiceImpl implements CategoryLogoService {

    @Autowired
    CategoryLogoRepository logoRepository;

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    JwtUtils jwtUtils;

    @Transactional
    @Override
    public void add(MultipartFile[] files) {
        List<CategoryLogoEntity> listLogo = new ArrayList<>();
        for (MultipartFile file : files) {
            CategoryLogoEntity categoryLogoEntity = new CategoryLogoEntity();
            categoryLogoEntity.setFileUrl(uploadFileService.uploadFileCloud(file));
            categoryLogoEntity.setFileName(file.getOriginalFilename());
            listLogo.add(categoryLogoEntity);
            categoryLogoEntity.setCreatedBy(jwtUtils.getCurrentUserId());

        }
        logoRepository.saveAll(listLogo);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        CategoryLogoEntity categoryLogoEntity = logoRepository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.logo-category-not-found")).build());
        logoRepository.delete(categoryLogoEntity);
    }

    @Override
    public PageResponse<CategoryLogoOutputDto> getAll(Integer page, Integer size, String sort) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        Page<CategoryLogoEntity> listLogo = logoRepository.findAll(pageable);
        return PageUtils.formatPageResponse(listLogo.map(categoryLogoEntity -> {
            CategoryLogoOutputDto outputDto = new CategoryLogoOutputDto();
            outputDto.setId(categoryLogoEntity.getId());
            outputDto.setFileName(categoryLogoEntity.getFileName());
            outputDto.setFileUrl(categoryLogoEntity.getFileUrl());
            outputDto.setCreatedAt(categoryLogoEntity.getCreatedAt());
            outputDto.setCreatedBy(categoryLogoEntity.getCreatedBy());
            return outputDto;
        }));
    }
}

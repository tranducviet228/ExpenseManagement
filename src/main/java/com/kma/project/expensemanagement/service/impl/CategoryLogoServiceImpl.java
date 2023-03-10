package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.response.CategoryLogoOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.entity.CategoryLogoEntity;
import com.kma.project.expensemanagement.repository.CategoryLogoRepository;
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
import java.util.List;

@Transactional(readOnly = true)
@Service
public class CategoryLogoServiceImpl implements CategoryLogoService {

    @Autowired
    CategoryLogoRepository logoRepository;

    @Autowired
    UploadFileService uploadFileService;

    @Transactional
    @Override
    public void add(MultipartFile[] files) {
        List<CategoryLogoEntity> listLogo = new ArrayList<>();
        for (MultipartFile file : files) {
            CategoryLogoEntity categoryLogoEntity = new CategoryLogoEntity();
            categoryLogoEntity.setFileUrl(uploadFileService.uploadFileCloud(file));
            categoryLogoEntity.setFileName(file.getOriginalFilename());
            listLogo.add(categoryLogoEntity);
        }
        logoRepository.saveAll(listLogo);
    }

    @Override
    public PageResponse<CategoryLogoOutputDto> getAll(Integer page, Integer size, String sort) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        Page<CategoryLogoEntity> listLogo = logoRepository.findAll(pageable);
        return PageUtils.formatPageResponse(listLogo.map(categoryLogoEntity -> {
            CategoryLogoOutputDto outputDto = new CategoryLogoOutputDto();
            outputDto.setFileName(categoryLogoEntity.getFileName());
            outputDto.setFileUrl(categoryLogoEntity.getFileUrl());
            return outputDto;
        }));
    }
}

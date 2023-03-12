package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.CategoryInputDto;
import com.kma.project.expensemanagement.dto.response.CategoryOutputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.CategoryLogoEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.CategoryMapper;
import com.kma.project.expensemanagement.repository.CategoryLogoRepository;
import com.kma.project.expensemanagement.repository.CategoryRepository;
import com.kma.project.expensemanagement.service.CategoryService;
import com.kma.project.expensemanagement.service.UploadFileService;
import com.kma.project.expensemanagement.utils.DataUtils;
import com.kma.project.expensemanagement.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository repository;

    @Autowired
    CategoryLogoRepository logoRepository;

    @Autowired
    CategoryMapper mapper;

    @Autowired
    UploadFileService uploadFileService;

    @Transactional
    @Override
    public CategoryOutputDto add(CategoryInputDto inputDto) {
        CategoryEntity categoryEntity = mapper.convertToEntity(inputDto);
        logoRepository.findById(inputDto.getLogoImageID())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.logo-not-found")).build());
        categoryEntity.setUpdatedAt(LocalDateTime.now());
        categoryEntity.setCreatedAt(LocalDateTime.now());
        repository.save(categoryEntity);
        return mapper.convertToDto(categoryEntity);
    }

    @Transactional
    @Override
    public CategoryOutputDto update(Long id, CategoryInputDto inputDto) {
        CategoryEntity categoryEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
        if (inputDto.getLogoImageID() != null) {
            logoRepository.findById(inputDto.getLogoImageID())
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.logo-not-found")).build());
        }
        categoryEntity = mapper.update(inputDto, categoryEntity);
        repository.save(categoryEntity);
        return mapper.convertToDto(categoryEntity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        CategoryEntity categoryEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
        repository.delete(categoryEntity);
    }

    @Override
    public DataResponse<CategoryOutputDto> getDetail(Long id) {
        CategoryEntity categoryEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
        CategoryLogoEntity logoEntity = logoRepository.findById(categoryEntity.getLogoImageID())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.logo-not-found")).build());
        CategoryOutputDto categoryOutputDto = mapper.convertToDto(categoryEntity);
        categoryOutputDto.setLogoImageUrl(logoEntity.getFileUrl());
        return DataUtils.formatData(categoryOutputDto);
    }

    @Override
    public PageResponse<CategoryOutputDto> getAllCategory(Integer page, Integer size, String sort, String search, Long parentId) {

        Pageable pageable = PageUtils.customPageable(page, size, sort);
        parentId = parentId == null ? 0 : parentId;
        Page<CategoryEntity> categoryPage = repository.findAllByNameLikeIgnoreCaseAndParentId(pageable, PageUtils.buildSearch(search), parentId);

        List<Long> logoIds = new ArrayList<>();
        categoryPage.forEach(categoryEntity -> logoIds.add(categoryEntity.getLogoImageID()));
        List<CategoryLogoEntity> logoEntities = logoRepository.getAllByIdIn(logoIds);
        Map<Long, CategoryLogoEntity> mapLogo = logoEntities.stream().collect(Collectors.toMap(CategoryLogoEntity::getId, Function.identity()));

        return PageUtils.formatPageResponse(categoryPage.map(categoryEntity -> {
            CategoryOutputDto categoryOutputDto = mapper.convertToDto(categoryEntity);
            if (mapLogo.get(categoryEntity.getLogoImageID()) != null) {
                categoryOutputDto.setLogoImageUrl(mapLogo.get(categoryEntity.getLogoImageID()).getFileUrl());
            }
            return categoryOutputDto;
        }));
    }
}

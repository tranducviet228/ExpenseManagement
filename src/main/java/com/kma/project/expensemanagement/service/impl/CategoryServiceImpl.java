package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.CategoryInputDto;
import com.kma.project.expensemanagement.dto.response.CategoryOutputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.CategoryLogoEntity;
import com.kma.project.expensemanagement.enums.CategoryType;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.CategoryMapper;
import com.kma.project.expensemanagement.repository.CategoryLogoRepository;
import com.kma.project.expensemanagement.repository.CategoryRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
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

import java.util.*;
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

    @Autowired
    JwtUtils jwtUtils;

    @Transactional
    @Override
    public CategoryOutputDto add(CategoryInputDto inputDto) {
        CategoryEntity categoryEntity = mapper.convertToEntity(inputDto);
        if (inputDto.getLogoImageID() != null) {
            logoRepository.findById(inputDto.getLogoImageID())
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.logo-not-found")).build());
        }
        if (inputDto.getParentId() == null) {
            categoryEntity.setParentId(0L);
        }
        categoryEntity.setCreatedBy(jwtUtils.getCurrentUserId());
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
        categoryOutputDto.setLogoImage(logoEntity);
        return DataUtils.formatData(categoryOutputDto);
    }

    @Override
    public PageResponse<CategoryOutputDto> getAllCategoryByParentId(Integer page, Integer size, String sort, String search, Long parentId) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        parentId = parentId == null ? 0 : parentId;
        Page<CategoryEntity> categoryPage = repository
                .findAllByNameLikeIgnoreCaseAndParentIdAndCreatedBy(pageable, PageUtils.buildSearch(search), parentId, jwtUtils.getCurrentUserId());

        List<Long> logoIds = new ArrayList<>();
        categoryPage.forEach(categoryEntity -> logoIds.add(categoryEntity.getLogoImageID()));
        List<CategoryLogoEntity> logoEntities = logoRepository.getAllByIdIn(logoIds);
        Map<Long, CategoryLogoEntity> mapLogo = logoEntities.stream().collect(Collectors.toMap(CategoryLogoEntity::getId, Function.identity()));

        return PageUtils.formatPageResponse(categoryPage.map(categoryEntity -> {
            CategoryOutputDto categoryOutputDto = mapper.convertToDto(categoryEntity);
            if (mapLogo.get(categoryEntity.getLogoImageID()) != null) {
                categoryOutputDto.setLogoImage(mapLogo.get(categoryEntity.getLogoImageID()).getFileUrl());
            }
            return categoryOutputDto;
        }));
    }

    @Override
    public Set<CategoryOutputDto> getAllCategory(String search, String type) {
        List<CategoryOutputDto> categoryOutputs = repository.findAllByNameLikeIgnoreCaseAndCreatedByAndCategoryType(PageUtils.buildSearch(search)
                        , jwtUtils.getCurrentUserId(), Enum.valueOf(CategoryType.class, type))
                .stream().map(categoryEntity -> mapper.convertToDto(categoryEntity)).collect(Collectors.toList());

        List<Long> logoIds = new ArrayList<>();
        categoryOutputs.forEach(categoryDto -> logoIds.add(categoryDto.getLogoImageID()));
        List<CategoryLogoEntity> logoEntities = logoRepository.getAllByIdIn(logoIds);
        Map<Long, CategoryLogoEntity> mapLogo = logoEntities.stream().collect(Collectors.toMap(CategoryLogoEntity::getId, Function.identity()));

        Set<Long> categoryIds = new HashSet<>();
        categoryOutputs.forEach(categoryOutputDto -> {
            if (categoryOutputDto.getLogoImageID() != null && mapLogo.get(categoryOutputDto.getLogoImageID()) != null) {
                categoryOutputDto.setLogoImage(mapLogo.get(categoryOutputDto.getLogoImageID()).getFileUrl());
            }
            categoryIds.add(categoryOutputDto.getId());
        });

        // tìm ra những thằng cha
        List<CategoryOutputDto> cateList = new ArrayList<>(categoryOutputs);
        cateList.forEach(item -> {
            repository.findById(item.getParentId()).ifPresent(category -> {
                if (!categoryIds.contains(category.getId())) {
                    categoryOutputs.add(mapper.convertToDto(category));
                }
            });
        });
        Set<CategoryOutputDto> categoryParent = categoryOutputs.stream()
                .filter(n -> n.getParentId().equals(0L))
                .collect(Collectors.toSet());
        // convert list to map, nhóm các thằng cùng cha lại với nhau
        Map<Long, List<CategoryOutputDto>> map = new HashMap<>();
        categoryOutputs.forEach(item -> {
            if (!item.getParentId().equals(0L)) {
                List<CategoryOutputDto> listMap;
                if (map.get(item.getParentId()) == null) {
                    listMap = new ArrayList<>();
                } else {
                    listMap = map.get(item.getParentId());
                }
                listMap.add(item);
                map.put(item.getParentId(), listMap);
            }
        });
        categoryParent.forEach(item -> {
            if (map.get(item.getId()) != null) {
                item.setChildCategory(map.get(item.getId()));
            }
        });
        return categoryParent;
    }

}

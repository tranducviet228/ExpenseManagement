package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.CategoryInputDto;
import com.kma.project.expensemanagement.dto.response.CategoryOutputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;

import java.util.Set;

public interface CategoryService {

    CategoryOutputDto add(CategoryInputDto inputDto);

    CategoryOutputDto update(Long id, CategoryInputDto inputDto);

    void delete(Long id);

    DataResponse<CategoryOutputDto> getDetail(Long id);

    PageResponse<CategoryOutputDto> getAllCategoryByParentId(Integer page, Integer size, String sort, String search, Long parentId);

    Set<CategoryOutputDto> getAllCategory(String search, String type);

}

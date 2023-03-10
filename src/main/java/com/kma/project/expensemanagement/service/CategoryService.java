package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.CategoryInputDto;
import com.kma.project.expensemanagement.dto.response.CategoryOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;

public interface CategoryService {

    CategoryOutputDto add(CategoryInputDto inputDto);

    CategoryOutputDto update(Long id, CategoryInputDto inputDto);

    void delete(Long id);

    CategoryOutputDto getDetail(Long id);

    PageResponse<CategoryOutputDto> getAllCategory(Integer page, Integer size, String sort, String search, Long parentId);
}

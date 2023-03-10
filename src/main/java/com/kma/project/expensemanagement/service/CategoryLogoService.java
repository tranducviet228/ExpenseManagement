package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.response.CategoryLogoOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryLogoService {

    void add(MultipartFile[] files);

    PageResponse<CategoryLogoOutputDto> getAll(Integer page, Integer size, String sort);
}

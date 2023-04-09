package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.response.CategoryLogoOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.service.CategoryLogoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/category-logo")
@Api(tags = "Quản lí logo danh mục thu chi")
public class CategoryLogoController {

    @Autowired
    CategoryLogoService categoryLogoService;

    @ApiOperation(value = "Thêm mới logo danh mục")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void add(@RequestPart("files") MultipartFile[] files) {
        categoryLogoService.add(files);
    }

    @ApiOperation(value = "Xóa logo danh mục")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        categoryLogoService.delete(id);
    }

    @ApiOperation(value = "Lấy danh sách logo danh mục ")
    @GetMapping
    public PageResponse<CategoryLogoOutputDto> getAll(Integer page, Integer size, String sort) {
        return categoryLogoService.getAll(page, size, sort);
    }

}

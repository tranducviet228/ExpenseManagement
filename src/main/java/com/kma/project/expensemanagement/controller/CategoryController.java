package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.CategoryInputDto;
import com.kma.project.expensemanagement.dto.response.CategoryOutputDto;
import com.kma.project.expensemanagement.dto.response.ContentResponse;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.exception.AppResponseDto;
import com.kma.project.expensemanagement.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/category")
@Api(tags = "Quản lí danh mục thu chi")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @ApiOperation(value = "Thêm mới danh mục thu chi")
    @PostMapping
    public CategoryOutputDto add(@RequestBody CategoryInputDto inputDto) {
        return categoryService.add(inputDto);
    }

    @ApiOperation(value = "Cập nhật danh mục thu chi")
    @PutMapping(value = "/{id}")
    public CategoryOutputDto update(@PathVariable("id") Long id, @RequestBody CategoryInputDto inputDto) {
        return categoryService.update(id, inputDto);
    }

    @ApiOperation(value = "Lấy chi tiết danh mục thu chi")
    @GetMapping("/{id}")
    public DataResponse<CategoryOutputDto> getDetail(@PathVariable("id") Long id) {
        return categoryService.getDetail(id);
    }

    @ApiOperation(value = "Xóa danh mục thu chi")
    @DeleteMapping("/{id}")
    public AppResponseDto<Object> delete(@PathVariable("id") Long id) {
        return categoryService.delete(id);
    }

    @ApiOperation(value = "Lấy danh sách danh mục thu chi")
    @GetMapping
    public PageResponse<CategoryOutputDto> getAllByParentId(Integer page, Integer size, String sort, String search, Long parentId) {
        return categoryService.getAllCategoryByParentId(page, size, sort, search, parentId);
    }


    @ApiOperation(value = "Lấy tất cả danh mục thu chi")
    @GetMapping("/all")
    public ContentResponse<Set<CategoryOutputDto>> getAll(String search, @RequestParam String type) {
        return categoryService.getAllCategory(search, type);
    }
}

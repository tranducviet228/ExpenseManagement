//package com.kma.project.expensemanagement.controller;
//
//import com.kma.project.expensemanagement.dto.request.CategoryInputDto;
//import com.kma.project.expensemanagement.dto.response.CategoryOutputDto;
//import com.kma.project.expensemanagement.service.CategoryService;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/category")
//public class TransactionController {
//
//    @Autowired
//    CategoryService categoryService;
//
//    @ApiOperation(value = "Thêm mới danh mục thu chi")
//    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public CategoryOutputDto add(@ModelAttribute CategoryInputDto inputDto) {
//        return categoryService.add(inputDto);
//    }
//
//    @ApiOperation(value = "Cập nhật danh mục thu chi")
//    @PostMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public CategoryOutputDto update(@PathVariable("id") Long id, @ModelAttribute CategoryInputDto inputDto) {
//        return categoryService.update(id, inputDto);
//    }
//
//    @ApiOperation(value = "Lấy chi tiết danh mục thu chi")
//    @GetMapping("/{id}")
//    public CategoryOutputDto getDetail(@PathVariable("id") Long id) {
//        return categoryService.getDetail(id);
//    }
//
//    @ApiOperation(value = "Xóa danh mục thu chi")
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable("id") Long id) {
//        categoryService.delete(id);
//    }
//
//    @ApiOperation(value = "Lấy danh sách danh mục thu chi")
//    @GetMapping
//    public Page<CategoryOutputDto> getAll(Integer page, Integer size, String sort, String search) {
//        return categoryService.getAllCategory(page, size, sort, search);
//    }
//
//}

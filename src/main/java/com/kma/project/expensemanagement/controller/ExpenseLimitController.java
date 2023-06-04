package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.ExpenseLimitInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.ExpenseLimitOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.service.ExpenseLimitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expense-limit")
@Api(tags = "Quản lí hạn mức")
public class ExpenseLimitController {

    @Autowired
    ExpenseLimitService expenseLimitService;

    @ApiOperation(value = "Thêm mới hạn mức")
    @PostMapping
    public ExpenseLimitOutputDto add(@RequestBody ExpenseLimitInputDto inputDto) {
        return expenseLimitService.add(inputDto);
    }

    @ApiOperation(value = "Cập nhật hạn mức")
    @PutMapping(value = "/{id}")
    public ExpenseLimitOutputDto update(@PathVariable("id") Long id, @RequestBody ExpenseLimitInputDto inputDto) {
        return expenseLimitService.update(id, inputDto);
    }

    @ApiOperation(value = "Lấy chi tiết hạn mức")
    @GetMapping("/{id}")
    public DataResponse<ExpenseLimitOutputDto> getDetail(@PathVariable("id") Long id) {
        return expenseLimitService.getDetail(id);
    }

    @ApiOperation(value = "Xóa hạn mức")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        expenseLimitService.delete(id);
    }

    @ApiOperation(value = "Lấy danh sách hạn mức")
    @GetMapping
    public PageResponse<ExpenseLimitOutputDto> getAll(Integer page, Integer size, String sort, String search) {
        return expenseLimitService.getAllExpenseLimit(page, size, sort, search);
    }

}

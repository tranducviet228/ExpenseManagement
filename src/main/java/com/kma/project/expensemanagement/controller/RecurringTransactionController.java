package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.RecurringTransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.RecurringTransactionOutputDto;
import com.kma.project.expensemanagement.service.RecurringTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/recurring-transaction")
@Api(tags = "Quản lí giao dịch định kì")
public class RecurringTransactionController {

    @Autowired
    RecurringTransactionService transactionService;

    @ApiOperation(value = "Thêm mới giao dịch định kì")
    @PostMapping
    public RecurringTransactionOutputDto add(@Valid @RequestBody RecurringTransactionInputDto inputDto) {
        return transactionService.add(inputDto);
    }

    @ApiOperation(value = "Cập nhật giao dịch định kì")
    @PostMapping(value = "/{id}")
    public RecurringTransactionOutputDto update(@PathVariable("id") Long id, @RequestBody RecurringTransactionInputDto inputDto) {
        return transactionService.update(id, inputDto);
    }

    @ApiOperation(value = "Lấy chi tiết giao dịch định kì")
    @GetMapping("/{id}")
    public DataResponse<RecurringTransactionOutputDto> getDetail(@PathVariable("id") Long id) {
        return transactionService.getDetail(id);
    }

    @ApiOperation(value = "Xóa giao dịch định kì")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        transactionService.delete(id);
    }

    @ApiOperation(value = "Lấy danh sách giao dịch định kì")
    @GetMapping
    public PageResponse<RecurringTransactionOutputDto> getAll(Integer page, Integer size, String sort, String search,
                                                              @RequestParam(required = true) String type,
                                                              @RequestParam(required = true) String status) {
        return transactionService.getAllTransaction(page, size, sort, search, type, status);
    }


}

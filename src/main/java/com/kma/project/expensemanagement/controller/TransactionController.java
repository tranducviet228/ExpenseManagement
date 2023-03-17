package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.TransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.TransactionOutputDto;
import com.kma.project.expensemanagement.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/transaction")
@Api(tags = "Quản lí giao dịch thu chi")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @ApiOperation(value = "Thêm mới giao dịch")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public TransactionOutputDto add(@Valid @ModelAttribute TransactionInputDto inputDto) {
        return transactionService.add(inputDto);
    }

    @ApiOperation(value = "Cập nhật giao dịch")
    @PostMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public TransactionOutputDto update(@PathVariable("id") Long id, @ModelAttribute TransactionInputDto inputDto) {
        return transactionService.update(id, inputDto);
    }

    @ApiOperation(value = "Lấy chi tiết giao dịch")
    @GetMapping("/{id}")
    public DataResponse<TransactionOutputDto> getDetail(@PathVariable("id") Long id) {
        return transactionService.getDetail(id);
    }

    @ApiOperation(value = "Xóa giao dịch")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        transactionService.delete(id);
    }

    @ApiOperation(value = "Lấy danh sách giao dịch")
    @GetMapping
    public PageResponse<TransactionOutputDto> getAll(Integer page, Integer size, String sort, String search) {
        return transactionService.getAllTransaction(page, size, sort, search);
    }

}

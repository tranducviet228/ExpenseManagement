package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.WalletInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;
import com.kma.project.expensemanagement.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@Api(tags = "Quản lí ví")
public class WalletController {

    @Autowired
    WalletService walletService;

    @PostMapping
    public WalletOutputDto add(@RequestBody WalletInputDto inputDto) {
        return walletService.add(inputDto);
    }

    @PutMapping(value = "/{id}")
    public WalletOutputDto update(@PathVariable("id") Long id, @RequestBody WalletInputDto inputDto) {
        return walletService.update(id, inputDto);
    }

    @GetMapping("/{id}")
    public DataResponse<WalletOutputDto> getDetail(@PathVariable("id") Long id) {
        return walletService.getDetail(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        walletService.delete(id);
    }

    @ApiOperation(value = "Lấy danh sách ví")
    @GetMapping
    public PageResponse<WalletOutputDto> getAll(Integer page, Integer size, String sort) {
        return walletService.getAllWallet(page, size, sort);
    }

}

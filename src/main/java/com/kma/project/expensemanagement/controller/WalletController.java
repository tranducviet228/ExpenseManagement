package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.WalletInputDto;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;
import com.kma.project.expensemanagement.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@ApiOperation(value = "Quản lí ví")
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
    public WalletOutputDto getDetail(@PathVariable("id") Long id) {
        return walletService.getDetail(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        walletService.delete(id);
    }

}

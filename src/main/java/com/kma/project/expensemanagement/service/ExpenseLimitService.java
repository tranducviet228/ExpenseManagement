package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.ExpenseLimitInputDto;
import com.kma.project.expensemanagement.dto.request.TransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.ExpenseLimitOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;

public interface ExpenseLimitService {

    void updateToLimit(TransactionInputDto transactionInputDto);

    ExpenseLimitOutputDto add(ExpenseLimitInputDto inputDto);

    ExpenseLimitOutputDto update(Long id, ExpenseLimitInputDto inputDto);

    void delete(Long id);

    DataResponse<ExpenseLimitOutputDto> getDetail(Long id);

    PageResponse<ExpenseLimitOutputDto> getAllTransaction(Integer page, Integer size, String sort, String search);
}

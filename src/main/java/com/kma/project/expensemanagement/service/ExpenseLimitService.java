package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.ExpenseLimitInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.ExpenseLimitOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.entity.TransactionEntity;

public interface ExpenseLimitService {

    void updateToLimit(TransactionEntity entity);

    ExpenseLimitOutputDto add(ExpenseLimitInputDto inputDto);

    ExpenseLimitOutputDto update(Long id, ExpenseLimitInputDto inputDto);

    void delete(Long id);

    DataResponse<ExpenseLimitOutputDto> getDetail(Long id);

    PageResponse<ExpenseLimitOutputDto> getAllExpenseLimit(Integer page, Integer size, String sort, String search);
}

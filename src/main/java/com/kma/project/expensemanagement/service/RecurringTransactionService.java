package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.RecurringTransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.RecurringTransactionOutputDto;

public interface RecurringTransactionService {

    RecurringTransactionOutputDto add(RecurringTransactionInputDto inputDto);

    RecurringTransactionOutputDto update(Long id, RecurringTransactionInputDto inputDto);

    void delete(Long id);

    DataResponse<RecurringTransactionOutputDto> getDetail(Long id);

    PageResponse<RecurringTransactionOutputDto> getAllTransaction(Integer page, Integer size, String sort, String search,
                                                                  String type, String status);

}

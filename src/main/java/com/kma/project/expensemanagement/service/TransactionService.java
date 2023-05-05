package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.TransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.TransactionOutputDto;
import com.kma.project.expensemanagement.entity.TransactionEntity;

public interface TransactionService {

    TransactionOutputDto add(TransactionInputDto inputDto);

    TransactionOutputDto update(Long id, TransactionInputDto inputDto);

    void delete(Long id);

    DataResponse<TransactionOutputDto> getDetail(Long id);

    PageResponse<TransactionOutputDto> getAllTransaction(Integer page, Integer size, String sort, String search);

    void mapDataResponse(TransactionOutputDto outputDto, TransactionEntity entity);


}

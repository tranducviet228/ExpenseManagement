package com.kma.project.expensemanagement.mapper;

import com.kma.project.expensemanagement.dto.request.RecurringTransactionInputDto;
import com.kma.project.expensemanagement.dto.response.RecurringTransactionOutputDto;
import com.kma.project.expensemanagement.entity.RecurringTransactionEntity;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RecurringTransactionMapper {

    RecurringTransactionEntity convertToEntity(RecurringTransactionInputDto dto);

    RecurringTransactionOutputDto convertToDto(RecurringTransactionEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RecurringTransactionEntity update(RecurringTransactionInputDto dto, @MappingTarget RecurringTransactionEntity entity);

    TransactionEntity convertToTransaction(RecurringTransactionEntity recurringTransactionEntity);
}

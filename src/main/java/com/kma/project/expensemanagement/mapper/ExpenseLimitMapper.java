package com.kma.project.expensemanagement.mapper;

import com.kma.project.expensemanagement.dto.request.ExpenseLimitInputDto;
import com.kma.project.expensemanagement.dto.response.ExpenseLimitOutputDto;
import com.kma.project.expensemanagement.entity.ExpenseLimitEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ExpenseLimitMapper {

    ExpenseLimitEntity convertToEntity(ExpenseLimitInputDto dto);

    ExpenseLimitOutputDto convertToOutputDto(ExpenseLimitEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ExpenseLimitInputDto dto, @MappingTarget ExpenseLimitEntity entity);
}

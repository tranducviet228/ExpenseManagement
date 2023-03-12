package com.kma.project.expensemanagement.mapper;

import com.kma.project.expensemanagement.dto.request.WalletInputDto;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;
import com.kma.project.expensemanagement.entity.WalletEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletEntity convertToEntity(WalletInputDto dto);

    WalletOutputDto convertToDto(WalletEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    WalletEntity update(WalletInputDto dto, @MappingTarget WalletEntity entity);

}

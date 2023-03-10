package com.kma.project.expensemanagement.mapper;

import com.kma.project.expensemanagement.dto.request.CategoryInputDto;
import com.kma.project.expensemanagement.dto.response.CategoryOutputDto;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryEntity convertToEntity(CategoryInputDto dto);

    CategoryOutputDto convertToDto(CategoryEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CategoryEntity update(CategoryInputDto dto, @MappingTarget CategoryEntity entity);

}

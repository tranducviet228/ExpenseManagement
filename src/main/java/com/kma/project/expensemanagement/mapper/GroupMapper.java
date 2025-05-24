package com.kma.project.expensemanagement.mapper;

import com.kma.project.expensemanagement.dto.request.GroupInputDto;
import com.kma.project.expensemanagement.dto.response.GroupOutputDto;
import com.kma.project.expensemanagement.entity.GroupEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupEntity convertToEntity(GroupInputDto dto);

    GroupEntity createNewEntity(GroupEntity entity);

    GroupOutputDto convertToDto(GroupEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GroupEntity update(GroupInputDto dto, @MappingTarget GroupEntity entity);

}

package com.kma.project.expensemanagement.mapper;

import com.kma.project.expensemanagement.dto.response.UserOutputDto;
import com.kma.project.expensemanagement.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserOutputDto convertToDto(UserEntity userEntity);

}

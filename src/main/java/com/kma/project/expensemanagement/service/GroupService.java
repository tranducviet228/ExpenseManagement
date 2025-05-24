package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.GroupInputDto;
import com.kma.project.expensemanagement.dto.response.GroupOutputDto;
import com.kma.project.expensemanagement.dto.response.ContentResponse;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.exception.AppResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface GroupService {

    GroupOutputDto add(GroupInputDto inputDto);

    GroupOutputDto update(Long id, GroupInputDto inputDto);

    AppResponseDto<Object> delete(Long id);

    DataResponse<GroupOutputDto> getDetail(Long id);

    PageResponse<GroupOutputDto> getAllGroup(Integer page, Integer size, String sort, String search);

}

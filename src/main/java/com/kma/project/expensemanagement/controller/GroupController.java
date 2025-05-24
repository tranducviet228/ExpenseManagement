package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.CategoryInputDto;
import com.kma.project.expensemanagement.dto.request.GroupInputDto;
import com.kma.project.expensemanagement.dto.response.*;
import com.kma.project.expensemanagement.exception.AppResponseDto;
import com.kma.project.expensemanagement.service.GroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/group")
@Api(tags = "Quản lí nhóm thu chi")
public class GroupController {

    @Autowired
    GroupService groupService;

    @ApiOperation(value = "Thêm mới nhóm thu chi")
    @PostMapping
    public GroupOutputDto add(@RequestBody GroupInputDto inputDto) {
        return groupService.add(inputDto);
    }

    @ApiOperation(value = "Cập nhật nhóm thu chi")
    @PutMapping(value = "/{id}")
    public GroupOutputDto update(@PathVariable("id") Long id, @RequestBody GroupInputDto inputDto) {
        return groupService.update(id, inputDto);
    }

    @ApiOperation(value = "Lấy chi tiết nhóm thu chi")
    @GetMapping("/{id}")
    public DataResponse<GroupOutputDto> getDetail(@PathVariable("id") Long id) {
        return groupService.getDetail(id);
    }

    @ApiOperation(value = "Xóa nhóm thu chi")
    @DeleteMapping("/{id}")
    public AppResponseDto<Object> delete(@PathVariable("id") Long id) {
        return groupService.delete(id);
    }

    @ApiOperation(value = "Lấy danh sách nhóm thu chi")
    @GetMapping
    public PageResponse<GroupOutputDto> getAllGroup(Integer page, Integer size, String sort, String search) {
        return groupService.getAllGroup(page, size, sort, search);
    }

}

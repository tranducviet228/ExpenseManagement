package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.GroupInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.GroupMemberOutputDto;
import com.kma.project.expensemanagement.dto.response.GroupOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.entity.GroupEntity;
import com.kma.project.expensemanagement.entity.GroupMemberEntity;
import com.kma.project.expensemanagement.entity.UserEntity;
import com.kma.project.expensemanagement.enums.RoleOfGroup;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.exception.AppResponseDto;
import com.kma.project.expensemanagement.mapper.GroupMapper;
import com.kma.project.expensemanagement.repository.GroupMemberRepository;
import com.kma.project.expensemanagement.repository.GroupRepository;
import com.kma.project.expensemanagement.repository.UserRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.service.GroupService;
import com.kma.project.expensemanagement.utils.DataUtils;
import com.kma.project.expensemanagement.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    @Autowired
    GroupRepository repository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    GroupMapper mapper;

    @Transactional
    @Override
    public GroupOutputDto add(GroupInputDto inputDto) {
        GroupEntity groupEntity = mapper.convertToEntity(inputDto);
        groupEntity.setCreatedBy(jwtUtils.getCurrentUserId());
        repository.save(groupEntity);

        // add member to group
        List<GroupMemberEntity> groupMemberEntities = addMemberToGroup(groupEntity.getId(), inputDto.getMemberUserNames());

        // add leader to group
        groupMemberEntities.add(GroupMemberEntity.builder()
                .groupId(groupEntity.getId())
                .userId(jwtUtils.getCurrentUserId())
                .roleOfGroup(RoleOfGroup.LEADER).build());

        groupMemberRepository.saveAll(groupMemberEntities);
        return mapper.convertToDto(groupEntity);
    }

    private List<GroupMemberEntity> addMemberToGroup(Long groupId, List<String> memberUserNames){
        List<UserEntity> userEntities = userRepository.findAllByUsernameIn(memberUserNames);
        Set<Long> memberIds = userEntities.stream().map(UserEntity::getId).collect(Collectors.toSet());

        return memberIds.stream().map(aLong -> GroupMemberEntity.builder()
                .groupId(groupId)
                .userId(aLong)
                .roleOfGroup(RoleOfGroup.MEMBER)
                .build()).collect(Collectors.toList());

    }

    @Transactional
    @Override
    public GroupOutputDto update(Long id, GroupInputDto inputDto) {
        GroupEntity groupEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.Group-not-found")).build());
        groupEntity = mapper.update(inputDto, groupEntity);
        repository.save(groupEntity);

        // remove or add to group
        List<GroupMemberEntity> groupMembers = groupMemberRepository.findAllByGroupId(id);
        groupMembers = groupMembers.stream().filter(groupMemberEntity ->
            RoleOfGroup.MEMBER.equals(groupMemberEntity.getRoleOfGroup())
        ).collect(Collectors.toList());
        groupMemberRepository.deleteAll(groupMembers);

        List<GroupMemberEntity> groupMemberEntities = addMemberToGroup(groupEntity.getId(), inputDto.getMemberUserNames());
        groupMemberRepository.saveAll(groupMemberEntities);

        return mapper.convertToDto(groupEntity);
    }

    @Transactional
    @Override
    public AppResponseDto<Object> delete(Long id) {
        GroupEntity groupEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.Group-not-found")).build());

        repository.delete(groupEntity);

        List<GroupMemberEntity> groupMembers = groupMemberRepository.findAllByGroupId(id);
        groupMemberRepository.deleteAll(groupMembers);
        return AppResponseDto.builder().httpStatus(200).message("Xóa thành công").isDelete(true).build();
    }

    @Override
    public DataResponse<GroupOutputDto> getDetail(Long id) {
        GroupEntity groupEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.group-not-found")).build());
        GroupOutputDto groupOutputDto = mapper.convertToDto(groupEntity);

        List<GroupMemberOutputDto> groupMembers = groupMemberRepository.findAllOutPutByGroupId(id);
        groupOutputDto.setGroupMembers(groupMembers);
        return DataUtils.formatData(groupOutputDto);
    }

    @Override
    public PageResponse<GroupOutputDto> getAllGroup(Integer page, Integer size, String sort, String search) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        Page<GroupEntity> groupEntities = repository.findAllGroup(pageable, search, jwtUtils.getCurrentUserId());
        return PageUtils.formatPageResponse(groupEntities.map(mapper::convertToDto));
    }

}

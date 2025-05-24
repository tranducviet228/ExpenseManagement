package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.dto.response.GroupMemberOutputDto;
import com.kma.project.expensemanagement.entity.GroupMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, Long> {


    List<GroupMemberEntity> findAllByGroupId(Long groupId);

    @Query(" select new com.kma.project.expensemanagement.dto.response.GroupMemberOutputDto(gm.userId, u.username, gm.groupId, gm.roleOfGroup)" +
            "from GroupMemberEntity gm join UserEntity u on gm.userId = u.id " +
            "where gm.groupId = :groupId")
    List<GroupMemberOutputDto> findAllOutPutByGroupId(Long groupId);
}

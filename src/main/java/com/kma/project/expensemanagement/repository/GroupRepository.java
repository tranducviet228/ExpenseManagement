package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    @Query("select ge from GroupEntity ge join GroupMemberEntity gm on ge.id = gm.groupId " +
            "where gm.userId = :userId and (:search is null or (ge.name like '%:search%'))")
    Page<GroupEntity> findAllGroup(Pageable pageable, String search, Long userId);

}

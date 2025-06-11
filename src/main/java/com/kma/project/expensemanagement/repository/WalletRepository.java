package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.WalletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Page<WalletEntity> findAllByCreatedBy(Pageable pageable, Long createdBy);

    @Query("SELECT DISTINCT w FROM WalletEntity w " +
            "LEFT JOIN GroupMemberEntity g ON w.groupId = g.groupId " +
            "WHERE (w.createdBy = :createdBy) OR (w.groupId IS NOT NULL AND g.userId = :createdBy) " +
            "ORDER BY w.createdAt")
    List<WalletEntity> findAllWallet(@Param("createdBy") Long createdBy);


    @Query(value = " select w from WalletEntity w where (:groupId is not null and w.groupId = :groupId) order by w.createdAt")
    List<WalletEntity> findAllByGroupIdOrderByCreatedAt(Long groupId);

    @Query(value = " select w.id from WalletEntity w where w.createdBy = :userId or (:groupId is not null and w.groupId = :groupId)")
    List<Long> getAllWalletId(Long userId, Long groupId);


}

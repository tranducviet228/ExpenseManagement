package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.WalletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Page<WalletEntity> findAllByCreatedBy(Pageable pageable, Long createdBy);

    @Query(value = " select w from WalletEntity w left join GroupEntity g on w.groupId = g.id " +
            "where (w.createdBy = :createdBy) or (w.groupId is not null) order by w.createdAt")
    List<WalletEntity> findAllWallet(Long createdBy);

    @Query(value = " select w from WalletEntity w where (:groupId is not null and w.groupId = :groupId) order by w.createdAt")
    List<WalletEntity> findAllByGroupIdOrderByCreatedAt(Long groupId);

    @Query(value = " select w.id from WalletEntity w where w.createdBy = :userId or w.groupId = :groupId")
    List<Long> getAllWalletId(Long userId, Long groupId);


}

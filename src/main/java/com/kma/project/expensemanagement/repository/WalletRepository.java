package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.enums.ScopeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Page<WalletEntity> findAllByCreatedBy(Pageable pageable, Long createdBy);

    List<WalletEntity> findAllByCreatedByAndScopeTypeOrderByCreatedAt(Long createdBy, ScopeType scopeType);

    @Query(value = " select w.id from WalletEntity w where w.createdBy = :userId and w.scopeType = :scopeType")
    List<Long> getAllWalletId(Long userId, ScopeType scopeType);


}

package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    Page<TransactionEntity> findAllByCreatedBy(Pageable pageable, Long createdById);

}

package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.WalletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Page<WalletEntity> findAllByCreatedBy(Pageable pageable, Long createdBy);

    List<WalletEntity> findAllByCreatedBy(Long createdBy);


}

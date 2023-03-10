package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

}

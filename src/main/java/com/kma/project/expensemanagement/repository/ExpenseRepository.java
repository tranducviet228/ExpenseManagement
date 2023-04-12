package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.ExpenseLimitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseLimitEntity, Long> {

    Page<ExpenseLimitEntity> getAllByCreatedByAndLimitNameLikeIgnoreCase(Pageable pageable, Long userId, String search);

    @Query(value = " select * from expense_limits where " +
            " :categoryId = any(category_ids) and :walletId = any(wallet_ids) " +
            " and (from_date <= :date and (to_date >= :date or to_date is null) )", nativeQuery = true)
    List<ExpenseLimitEntity> getValidExpenseLimit(Long categoryId, Long walletId, LocalDate date);
}

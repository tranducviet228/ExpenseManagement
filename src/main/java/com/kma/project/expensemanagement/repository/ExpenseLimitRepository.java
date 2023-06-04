package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.ExpenseLimitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseLimitRepository extends JpaRepository<ExpenseLimitEntity, Long> {

    Page<ExpenseLimitEntity> getAllByCreatedByAndLimitNameLikeIgnoreCase(Pageable pageable, Long userId, String search);

    @Query(value = " select * from expense_limits where " +
            " (:categoryId = any(category_ids)) and (:walletId = any(wallet_ids)) " +
            " and (from_date <= :date and (to_date >= :date or to_date is null) " +
            " and created_by = :userId) ", nativeQuery = true)
    List<ExpenseLimitEntity> getValidExpenseLimit(String categoryId, String walletId, LocalDate date, Long userId);

    @Query(value = " select r from ExpenseLimitEntity r where" +
            " r.createdBy = :createdById and r.toDate < :date ")
    Page<ExpenseLimitEntity> findAllEndLimit(Pageable pageable, Long createdById, LocalDate date);

    @Query(value = " select r from ExpenseLimitEntity r where" +
            " r.createdBy = :createdById and r.fromDate <= :date and (r.toDate >= :date or r.toDate is null)")
    Page<ExpenseLimitEntity> findAllStartLimit(Pageable pageable, Long createdById, LocalDate date);

    @Query(value = " select r from ExpenseLimitEntity r where" +
            " r.createdBy = :createdById and r.fromDate > :date ")
    Page<ExpenseLimitEntity> findAllNextLimit(Pageable pageable, Long createdById, LocalDate date);
}

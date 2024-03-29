package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.RecurringTransactionEntity;
import com.kma.project.expensemanagement.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransactionEntity, Long> {

    @Query(value = " select r from RecurringTransactionEntity r where" +
            " r.createdBy = :createdById and r.toDate < :date and r.transactionType = :type ")
    Page<RecurringTransactionEntity> findAllEndTransaction(Pageable pageable, Long createdById, LocalDate date, TransactionType type);

    @Query(value = " select r from RecurringTransactionEntity r where" +
            " r.createdBy = :createdById and r.fromDate <= :date and (r.toDate >= :date or r.toDate is null) " +
            " and r.transactionType = :type ")
    Page<RecurringTransactionEntity> findAllStartTransaction(Pageable pageable, Long createdById, LocalDate date, TransactionType type);

    @Query(value = " select r from RecurringTransactionEntity r where" +
            " r.createdBy = :createdById and r.fromDate > :date and r.transactionType = :type ")
    Page<RecurringTransactionEntity> findAllNextTransaction(Pageable pageable, Long createdById, LocalDate date, TransactionType type);

    @Query(value = " select r from RecurringTransactionEntity r" +
            " where (:date_time between r.fromDate and r.toDate) or (:date_time >= r.fromDate and r.toDate is null)")
    List<RecurringTransactionEntity> getAllValidRecurring(@Param("date_time") LocalDate date_time);


    Page<RecurringTransactionEntity> getAllRecurringByCreatedByAndTransactionType(Pageable pageable, Long createdBy, TransactionType type);

    @Query(value = "select count(r) from RecurringTransactionEntity r where r.category.id in :categoryIds")
    Long countAllByCategoryIn(@Param("categoryIds") Collection<Long> categoryIds);


}

package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.RecurringTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransactionEntity, Long> {

    Page<RecurringTransactionEntity> findAllByCreatedBy(Pageable pageable, Long createdById);


    @Query(value = " select r from RecurringTransactionEntity r" +
            " where (:date_time between r.fromDate and r.toDate) or (:date_time >= r.fromDate and r.toDate is null)")
    List<RecurringTransactionEntity> getAllValidRecurring(@Param("date_time") LocalDateTime date_time);

    Long countAllByCategory(CategoryEntity category);


}

package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    Page<TransactionEntity> findAllByCreatedBy(Pageable pageable, Long createdById);

//    @Query(value = " select t.ariseDate, sum(t.amount) from TransactionEntity t where " +
//            " t.ariseDate between :fromDate and :toDate group by t.ariseDate")
//    List<Map<String, BigDecimal>> findTransactionByDate(LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = " select t from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and (:walletId is null or t.wallet.id = :walletId) ")
    List<TransactionEntity> findAllTransactionByWalletId(LocalDateTime fromDate, LocalDateTime toDate, Long walletId);

    @Query(value = " select t from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate ")
    List<TransactionEntity> findAllInMonth(LocalDateTime fromDate, LocalDateTime toDate);

    @Query("SELECT MONTH(t.createdAt) AS name, SUM(CASE WHEN t.transactionType = 'EXPENSE' THEN t.amount ELSE 0 END) AS expenseTotal, "
            + " SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.amount ELSE 0 END) AS incomeTotal "
            + " FROM TransactionEntity t where YEAR(t.createdAt) = :year "
            + " GROUP BY MONTH(t.createdAt)")
    List<ReportData> sumAmountByMonth(Integer year);

//    @Query("SELECT MONTH(t.createdAt) AS month, SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) AS totalIncome,
//    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) AS totalExpense "
//            + "FROM Transaction t WHERE YEAR(t.createdAt) = :year "
//            + "GROUP BY MONTH(t.createdAt)")

    interface ReportData {

        String getName();

        BigDecimal getExpenseTotal();

        BigDecimal getIncomeTotal();


    }


}

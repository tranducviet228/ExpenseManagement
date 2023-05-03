package com.kma.project.expensemanagement.repository;

import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query(value = " select t from TransactionEntity t where " +
            " t.ariseDate between :fromDate and :toDate and (t.wallet.id in :walletIds) and t.createdBy = :userId ")
    List<TransactionEntity> findAllTransactionByAriseDate(LocalDateTime fromDate, LocalDateTime toDate, List<Long> walletIds,
                                                          Long userId);

    Page<TransactionEntity> findAllByCreatedBy(Pageable pageable, Long createdById);

//    @Query(value = " select t.ariseDate, sum(t.amount) from TransactionEntity t where " +
//            " t.ariseDate between :fromDate and :toDate group by t.ariseDate")
//    List<Map<String, BigDecimal>> findTransactionByDate(LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = " select t from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and (t.wallet.id in :walletIds) and t.createdBy = :userId ")
    List<TransactionEntity> findAllTransactionByWalletId(LocalDateTime fromDate, LocalDateTime toDate, List<Long> walletIds,
                                                         Long userId);

    @Query(value = " select t from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and t.createdBy = :userId ")
    List<TransactionEntity> findAllInMonth(LocalDateTime fromDate, LocalDateTime toDate, Long userId);

    @Query(value = " select sum(t.amount) from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and t.transactionType = :tranType " +
            " and ( t.wallet.id in :walletIds ) and t.createdBy = :userId ")
    BigDecimal sumTotalByWalletIdAndTranType(LocalDateTime fromDate, LocalDateTime toDate, List<Long> walletIds,
                                             TransactionType tranType, Long userId);

    @Query("SELECT MONTH(t.createdAt) AS name, SUM(CASE WHEN t.transactionType = 'EXPENSE' THEN t.amount ELSE 0 END) AS expenseTotal, "
            + " SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.amount ELSE 0 END) AS incomeTotal "
            + " FROM TransactionEntity t where YEAR(t.createdAt) = :year and (t.wallet.id in :walletIds) "
            + " AND t.createdBy = :userId GROUP BY MONTH(t.createdAt)")
    List<ReportData> sumAmountByMonth(Integer year, List<Long> walletIds, Long userId);

//    @Query("SELECT MONTH(t.createdAt) AS month, SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) AS totalIncome,
//    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) AS totalExpense "
//            + "FROM Transaction t WHERE YEAR(t.createdAt) = :year "
//            + "GROUP BY MONTH(t.createdAt)")

    @Query("SELECT SUM(t.amount) AS totalAmount, c.name as categoryName, l.fileUrl as categoryImage " +
            " FROM TransactionEntity t " +
            " join CategoryEntity c on t.category.id = c.id " +
            " join CategoryLogoEntity l on c.logoImageID = l.id " +
            " where t.transactionType = :transactionType and ( t.wallet.id in :walletIds) " +
            " and t.createdAt between :fromDate and :toDate and t.createdBy = :userId " +
            " GROUP BY t.transactionType, c.name, l.fileUrl ")
    List<CategoryDetailReport> getCategoryDetail(TransactionType transactionType, List<Long> walletIds,
                                                 LocalDateTime fromDate, LocalDateTime toDate, Long userId);

    @Query(value = " select sum(amount) as amount, DATE(created_at) as createdAt from transactions where " +
            " (created_at between :fromDate and :toDate) and (transaction_type = :tranType) " +
            " and ( wallet_id in :walletIds) " +
            " and ( category_id in :categoryIds) and created_by = :userId " +
            " group by DATE(created_at) ", nativeQuery = true)
    List<AnalysisDetail> getDayAnalysisDetail(
            @Param(value = "fromDate") LocalDateTime fromDate,
            @Param(value = "toDate") LocalDateTime toDate,
            @Param(value = "tranType") String tranType,
            @Param(value = "walletIds") List<Long> walletIds,
            @Param(value = "categoryIds") List<Long> categoryIds,
            @Param(value = "userId") Long userId
    );

//    @Query(value = " select sum(amount) as amount, YEAR(created_at) AS year, MONTH(created_at) AS month from transactions where " +
//            " (created_at between :fromDate and :toDate) and (transaction_type = :tranType) " +
//            " and ( wallet_id in :walletIds) " +
//            " and ( category_id in :categoryIds) " +
//            " group by YEAR(created_at), MONTH(created_at) ", nativeQuery = true)
//    List<AnalysisMonthDetail> getMonthAnalysisDetail(
//            @Param(value = "fromDate") LocalDateTime fromDate,
//            @Param(value = "toDate") LocalDateTime toDate,
//            @Param(value = "tranType") String tranType,
//            @Param(value = "walletIds") List<Long> walletIds,
//            @Param(value = "categoryIds") List<Long> categoryIds
//    );

    @Query(value = " select sum(t.amount) as amount, YEAR(t.createdAt) AS year, MONTH(t.createdAt) as month from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and t.transactionType = :tranType " +
            " and (t.wallet.id in :walletIds) " +
            " and (t.category.id in :categoryIds) and t.createdBy = :userId " +
            " group by YEAR(t.createdAt), MONTH(t.createdAt) ")
    List<AnalysisMonthDetail> getMonthAnalysisDetail(LocalDateTime fromDate, LocalDateTime toDate, TransactionType tranType,
                                                     List<Long> walletIds, List<Long> categoryIds, Long userId);

    @Query(value = " select sum(t.amount) as amount, YEAR(t.createdAt) as year from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and t.transactionType = :tranType " +
            " and ( t.wallet.id in :walletIds) " +
            " and ( t.category.id in :categoryIds) and t.createdBy = :userId " +
            " group by YEAR(t.createdAt) ")
    List<AnalysisMonthDetail> getYearAnalysisDetail(LocalDateTime fromDate, LocalDateTime toDate, TransactionType tranType,
                                                    List<Long> walletIds, List<Long> categoryIds, Long userId);

    interface AnalysisMonthDetail {
        BigDecimal getAmount();

        Integer getYear();

        Integer getMonth();
    }

    interface AnalysisDetail {

        BigDecimal getAmount();

        LocalDate getCreatedAt();

    }

    interface CategoryDetailReport {

        BigDecimal getTotalAmount();

        String getCategoryName();

        String getCategoryImage();

    }

    interface ReportData {

        String getName();

        BigDecimal getExpenseTotal();

        BigDecimal getIncomeTotal();

    }


}

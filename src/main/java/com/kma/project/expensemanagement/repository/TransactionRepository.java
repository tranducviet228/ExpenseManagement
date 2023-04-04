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

    @Query(value = " select sum(t.amount) from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and t.transactionType = :tranType " +
            " and (:walletId is null or t.wallet.id = :walletId) ")
    BigDecimal sumTotalByWalletIdAndTranType(LocalDateTime fromDate, LocalDateTime toDate, Long walletId, TransactionType tranType);

    @Query("SELECT MONTH(t.createdAt) AS name, SUM(CASE WHEN t.transactionType = 'EXPENSE' THEN t.amount ELSE 0 END) AS expenseTotal, "
            + " SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.amount ELSE 0 END) AS incomeTotal "
            + " FROM TransactionEntity t where YEAR(t.createdAt) = :year "
            + " GROUP BY MONTH(t.createdAt)")
    List<ReportData> sumAmountByMonth(Integer year);

//    @Query("SELECT MONTH(t.createdAt) AS month, SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) AS totalIncome,
//    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) AS totalExpense "
//            + "FROM Transaction t WHERE YEAR(t.createdAt) = :year "
//            + "GROUP BY MONTH(t.createdAt)")

    @Query("SELECT SUM(t.amount) AS totalAmount, c.name as categoryName, l.fileUrl as categoryImage " +
            " FROM TransactionEntity t " +
            " join CategoryEntity c on t.category.id = c.id " +
            " join CategoryLogoEntity l on c.logoImageID = l.id " +
            " where t.transactionType = :transactionType and (:walletId is null or t.wallet.id = :walletId) " +
            " and t.createdAt between :fromDate and :toDate" +
            " GROUP BY t.transactionType, c.name, l.fileUrl ")
    List<CategoryDetailReport> getCategoryDetail(TransactionType transactionType, Long walletId,
                                                 LocalDateTime fromDate, LocalDateTime toDate);

//    @Query(value = " select sum(t.amount) as amount, TO_CHAR(t.createdAt, 'yyyy-MM-') as createdAt from TransactionEntity t where " +
//            " t.createdAt between :fromDate and :toDate and t.transactionType = :tranType " +
//            " and (:walletIds is null or t.wallet.id in :walletIds) " +
//            " and (:categoryIds is null or t.category.id in :categoryIds) " +
//            " group by DATE(t.createdAt) ")
//    List<AnalysisDetail> getDayAnalysisDetail(LocalDateTime fromDate, LocalDateTime toDate, String tranType,
//                                           List<Long> walletIds, List<Long> categoryIds);

    @Query(value = " select sum(amount) as amount, DATE(arise_date) as createdAt from transactions where " +
            " (created_at between :fromDate and :toDate) and (transaction_type = :tranType) " +
            " and ( wallet_id in :walletIds) " +
//            " and (:categoryIds is null or category_id in :categoryIds) " +
            " group by DATE(arise_date) ", nativeQuery = true)
    List<AnalysisDetail> getDayAnalysisDetail(
            @Param(value = "fromDate") LocalDateTime fromDate,
            @Param(value = "toDate") LocalDateTime toDate,
            @Param(value = "tranType") String tranType,
            @Param(value = "walletIds") List<Long> walletIds
//            @Param(value = "categoryIds") List<Long> categoryIds
    );

    @Query(value = " select sum(t.amount), MONTH(t.createdAt) from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and t.transactionType = :tranType " +
            " and (:walletIds is null or t.wallet.id in :walletIds) " +
            " and (:categoryIds is null or t.category.id in :categoryIds) " +
            " group by MONTH(t.createdAt) ")
    List<AnalysisDetail> getMonthAnalysisDetail(LocalDateTime fromDate, LocalDateTime toDate, TransactionType tranType,
                                                List<Long> walletIds, List<Long> categoryIds);

    @Query(value = " select sum(t.amount), YEAR(t.createdAt) from TransactionEntity t where " +
            " t.createdAt between :fromDate and :toDate and t.transactionType = :tranType " +
            " and (:walletIds is null or t.wallet.id in :walletIds) " +
            " and (:categoryIds is null or t.category.id in :categoryIds) " +
            " group by YEAR(t.createdAt) ")
    List<AnalysisDetail> getYearAnalysisDetail(LocalDateTime fromDate, LocalDateTime toDate, TransactionType tranType,
                                               List<Long> walletIds, List<Long> categoryIds);

    interface AnalysisDetail {

        BigDecimal getAmount();

        LocalDateTime getCreatedAt();

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

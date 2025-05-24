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
import java.util.Collection;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query(value = " select t from TransactionEntity t where " +
            " t.ariseDate between :fromDate and :toDate and (t.wallet.id in :walletIds) and t.createdBy = :userId " +
            " and(:groupId IS NULL OR t.groupId = :groupId) order by t.ariseDate desc")
    List<TransactionEntity> findAllTransactionByAriseDate(LocalDateTime fromDate, LocalDateTime toDate, List<Long> walletIds,
                                                          Long userId, Long groupId);

//    @Query(value = " select t from TransactionEntity t where " +
//            " t.ariseDate between :fromDate and :toDate order by t.ariseDate ")
//    List<TransactionEntity> findAllTransactionByAriseDate(LocalDateTime fromDate, LocalDateTime toDate);

    Page<TransactionEntity> findAllByCreatedByAndGroupId(Pageable pageable, Long createdById, Long groupId);

    @Query(value = " select t from TransactionEntity t where " +
            " t.ariseDate between :fromDate and :toDate and (t.wallet.id in :walletIds) and t.createdBy = :userId " +
            " and (:groupId IS NULL OR t.groupId = :groupId)")
    List<TransactionEntity> findAllTransactionByWalletId(LocalDateTime fromDate, LocalDateTime toDate, List<Long> walletIds,
                                                         Long userId, Long groupId);

//    @Query(value = " select sum(amount) as amount, DATE(arise_date) as createdAt from transactions t where " +
//            " t.arise_date between :fromDate and :toDate and t.transaction_type = 'EXPENSE' " +
//            " and t.created_by = :userId and (:groupId IS NULL OR t.group_id = :groupId) group by DATE(arise_date)", nativeQuery = true)
//    List<AnalysisDetail> getTotalInWeek(LocalDateTime fromDate, LocalDateTime toDate, Long userId, Long groupId);

    @Query(value = "SELECT SUM(amount) AS amount, DATE(arise_date) AS createdAt " +
            "FROM transactions t " +
            "WHERE t.arise_date BETWEEN :fromDate AND :toDate " +
            "AND t.transaction_type = 'EXPENSE' " +
            "AND t.created_by = :userId " +
            "AND t.group_id IS NULL " +
            "GROUP BY DATE(arise_date)", nativeQuery = true)
    List<AnalysisDetail> getTotalInWeekWithoutGroupId(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("userId") Long userId
    );

    @Query(value = "SELECT SUM(amount) AS amount, DATE(arise_date) AS createdAt " +
            "FROM transactions t " +
            "WHERE t.arise_date BETWEEN :fromDate AND :toDate " +
            "AND t.transaction_type = 'EXPENSE' " +
            "AND t.created_by = :userId " +
            "AND t.group_id = :groupId " +
            "GROUP BY DATE(arise_date)", nativeQuery = true)
    List<AnalysisDetail> getTotalInWeekWithGroupId(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("userId") Long userId,
            @Param("groupId") Long groupId
    );


    @Query(value = " select t from TransactionEntity t where " +
            " t.ariseDate between :fromDate and :toDate and t.createdBy = :userId and(:groupId IS NULL OR t.groupId = :groupId)")
    List<TransactionEntity> findAllInMonth(LocalDateTime fromDate, LocalDateTime toDate, Long userId, Long groupId);

    @Query(value = " select sum(t.amount) from TransactionEntity t where " +
            " t.ariseDate between :fromDate and :toDate and t.transactionType = :tranType " +
            " and ( t.wallet.id in :walletIds ) and t.createdBy = :userId and(:groupId IS NULL OR t.groupId = :groupId)")
    BigDecimal sumTotalByWalletIdAndTranType(LocalDateTime fromDate, LocalDateTime toDate, List<Long> walletIds,
                                             TransactionType tranType, Long userId, Long groupId);

    @Query("SELECT MONTH(t.ariseDate) AS name, SUM(CASE WHEN t.transactionType = 'EXPENSE' THEN t.amount ELSE 0 END) AS expenseTotal, "
            + " SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.amount ELSE 0 END) AS incomeTotal "
            + " FROM TransactionEntity t where YEAR(t.ariseDate) = :year and (t.wallet.id in :walletIds) "
            + " AND t.createdBy = :userId and(:groupId IS NULL OR t.groupId = :groupId) GROUP BY MONTH(t.ariseDate)")
    List<ReportData> sumAmountByMonth(Integer year, List<Long> walletIds, Long userId, Long groupId);

    @Query("SELECT SUM(t.amount) AS totalAmount, c.name as categoryName, l.fileUrl as categoryImage " +
            " FROM TransactionEntity t " +
            " join CategoryEntity c on t.category.id = c.id " +
            " join CategoryLogoEntity l on c.logoImageID = l.id " +
            " where t.transactionType = :transactionType and ( t.wallet.id in :walletIds) " +
            " and t.ariseDate between :fromDate and :toDate and t.createdBy = :userId and(:groupId IS NULL OR t.groupId = :groupId) " +
            " GROUP BY t.transactionType, c.name, l.fileUrl ")
    List<CategoryDetailReport> getCategoryDetail(TransactionType transactionType, List<Long> walletIds,
                                                 LocalDateTime fromDate, LocalDateTime toDate, Long userId, Long groupId);

    @Query(value = "SELECT SUM(amount) AS amount, DATE(arise_date) AS createdAt " +
            "FROM transactions " +
            "WHERE arise_date BETWEEN :fromDate AND :toDate " +
            "AND transaction_type = :tranType " +
            "AND wallet_id IN :walletIds " +
            "AND category_id IN :categoryIds " +
            "AND created_by = :userId " +
            "AND group_id = :groupId " +
            "GROUP BY DATE(arise_date)", nativeQuery = true)
    List<AnalysisDetail> getDayAnalysisDetailWithGroupId(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("tranType") String tranType,
            @Param("walletIds") List<Long> walletIds,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("userId") Long userId,
            @Param("groupId") Long groupId
    );


    @Query(value = "SELECT SUM(amount) AS amount, DATE(arise_date) AS createdAt " +
            "FROM transactions " +
            "WHERE arise_date BETWEEN :fromDate AND :toDate " +
            "AND transaction_type = :tranType " +
            "AND wallet_id IN :walletIds " +
            "AND category_id IN :categoryIds " +
            "AND created_by = :userId " +
            "AND group_id IS NULL " +
            "GROUP BY DATE(arise_date)", nativeQuery = true)
    List<AnalysisDetail> getDayAnalysisDetailWithoutGroupId(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("tranType") String tranType,
            @Param("walletIds") List<Long> walletIds,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("userId") Long userId
    );

    @Query(value = " select sum(t.amount) as amount, YEAR(t.ariseDate) AS year, MONTH(t.ariseDate) as month from TransactionEntity t where " +
            " t.ariseDate between :fromDate and :toDate and t.transactionType = :tranType " +
            " and (t.wallet.id in :walletIds) " +
            " and (t.category.id in :categoryIds) and t.createdBy = :userId and(:groupId IS NULL OR t.groupId = :groupId)" +
            " group by YEAR(t.ariseDate), MONTH(t.ariseDate) ")
    List<AnalysisMonthDetail> getMonthAnalysisDetail(LocalDateTime fromDate, LocalDateTime toDate, TransactionType tranType,
                                                     List<Long> walletIds, List<Long> categoryIds, Long userId, Long groupId);

    @Query(value = " select sum(t.amount) as amount, YEAR(t.ariseDate) as year from TransactionEntity t where " +
            " t.ariseDate between :fromDate and :toDate and t.transactionType = :tranType " +
            " and ( t.wallet.id in :walletIds) " +
            " and ( t.category.id in :categoryIds) and t.createdBy = :userId and(:groupId IS NULL OR t.groupId = :groupId)" +
            " group by YEAR(t.ariseDate) ")
    List<AnalysisMonthDetail> getYearAnalysisDetail(LocalDateTime fromDate, LocalDateTime toDate, TransactionType tranType,
                                                    List<Long> walletIds, List<Long> categoryIds, Long userId, Long groupId);

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

    @Query(value = " select count(t) from TransactionEntity t where t.category.id in :categoryIds")
    Long countAllByCategoryIn(@Param("categoryIds") Collection<Long> categoryIds);


    @Query(value = "select c.name as categoryName, sum(t.amount) as amount from TransactionEntity t join CategoryEntity c on t.category.id = c.id " +
            " where t.createdBy = :userId and t.transactionType = :type and(:groupId IS NULL OR t.groupId = :groupId)" +
            " group by t.category.id, c.name ")
    List<CategoryReport> getTotalTransactionByCategory(Long userId, TransactionType type, Long groupId);

    @Query(value = " select sum(t.amount) from TransactionEntity t where t.createdBy = :userId and t.transactionType = :type " +
            "and(:groupId IS NULL OR t.groupId = :groupId)")
    BigDecimal getTotal(Long userId, TransactionType type, Long groupId);

    interface CategoryReport {

        String getCategoryName();

        BigDecimal getAmount();

    }

}

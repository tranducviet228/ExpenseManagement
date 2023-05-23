package com.kma.project.expensemanagement.job;

import com.kma.project.expensemanagement.entity.RecurringTransactionEntity;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.mapper.RecurringTransactionMapper;
import com.kma.project.expensemanagement.repository.RecurringTransactionRepository;
import com.kma.project.expensemanagement.repository.TransactionRepository;
import com.kma.project.expensemanagement.utils.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional(readOnly = true)
@Component
public class RecurringTransactionJob {

    @Autowired
    RecurringTransactionRepository recurringTransactionRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    RecurringTransactionMapper recurringTransactionMapper;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void createRecurringTransaction() {
        List<RecurringTransactionEntity> recurringTransactionList = recurringTransactionRepository.getAllValidRecurring(LocalDate.now());
        List<TransactionEntity> transactionEntityList = new ArrayList<>();
        for (RecurringTransactionEntity recurringTransactionEntity : recurringTransactionList) {
            TransactionEntity transactionEntity = null;
            switch (recurringTransactionEntity.getFrequencyType().name()) {
                case EnumUtils.DAILY:
                    transactionEntity = createDailyTransaction(recurringTransactionEntity);
                    break;
                case EnumUtils.WEEKLY:
                    transactionEntity = createWeeklyTransaction(recurringTransactionEntity);
                    break;
                case EnumUtils.MONTHLY:
                    transactionEntity = createMonthlyTransaction(recurringTransactionEntity);
                    break;
                case EnumUtils.QUARTERLY:
                    transactionEntity = createQuarterlyTransaction(recurringTransactionEntity);
                    break;
                case EnumUtils.YEARLY:
                    transactionEntity = createYearlyTransaction(recurringTransactionEntity);
                    break;
                case EnumUtils.WEEKDAY:
                    transactionEntity = createWeekDayTransaction(recurringTransactionEntity);
                    break;
            }
            if (transactionEntity != null) {
                transactionEntityList.add(transactionEntity);
            }
        }
        transactionRepository.saveAll(transactionEntityList);
        recurringTransactionRepository.saveAll(recurringTransactionList);
    }

    private TransactionEntity createDailyTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        if (LocalTime.now().withSecond(0).withNano(0).equals(recurringTransactionEntity.getTime().withSecond(0))) {
            return createTransaction(recurringTransactionEntity);
        }
        return null;
    }

    private TransactionEntity createMonthlyTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        // ngày đầu tiên của tháng sau
        LocalDateTime nextMonthDate = recurringTransactionEntity.getAriseDate().plusMonths(1).withDayOfMonth(1);
        try {
            nextMonthDate = nextMonthDate.withDayOfMonth(recurringTransactionEntity.getAriseDate().getDayOfMonth());
        } catch (DateTimeException dateTimeException) {
            nextMonthDate = nextMonthDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
        }
        return checkDate(recurringTransactionEntity, nextMonthDate);
    }

    private TransactionEntity createYearlyTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        LocalDateTime nextYearDate = recurringTransactionEntity.getAriseDate().plusYears(1);
        return checkDate(recurringTransactionEntity, nextYearDate);

    }

    private TransactionEntity createQuarterlyTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        LocalDateTime nextQuarterDate = recurringTransactionEntity.getAriseDate().plusMonths(3);
        return checkDate(recurringTransactionEntity, nextQuarterDate);
    }

    private TransactionEntity createWeeklyTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        LocalDateTime nextWeekDate = recurringTransactionEntity.getAriseDate().plusDays(7);
        return checkDate(recurringTransactionEntity, nextWeekDate);

    }

    private TransactionEntity createWeekDayTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        List<String> dayOfWeeks = Arrays.asList(recurringTransactionEntity.getDayInWeeks());
        if (LocalTime.now().withSecond(0).withNano(0).equals(recurringTransactionEntity.getTime().withSecond(0))
                && dayOfWeeks.contains(LocalDate.now().getDayOfWeek().toString())) {
            return createTransaction(recurringTransactionEntity);
        }
        return null;

    }

    public TransactionEntity checkDate(RecurringTransactionEntity recurringTransactionEntity, LocalDateTime nextDate) {
        if (LocalTime.now().withSecond(0).withNano(0).equals(recurringTransactionEntity.getTime().withSecond(0))
                && nextDate.toLocalDate().equals(LocalDate.now())) {
            recurringTransactionEntity.setAriseDate(nextDate);
            createTransaction(recurringTransactionEntity);
        }
        return null;
    }

    public TransactionEntity createTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        TransactionEntity transactionEntity = recurringTransactionMapper.convertToTransaction(recurringTransactionEntity);
        transactionEntity.setId(null);
        transactionEntity.setCreatedAt(LocalDateTime.now());
        transactionEntity.setAriseDate(LocalDateTime.now());
        return transactionEntity;
    }

}

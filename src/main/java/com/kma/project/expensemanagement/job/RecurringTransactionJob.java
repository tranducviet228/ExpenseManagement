package com.kma.project.expensemanagement.job;

import com.kma.project.expensemanagement.entity.RecurringTransactionEntity;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.mapper.RecurringTransactionMapper;
import com.kma.project.expensemanagement.repository.RecurringTransactionRepository;
import com.kma.project.expensemanagement.repository.TransactionRepository;
import com.kma.project.expensemanagement.service.ExpenseLimitService;
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

    @Autowired
    ExpenseLimitService expenseLimitService;

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
                case EnumUtils.WEEK:
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
                // update expense limit
                expenseLimitService.updateToLimit(transactionEntity);
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
        return checkDate(recurringTransactionEntity, EnumUtils.MONTHLY);
    }

    private TransactionEntity createYearlyTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        return checkDate(recurringTransactionEntity, EnumUtils.YEARLY);

    }

    private TransactionEntity createQuarterlyTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        return checkDate(recurringTransactionEntity, EnumUtils.QUARTERLY);
    }

    private TransactionEntity createWeeklyTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        return checkDate(recurringTransactionEntity, EnumUtils.WEEKLY);

    }

    private TransactionEntity createWeekDayTransaction(RecurringTransactionEntity recurringTransactionEntity) {
        List<String> dayOfWeeks = Arrays.asList(recurringTransactionEntity.getDayInWeeks());
        if (LocalTime.now().withSecond(0).withNano(0).equals(recurringTransactionEntity.getTime().withSecond(0))
                && dayOfWeeks.contains(LocalDate.now().getDayOfWeek().toString())) {
            return createTransaction(recurringTransactionEntity);
        }
        return null;

    }

    public TransactionEntity checkDate(RecurringTransactionEntity recurringTransactionEntity, String dateType) {
        if (LocalTime.now().withSecond(0).withNano(0).equals(recurringTransactionEntity.getTime().withSecond(0))
                && recurringTransactionEntity.getAriseDate().toLocalDate().equals(LocalDate.now())) {

            TransactionEntity transactionEntity = createTransaction(recurringTransactionEntity);

            LocalDateTime nextDate = null;
            switch (dateType) {
                case EnumUtils.MONTHLY:
                    // ngày đầu tiên của tháng sau
                    nextDate = recurringTransactionEntity.getAriseDate().plusMonths(1).withDayOfMonth(1);
                    try {
                        nextDate = nextDate.withDayOfMonth(recurringTransactionEntity.getAriseDate().getDayOfMonth());
                    } catch (DateTimeException dateTimeException) {
                        nextDate = nextDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
                    }
                    break;
                case EnumUtils.YEARLY:
                    nextDate = recurringTransactionEntity.getAriseDate().plusYears(1);
                    break;
                case EnumUtils.QUARTERLY:
                    nextDate = recurringTransactionEntity.getAriseDate().plusMonths(3);
                    break;
                case EnumUtils.WEEKLY:
                    nextDate = recurringTransactionEntity.getAriseDate().plusDays(7);
                    break;
            }
            recurringTransactionEntity.setAriseDate(nextDate);
            return transactionEntity;
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

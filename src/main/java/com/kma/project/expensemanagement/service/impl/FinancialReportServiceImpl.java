package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.response.TransactionOutputDto;
import com.kma.project.expensemanagement.dto.response.report.*;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.TransactionMapper;
import com.kma.project.expensemanagement.repository.TransactionRepository;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.service.FinancialReportService;
import com.kma.project.expensemanagement.service.WalletService;
import com.kma.project.expensemanagement.utils.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    @Autowired
    TransactionMapper transactionMapper;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    WalletService walletService;

    public static List<LocalDate> getWeekReport() {
        LocalDate date = LocalDate.now();
        String dayOfWeek = String.valueOf(date.getDayOfWeek());
        if (!EnumUtils.MON.equals(dayOfWeek)) {
            date = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        }
        List<LocalDate> list = new ArrayList<>();
        list.add(date);
        for (int i = 1; i < 7; i++) {
            date = date.plusDays(1);
            list.add(date);
        }
        return list;
    }

    @Override
    public FinancialStatementOutputDto financialStatement(Long walletId, String localDate) {

        BigDecimal accountBalance = null;
        if (walletId != null) {
            WalletEntity walletEntity = walletRepository.findById(walletId)
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
            accountBalance = walletEntity.getAccountBalance();
        } else {
            accountBalance = walletService.getInfoAllWallet().getMoneyTotal();
        }

        LocalDate firstDateInMonth = localDate == null ? LocalDate.now().withDayOfMonth(1) : LocalDate.parse(localDate).withDayOfMonth(1);
        LocalDate lastDateInMonth = firstDateInMonth.withDayOfMonth(firstDateInMonth.lengthOfMonth());

        LocalDateTime firstDate = firstDateInMonth.atTime(0, 0, 0);
        LocalDateTime lastDate = lastDateInMonth.atTime(23, 59, 59);

        List<TransactionOutputDto> transactionOutputs = getAllTransaction(walletId, firstDate, lastDate);

        Map<LocalDate, List<TransactionOutputDto>> map = new HashMap<>();
        BigDecimal expenseTotal = BigDecimal.ZERO;
        BigDecimal incomeTotal = BigDecimal.ZERO;

        // set transaction to per day
        for (TransactionOutputDto outputDto : transactionOutputs) {
            List<TransactionOutputDto> listTran;
            if (map.get(outputDto.getAriseDate().toLocalDate()) == null) {
                listTran = new ArrayList<>();
            } else {
                listTran = map.get(outputDto.getAriseDate().toLocalDate());
            }
            listTran.add(outputDto);
            map.put(outputDto.getAriseDate().toLocalDate(), listTran);

            // calculate total income and expense
            if (EnumUtils.EXPENSE.equals(outputDto.getTransactionType())) {
                expenseTotal = expenseTotal.add(outputDto.getAmount());
            } else {
                incomeTotal = incomeTotal.add(outputDto.getAmount());
            }
        }

        return FinancialStatementOutputDto.builder()
                .dayTransactionList(getReportListFromMap(map))
                .expenseTotal(expenseTotal)
                .incomeTotal(incomeTotal)
                .currentBalance(accountBalance)
                .build();
    }

    private List<DayTransactionReportOutputDto> getReportListFromMap(Map<LocalDate, List<TransactionOutputDto>> map) {
        // set data to out put
        List<DayTransactionReportOutputDto> dayTransactionList = new ArrayList<>();
        map.forEach((date, transactionList) -> {
            DayTransactionReportOutputDto dayReport = new DayTransactionReportOutputDto();
            dayReport.setDate(date);
            dayReport.setTransactionOutputs(transactionList);
            BigDecimal amountTotal = BigDecimal.ZERO;
            for (TransactionOutputDto item : transactionList) {
                amountTotal = amountTotal.add(item.getAmount());
            }
            dayReport.setAmountTotal(amountTotal);
            dayTransactionList.add(dayReport);
        });
        return dayTransactionList;
    }

    // get all Transaction in fromDate to toDate
    private List<TransactionOutputDto> getAllTransaction(Long walletId, LocalDateTime firstDate, LocalDateTime lastDate) {
        List<TransactionOutputDto> transactionOutputs = null;
        // get current balance
        if (walletId != null) {
            // get all transaction in month
            transactionOutputs = transactionRepository.findAllTransactionByWalletId(firstDate, lastDate, walletId)
                    .stream().map(entity -> transactionMapper.convertToDto(entity)).collect(Collectors.toList());
        } else {
            // get all transaction in all wallet
            transactionOutputs = transactionRepository.findAllInMonth(firstDate, lastDate)
                    .stream().map(entity -> transactionMapper.convertToDto(entity)).collect(Collectors.toList());
        }
        return transactionOutputs;
    }

    public ExpenseIncomeSituationOutputDto getCurrentReport(Long walletId) {
        LocalDate currentDate = LocalDate.now();
        List<DataReportOutputDto> dataReports = new ArrayList<>();

        // ngày hiện tại
        ExpenseAndIncomeTotalOutputDto dayTotal = getTotalInPeriodTime(currentDate, currentDate, walletId);
        dataReports.add(DataReportOutputDto.builder().expenseTotal(dayTotal.getExpenseTotal())
                .incomeTotal(dayTotal.getIncomeTotal())
                .remainTotal(dayTotal.getIncomeTotal().subtract(dayTotal.getExpenseTotal()))
                .name("Ngày hôm nay").build());

        // tuần hiện tại
        List<LocalDate> dateInWeek = getWeekReport();
        ExpenseAndIncomeTotalOutputDto weekTotal = getTotalInPeriodTime(dateInWeek.get(0), dateInWeek.get(dateInWeek.size() - 1), walletId);
        dataReports.add(DataReportOutputDto.builder().expenseTotal(weekTotal.getExpenseTotal())
                .incomeTotal(weekTotal.getIncomeTotal())
                .remainTotal(weekTotal.getIncomeTotal().subtract(weekTotal.getExpenseTotal()))
                .name("Tuần này").build());

        // tháng hiện tại
        ExpenseAndIncomeTotalOutputDto monthTotal = getTotalInPeriodTime(currentDate.withDayOfMonth(1),
                currentDate.withDayOfMonth(currentDate.lengthOfMonth()), walletId);
        dataReports.add(DataReportOutputDto.builder().expenseTotal(monthTotal.getExpenseTotal())
                .incomeTotal(monthTotal.getIncomeTotal())
                .remainTotal(monthTotal.getIncomeTotal().subtract(monthTotal.getExpenseTotal()))
                .name("Tháng này").build());

        // quý hiện tại
        LocalDate startOfQuarter = currentDate.with(currentDate.getMonth().firstMonthOfQuarter())
                .withDayOfMonth(1);
        LocalDate endOfQuarter = startOfQuarter.plusMonths(3).minusDays(1);
        ExpenseAndIncomeTotalOutputDto quarterTotal = getTotalInPeriodTime(startOfQuarter, endOfQuarter, walletId);
        dataReports.add(DataReportOutputDto.builder().expenseTotal(quarterTotal.getExpenseTotal())
                .incomeTotal(quarterTotal.getIncomeTotal())
                .remainTotal(quarterTotal.getIncomeTotal().subtract(quarterTotal.getExpenseTotal()))
                .name("Quý này").build());

        // năm hiện tại
        ExpenseAndIncomeTotalOutputDto yearTotal = getTotalInPeriodTime(currentDate.withDayOfYear(1),
                currentDate.withDayOfYear(currentDate.lengthOfYear()), walletId);
        dataReports.add(DataReportOutputDto.builder().expenseTotal(yearTotal.getExpenseTotal())
                .incomeTotal(yearTotal.getIncomeTotal())
                .remainTotal(yearTotal.getIncomeTotal().subtract(yearTotal.getExpenseTotal()))
                .name("Năm này").build());

        return ExpenseIncomeSituationOutputDto.builder()
                .data(dataReports)
                .build();
    }

    @Override
    public ExpenseIncomeSituationOutputDto expenseIncomeSituation(String type, Integer year, Integer toYear,
                                                                  Long walletId, String fromTime, String toTime) {
        switch (type) {
            case EnumUtils.CURRENT:
                return getCurrentReport(walletId);

            case EnumUtils.MONTH:
                return getMonthReport(walletId, year);

            case EnumUtils.QUARTER:
                return getQuarterReport(walletId, year);

            case EnumUtils.YEAR:
                return getYearReport(walletId, year, toYear);

            case EnumUtils.CUSTOM:
                return getCustomReport(walletId, LocalDate.parse(fromTime), LocalDate.parse(toTime));

        }
        return null;
    }

    public ExpenseIncomeSituationOutputDto getMonthReport(Long walletId, Integer year) {

        List<DataReportOutputDto> dataReports = new ArrayList<>();
        List<TransactionRepository.ReportData> data = transactionRepository.sumAmountByMonth(year);
        data.forEach(item -> {
            dataReports.add(DataReportOutputDto.builder()
                    .expenseTotal(item.getExpenseTotal())
                    .incomeTotal(item.getIncomeTotal())
                    .remainTotal(item.getIncomeTotal().subtract(item.getExpenseTotal()))
                    .name("Tháng " + item.getName())
                    .build());
        });
        return ExpenseIncomeSituationOutputDto.builder()
                .data(dataReports)
                .build();
    }

    public ExpenseIncomeSituationOutputDto getQuarterReport(Long walletId, Integer year) {
        List<DataReportOutputDto> dataReports = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            // lấy ngày đầu tiên của quý 1
            LocalDate startQuarter = LocalDate.ofYearDay(year, 1).with(IsoFields.QUARTER_OF_YEAR, i);
            LocalDate endQuarter = startQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

            ExpenseAndIncomeTotalOutputDto expenseAndIncomeTotal = getTotalInPeriodTime(startQuarter, endQuarter, walletId);

            dataReports.add(DataReportOutputDto.builder()
                    .expenseTotal(expenseAndIncomeTotal.getExpenseTotal())
                    .incomeTotal(expenseAndIncomeTotal.getIncomeTotal())
                    .remainTotal(expenseAndIncomeTotal.getIncomeTotal().subtract(expenseAndIncomeTotal.getExpenseTotal()))
                    .name("Quý " + i)
                    .build());
        }
        return ExpenseIncomeSituationOutputDto.builder()
                .data(dataReports)
                .build();
    }

    public ExpenseIncomeSituationOutputDto getYearReport(Long walletId, Integer year, Integer toYear) {
        List<DataReportOutputDto> dataReports = new ArrayList<>();

        for (int i = 0; i <= (toYear - year); i++) {
            ExpenseIncomeInYearOutputDto expenseAndIncomeTotal = getTotalInYear(walletId, year + i);
            dataReports.add(DataReportOutputDto.builder()
                    .expenseTotal(expenseAndIncomeTotal.getExpenseTotal())
                    .incomeTotal(expenseAndIncomeTotal.getIncomeTotal())
                    .remainTotal(expenseAndIncomeTotal.getIncomeTotal().subtract(expenseAndIncomeTotal.getExpenseTotal()))
                    .name("Năm " + (year + i))
                    .build());
        }
        return ExpenseIncomeSituationOutputDto.builder()
                .data(dataReports)
                .build();
    }

    public ExpenseIncomeSituationOutputDto getCustomReport(Long walletId, LocalDate fromTime, LocalDate toTime) {
        List<DataReportOutputDto> dataReports = new ArrayList<>();

        ExpenseAndIncomeTotalOutputDto expenseAndIncomeTotal = getTotalInPeriodTime(fromTime, toTime, walletId);

        dataReports.add(DataReportOutputDto.builder()
                .expenseTotal(expenseAndIncomeTotal.getExpenseTotal())
                .incomeTotal(expenseAndIncomeTotal.getIncomeTotal())
                .remainTotal(expenseAndIncomeTotal.getIncomeTotal().subtract(expenseAndIncomeTotal.getExpenseTotal()))
                .build());

        return ExpenseIncomeSituationOutputDto.builder()
                .data(dataReports)
                .build();
    }

    // lấy tổng số tiền trong 1 năm
    public ExpenseIncomeInYearOutputDto getTotalInYear(Long walletId, Integer year) {
        LocalDate fistDateInYear = LocalDate.ofYearDay(year, 1);
        LocalDate lastDateInYear = fistDateInYear.with(TemporalAdjusters.lastDayOfYear());
        ExpenseAndIncomeTotalOutputDto outputDto = getTotalInPeriodTime(fistDateInYear, lastDateInYear, walletId);
        return ExpenseIncomeInYearOutputDto.builder()
                .expenseTotal(outputDto.getExpenseTotal())
                .incomeTotal(outputDto.getIncomeTotal()).build();
    }

    // lấy tổng số tiền trong 1 khoảng thời gian
    public ExpenseAndIncomeTotalOutputDto getTotalInPeriodTime(LocalDate fromDate, LocalDate toDate, Long walletId) {
        LocalDateTime firstDate = fromDate.atTime(0, 0, 0);
        LocalDateTime lastDate = toDate.atTime(23, 59, 59);
        BigDecimal expenseTotal = BigDecimal.ZERO;
        BigDecimal incomeTotal = BigDecimal.ZERO;
        List<TransactionEntity> trans = transactionRepository.findAllTransactionByWalletId(firstDate, lastDate, walletId);
        for (TransactionEntity item : trans) {
            if (EnumUtils.EXPENSE.equals(item.getTransactionType().name())) {
                expenseTotal = expenseTotal.add(item.getAmount());
            } else {
                incomeTotal = incomeTotal.add(item.getAmount());
            }
        }
        return ExpenseAndIncomeTotalOutputDto.builder().expenseTotal(expenseTotal).incomeTotal(incomeTotal).build();
    }
}

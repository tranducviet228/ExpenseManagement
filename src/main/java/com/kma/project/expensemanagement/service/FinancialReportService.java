package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.response.report.*;

import java.util.List;

public interface FinancialReportService {

    // tài chính hiện tại
    FinancialStatementOutputDto financialStatement(Long walletId, String fromDate, String toDate);

    // tình hình thu chi hiện tại
//    CurrentSituationOutputDto currentExpenseIncomeSituation(Long walletId);

    // tình hình thu chi
    ExpenseIncomeSituationOutputDto expenseIncomeSituation(String type, Integer year, Integer toYear,
                                                           List<Long> walletIds, String fromTime, String toTime);

    DetailReportOutputDto getDetailReport(String type, String time, String toTime, String timeType, List<Long> walletIds);

    // phân tích chi tiêu, thu
    ReportStatisticOutputDto expenseIncomeAnalysis(String type, String timeType, String fromTime, String toTime,
                                                   List<Long> categoryIds, List<Long> walletIds);

    List<CategoryReportOutputDto> getCategoryReport(String type);

    WeekReportOutputDto getWeekExpenseReport();

}

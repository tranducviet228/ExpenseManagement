package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.report.*;
import com.kma.project.expensemanagement.enums.ScopeType;

import java.util.List;

public interface FinancialReportService {

    // tài chính hiện tại
    FinancialStatementOutputDto financialStatement(Long walletId, String fromDate, String toDate, Long groupId);

    // tình hình thu chi hiện tại
//    CurrentSituationOutputDto currentExpenseIncomeSituation(Long walletId);

    // tình hình thu chi
    ExpenseIncomeSituationOutputDto expenseIncomeSituation(String type, Integer year, Integer toYear,
                                                           List<Long> walletIds, String fromTime, String toTime, Long groupId);

    DetailReportOutputDto getDetailReport(String type, String time, String toTime, String timeType, List<Long> walletIds, Long groupId);

    // phân tích chi tiêu, thu
    ReportStatisticOutputDto expenseIncomeAnalysis(String type, String timeType, String fromTime, String toTime,
                                                   List<Long> categoryIds, List<Long> walletIds, Long groupId);

    DataResponse<List<CategoryReportOutputDto>> getCategoryReport(String type, Long groupId);

    DataResponse<WeekReportOutputDto> getWeekExpenseReport(Long groupId);

}

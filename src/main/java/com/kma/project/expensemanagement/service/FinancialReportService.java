package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.report.*;
import com.kma.project.expensemanagement.enums.ScopeType;

import java.util.List;

public interface FinancialReportService {

    // tài chính hiện tại
    FinancialStatementOutputDto financialStatement(Long walletId, String fromDate, String toDate, ScopeType scopeType);

    // tình hình thu chi hiện tại
//    CurrentSituationOutputDto currentExpenseIncomeSituation(Long walletId);

    // tình hình thu chi
    ExpenseIncomeSituationOutputDto expenseIncomeSituation(String type, Integer year, Integer toYear,
                                                           List<Long> walletIds, String fromTime, String toTime, ScopeType scopeType);

    DetailReportOutputDto getDetailReport(String type, String time, String toTime, String timeType, List<Long> walletIds, ScopeType scopeType);

    // phân tích chi tiêu, thu
    ReportStatisticOutputDto expenseIncomeAnalysis(String type, String timeType, String fromTime, String toTime,
                                                   List<Long> categoryIds, List<Long> walletIds, ScopeType scopeType);

    DataResponse<List<CategoryReportOutputDto>> getCategoryReport(String type, ScopeType scopeType);

    DataResponse<WeekReportOutputDto> getWeekExpenseReport(ScopeType scopeType);

}

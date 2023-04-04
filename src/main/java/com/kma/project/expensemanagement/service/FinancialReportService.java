package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.response.report.DetailReportOutputDto;
import com.kma.project.expensemanagement.dto.response.report.ExpenseIncomeSituationOutputDto;
import com.kma.project.expensemanagement.dto.response.report.FinancialStatementOutputDto;
import com.kma.project.expensemanagement.dto.response.report.ReportStatisticOutputDto;

import java.util.List;

public interface FinancialReportService {

    // tài chính hiện tại
    FinancialStatementOutputDto financialStatement(Long walletId, String localDate);

    // tình hình thu chi hiện tại
//    CurrentSituationOutputDto currentExpenseIncomeSituation(Long walletId);

    // tình hình thu chi
    ExpenseIncomeSituationOutputDto expenseIncomeSituation(String type, Integer year, Integer toYear,
                                                           Long walletId, String fromTime, String toTime);

    DetailReportOutputDto getDetailReport(String type, String time, String toTime, String timeType, Long walletId);

    // phân tích chi tiêu
    ReportStatisticOutputDto expenseAnalysis(String type, String timeType, String fromTime, String toTime,
                                             List<Long> categoryIds, List<Long> walletIds);
//
//    // phân tích thu
//    void incomeAnalysis();
//
//    // chuyến đi, sự kiện
//    void eventReport();

}

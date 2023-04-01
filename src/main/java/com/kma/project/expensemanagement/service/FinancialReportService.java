package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.response.report.ExpenseIncomeSituationOutputDto;
import com.kma.project.expensemanagement.dto.response.report.FinancialStatementOutputDto;

public interface FinancialReportService {

    // tài chính hiện tại
    FinancialStatementOutputDto financialStatement(Long walletId, String localDate);

    // tình hình thu chi hiện tại
//    CurrentSituationOutputDto currentExpenseIncomeSituation(Long walletId);

    // tình hình thu chi
    ExpenseIncomeSituationOutputDto expenseIncomeSituation(String type, Integer year, Integer toYear,
                                                           Long walletId, String fromTime, String toTime);

//    // phân tích chi tiêu
//    void expenseAnalysis();
//
//    // phân tích thu
//    void incomeAnalysis();
//
//    // chuyến đi, sự kiện
//    void eventReport();

}

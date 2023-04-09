package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.ReportSituationInputDto;
import com.kma.project.expensemanagement.dto.request.ReportStatisticInputDto;
import com.kma.project.expensemanagement.dto.response.report.DetailReportOutputDto;
import com.kma.project.expensemanagement.dto.response.report.ExpenseIncomeSituationOutputDto;
import com.kma.project.expensemanagement.dto.response.report.FinancialStatementOutputDto;
import com.kma.project.expensemanagement.dto.response.report.ReportStatisticOutputDto;
import com.kma.project.expensemanagement.service.FinancialReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/report")
@Api(tags = "Báo cáo")
public class FinancialReportController {

    @Autowired
    FinancialReportService financialReportService;

    @ApiOperation("Tài chính hiện tại")
    @GetMapping("/")
    public FinancialStatementOutputDto getFinancialStatement(Long walletId, String localDate) {
        return financialReportService.financialStatement(walletId, localDate);
    }

//    @ApiOperation("Tình hình thu chi hiện tại")
//    @GetMapping("/current-report")
//    public CurrentSituationOutputDto currentSituation(Long walletId) {
//        return financialReportService.currentExpenseIncomeSituation(walletId);
//    }

    @ApiOperation("Tình hình thu chi")
    @PutMapping("/report")
    public ExpenseIncomeSituationOutputDto expenseIncomeSituation(@RequestBody ReportSituationInputDto inputDto,
                                                                  String type, Integer year, Integer toYear,
                                                                  String fromTime, String toTime) {
        return financialReportService.expenseIncomeSituation(type, year, toYear, inputDto.getWalletIds(), fromTime, toTime);
    }

    @ApiOperation("Chi tiết thu chi theo từng danh mục")
    @PutMapping("/detail-category")
    public DetailReportOutputDto getDetailReport(@RequestBody ReportSituationInputDto inputDto, String type, String time,
                                                 String toTime, String timeType) {
        return financialReportService.getDetailReport(type, time, toTime, timeType, inputDto.getWalletIds());
    }

    @ApiOperation("Phân tích chi tiêu và thu")
    @PutMapping("/statistic")
    public ReportStatisticOutputDto expenseAnalysis(@RequestBody ReportStatisticInputDto inputDto,
                                                    String type, String timeType, String fromTime, String toTime) {
        return financialReportService.expenseIncomeAnalysis(type, timeType, fromTime, toTime, inputDto.getCategoryIds(), inputDto.getWalletIds());
    }


}

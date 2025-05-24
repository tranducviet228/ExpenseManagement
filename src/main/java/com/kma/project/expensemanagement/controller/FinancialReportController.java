package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.ReportSituationInputDto;
import com.kma.project.expensemanagement.dto.request.ReportStatisticInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.report.*;
import com.kma.project.expensemanagement.enums.ScopeType;
import com.kma.project.expensemanagement.service.FinancialReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/report")
@Api(tags = "Báo cáo")
public class FinancialReportController {

    @Autowired
    FinancialReportService financialReportService;

    @ApiOperation("Tài chính hiện tại")
    @GetMapping("/")
    public FinancialStatementOutputDto getFinancialStatement(Long walletId, String fromDate, String toDate, ScopeType scopeType) {
        return financialReportService.financialStatement(walletId, fromDate, toDate, scopeType);
    }

//    @ApiOperation("Tình hình thu chi hiện tại")
//    @GetMapping("/current-report")
//    public CurrentSituationOutputDto currentSituation(Long walletId) {
//        return financialReportService.currentExpenseIncomeSituation(walletId);
//    }

    @ApiOperation("Tình hình thu chi")
    @PutMapping
    public ExpenseIncomeSituationOutputDto expenseIncomeSituation(@RequestBody ReportSituationInputDto inputDto,
                                                                  String type, Integer year, Integer toYear,
                                                                  String fromTime, String toTime, ScopeType scopeType) {
        return financialReportService.expenseIncomeSituation(type, year, toYear, inputDto.getWalletIds(), fromTime, toTime, scopeType);
    }

    @ApiOperation("Chi tiết thu chi theo từng danh mục")
    @PutMapping("/detail-category")
    public DetailReportOutputDto getDetailReport(@RequestBody ReportSituationInputDto inputDto, String type, String time,
                                                 String toTime, String timeType, ScopeType scopeType) {
        return financialReportService.getDetailReport(type, time, toTime, timeType, inputDto.getWalletIds(), scopeType);
    }

    @ApiOperation("Phân tích chi tiêu và thu")
    @PutMapping("/statistic")
    public ReportStatisticOutputDto expenseAnalysis(@RequestBody ReportStatisticInputDto inputDto,
                                                    String type, String timeType, String fromTime, String toTime, ScopeType scopeType) {
        return financialReportService.expenseIncomeAnalysis(type, timeType, fromTime, toTime, inputDto.getCategoryIds(), inputDto.getWalletIds(), scopeType);
    }

    @ApiOperation("Tỉ lệ chi tiêu theo từng danh mục")
    @GetMapping("/category-report")
    public DataResponse<List<CategoryReportOutputDto>> getCategoryReport(String type, ScopeType scopeType) {
        return financialReportService.getCategoryReport(type, scopeType);
    }

    @ApiOperation("Báo cáo chi tuần hiện tại")
    @GetMapping("/week-report")
    public DataResponse<WeekReportOutputDto> getWeekReport(ScopeType scopeType) {
        return financialReportService.getWeekExpenseReport(scopeType);
    }
}

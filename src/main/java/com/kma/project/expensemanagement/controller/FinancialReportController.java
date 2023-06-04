package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.request.ReportSituationInputDto;
import com.kma.project.expensemanagement.dto.request.ReportStatisticInputDto;
import com.kma.project.expensemanagement.dto.response.report.*;
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
    public FinancialStatementOutputDto getFinancialStatement(Long walletId, String fromDate, String toDate) {
        return financialReportService.financialStatement(walletId, fromDate, toDate);
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

    @ApiOperation("Tỉ lệ chi tiêu theo từng danh mục")
    @GetMapping("/category-report")
    public List<CategoryReportOutputDto> getCategoryReport(String type) {
        return financialReportService.getCategoryReport(type);
    }

    @ApiOperation("Báo cáo chi tuần hiện tại")
    @GetMapping("/week-report")
    public WeekReportOutputDto getWeekReport() {
        return financialReportService.getWeekExpenseReport();
    }
}

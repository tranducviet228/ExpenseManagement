package com.kma.project.expensemanagement.dto.response.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialStatementOutputDto {

    private BigDecimal expenseTotal;

    private BigDecimal incomeTotal;

    private BigDecimal currentBalance;

    private List<DayTransactionReportOutputDto> dayTransactionList;

}

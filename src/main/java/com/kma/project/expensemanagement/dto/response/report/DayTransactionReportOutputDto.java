package com.kma.project.expensemanagement.dto.response.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kma.project.expensemanagement.dto.response.TransactionOutputDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DayTransactionReportOutputDto {

    private LocalDate date;

    private BigDecimal amountTotal;

    private List<TransactionOutputDto> transactionOutputs;
}

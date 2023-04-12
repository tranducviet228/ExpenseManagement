package com.kma.project.expensemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class ExpenseLimitInputDto {

    private BigDecimal amount;

    private String limitName;

    private List<String> categoryIds;

    private List<String> walletIds;

    private LocalDate fromDate;

    private LocalDate toDate;

}

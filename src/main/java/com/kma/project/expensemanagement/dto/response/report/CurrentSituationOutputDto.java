package com.kma.project.expensemanagement.dto.response.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentSituationOutputDto {

    private BigDecimal dayExpenseTotal;

    private BigDecimal dayIncomeTotal;

    private BigDecimal weekExpenseTotal;

    private BigDecimal weekIncomeTotal;

    private BigDecimal monthExpenseTotal;

    private BigDecimal monthIncomeTotal;

    private BigDecimal quarterExpenseTotal;

    private BigDecimal quarterIncomeTotal;

    private BigDecimal yearExpenseTotal;

    private BigDecimal yearIncomeTotal;

}

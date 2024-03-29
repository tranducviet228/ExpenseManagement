package com.kma.project.expensemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecurringTransactionInputDto {

    @Min(value = 0, message = "{error.amount-not-valid}")
    private BigDecimal amount;

    private Long categoryId;

    private String description;

    private Long walletId;

    private boolean addToReport;

    @Pattern(regexp = "^(EXPENSE|INCOME)", message = "{error.transaction-type-not-valid}")
    private String transactionType;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String time;

    private String frequencyType;

    private List<String> dayInWeeks;

}

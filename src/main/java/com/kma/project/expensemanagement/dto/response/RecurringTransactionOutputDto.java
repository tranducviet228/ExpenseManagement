package com.kma.project.expensemanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecurringTransactionOutputDto {

    private Long id;

    @Min(value = 0, message = "{error.amount-not-valid}")
    private BigDecimal amount;

    private Long categoryId;

    private String categoryName;

    private String categoryLogo;

    private String description;

    private Long walletId;

    private String walletName;

    private boolean addToReport;

    private String transactionType;

    private LocalDate fromDate;

    private LocalDate toDate;

    private LocalTime time;

    private String frequencyType;

    private List<String> dayInWeeks;

}

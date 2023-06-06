package com.kma.project.expensemanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionExcelOutputDto {

    private Long id;

    private BigDecimal amount;

    private String categoryName;

    private String description;

    private LocalDateTime ariseDate;

    private LocalDate date;

    private LocalTime time;

    private String walletName;

    private String walletType;

    private String transactionType;

    private String transactionTypeName;
}

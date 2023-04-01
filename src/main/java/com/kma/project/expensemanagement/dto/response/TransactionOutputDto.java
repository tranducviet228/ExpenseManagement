package com.kma.project.expensemanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionOutputDto {

    private Long id;

    private BigDecimal amount;

    private Long categoryId;

    private String categoryName;

    private String description;

    private LocalDateTime ariseDate;

    private Long walletId;

    private String walletName;

    private boolean addToReport;

    private String transactionType;

    private String imageUrl;

    private LocalDateTime createdAt;

    private Long createdBy;

}

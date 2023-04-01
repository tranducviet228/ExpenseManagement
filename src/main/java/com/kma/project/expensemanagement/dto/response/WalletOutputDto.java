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
public class WalletOutputDto {

    private Long id;

    private BigDecimal accountBalance;

    private String name;

    private String accountType;

    private String currency;

    private String description;

    private boolean isReport;

    private LocalDateTime createdAt;

    private Long createdBy;

}

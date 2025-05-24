package com.kma.project.expensemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kma.project.expensemanagement.enums.ScopeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletInputDto {

    private BigDecimal accountBalance;

    private String name;

    private String accountType;

    private String currency;

    private String description;

    private boolean isReport;

    private Long groupId;
}

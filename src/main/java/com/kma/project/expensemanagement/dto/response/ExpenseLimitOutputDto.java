package com.kma.project.expensemanagement.dto.response;

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
public class ExpenseLimitOutputDto {

    private Long id;

    private BigDecimal amount;

    private BigDecimal actualAmount;

    private String limitName;

    private List<String> categoryIds;

    private List<WalletOutputDto> walletOutputs;

    private List<String> walletIds;

    private LocalDate fromDate;

    private LocalDate toDate;

}

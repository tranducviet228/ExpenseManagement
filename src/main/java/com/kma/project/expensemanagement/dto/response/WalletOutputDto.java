package com.kma.project.expensemanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletOutputDto {

    private Long id;

    private Long accountBalance;

    private String name;

    private String accountType;

    private String currency;

    private String description;

    private boolean isReport;

}

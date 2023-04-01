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
public class DataReportOutputDto {

    private String name;

    private BigDecimal incomeTotal;

    private BigDecimal expenseTotal;

    private BigDecimal remainTotal;

}

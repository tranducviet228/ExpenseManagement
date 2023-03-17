package com.kma.project.expensemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryInputDto {

    private String name;

    private Long parentId;

    private String description;

    private boolean isPay;

    private Long logoImageID;

    private String categoryType;

}

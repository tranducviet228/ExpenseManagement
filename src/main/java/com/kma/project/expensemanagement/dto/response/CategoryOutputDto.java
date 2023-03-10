package com.kma.project.expensemanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryOutputDto {

    private Long id;

    private String name;

    private Long parentId;

    private String description;

    private boolean isPay;

    private Long logoImageID;

    private String logoImageUrl;

}

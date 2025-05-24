package com.kma.project.expensemanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kma.project.expensemanagement.enums.RoleOfGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMemberOutputDto {

    private Long userId;

    private String username;

    private Long groupId;

    private RoleOfGroup groupRole;

}

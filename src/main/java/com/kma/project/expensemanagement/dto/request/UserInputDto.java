package com.kma.project.expensemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInputDto {

    private String username;

    private String email;

    private String password;

    private String confirmPassword;

    private List<String> roles;

}

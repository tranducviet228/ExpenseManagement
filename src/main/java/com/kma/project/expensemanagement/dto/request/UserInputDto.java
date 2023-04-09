package com.kma.project.expensemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInputDto {

    @Size(min = 3, max = 20, message = "{error.username-not-valid}")
    private String username;

    @Email(message = "{error.email-not-valid}")
    private String email;

    @Size(min = 6, max = 40, message = "{error.password-not-valid}")
    private String password;

    @Size(min = 6, max = 40, message = "{error.password-not-valid}")
    private String confirmPassword;

    private List<String> roles;

    private String fullName;

    private String phone;
}

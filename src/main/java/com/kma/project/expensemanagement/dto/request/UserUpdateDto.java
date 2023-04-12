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
public class UserUpdateDto {

    @Size(min = 3, max = 20, message = "{error.username-not-valid}")
    private String username;

    @Email(message = "{error.email-not-valid}")
    private String email;

    private String password;

    private String confirmPassword;

    private List<String> roles;

    private String fullName;

    private String phone;
}

package com.kma.project.expensemanagement.dto.authen;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank(message = "{error.username-not-null}")
    @Size(min = 3, max = 20, message = "{error.username-not-valid}")
    private String username;

    @NotBlank(message = "{error.email-not-null}")
    @Email(message = "{error.email-not-valid}")
    private String email;

    @NotBlank(message = "{error.password-not-null}")
    @Size(min = 6, max = 40, message = "{error.password-not-valid}")
    private String password;
}

package com.kma.project.expensemanagement.dto.authen;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "{error.email-not-null}")
    private String email;

}

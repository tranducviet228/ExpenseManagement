package com.kma.project.expensemanagement.dto.authen;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OtpRequestDto {

    @NotBlank(message = "{error.otp-not-null}")
    private String otp;

    @NotBlank(message = "{error.email-not-null}")
    private String email;

}

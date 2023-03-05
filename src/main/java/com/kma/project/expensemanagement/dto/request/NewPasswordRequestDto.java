package com.kma.project.expensemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewPasswordRequestDto {

    @NotBlank(message = "{error.password-not-null}")
    @Size(min = 6, max = 40, message = "{error.password-not-valid}")
    private String password;

    @NotBlank(message = "{error.confirm-password-not-null}")
    private String confirmPassword;

    @NotBlank(message = "{error.email-not-null}")
    private String email;

}

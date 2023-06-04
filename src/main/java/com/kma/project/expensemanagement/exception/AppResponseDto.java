package com.kma.project.expensemanagement.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponseDto<T> {

    private Integer httpStatus;
    private List<AppExceptionError> errors;
    private T data;
    private String message;
    private Boolean isDelete;


    public static AppResponseDto fromError(int httpStatus, AppException appException) {
        return AppResponseDto.builder()
                .httpStatus(httpStatus)
                .errors(appException.getErrors())
                .build();
    }

    public static AppResponseDto fromError(AppException appException) {
        return AppResponseDto.builder()
                .httpStatus(appException.getHttpStatus())
                .errors(appException.getErrors())
                .build();
    }

    public static AppResponseDto fromError(int httpStatus, Exception ex) {
        return AppResponseDto.builder()
                .httpStatus(httpStatus)
                .errors(Collections.singletonList(AppExceptionError.builder()
                        .errorCode("error.common.error")
                        .errorMessage(ex.getLocalizedMessage())
                        .build()))
                .build();
    }

    public static AppResponseDto fromError(Exception ex) {
        return AppResponseDto.builder()
                .httpStatus(500)
                .errors(Collections.singletonList(AppExceptionError.builder()
                        .errorCode("error.common.error")
                        .errorMessage(ex.getLocalizedMessage())
                        .build()))
                .build();
    }

    public static <T> AppResponseDto<T> from(T data) {
        AppResponseDto<T> response = new AppResponseDto<>();
        response.setHttpStatus(200);
        response.setData(data);
        return response;
    }
}

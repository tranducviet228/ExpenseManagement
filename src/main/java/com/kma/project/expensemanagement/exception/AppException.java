package com.kma.project.expensemanagement.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class AppException extends RuntimeException implements Serializable {

    @Builder.Default
    private Integer httpStatus = 500;
    @Builder.Default
    private List<String> errorCodes = new ArrayList<>();
    @Builder.Default
    private List<AppExceptionError> errors = new ArrayList<>();

    public AppException(Integer httpStatus, List<String> errorCodes, List<AppExceptionError> errors) {
        super();
        this.httpStatus = httpStatus;
        this.errorCodes = errorCodes;
        this.errors = errors;
    }

    public AppException() {
        super();
        init();
    }

    public AppException(String message) {
        super(message);
        init();
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        init();
    }

    public AppException(Throwable cause) {
        super(cause);
        init();
    }

    protected AppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        init();
    }

    private void init() {
        this.httpStatus = 500;
        this.errorCodes = new ArrayList<>();
        this.errors = new ArrayList<>();
    }
}

package com.kma.project.expensemanagement.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({AppException.class})
    public ResponseEntity<Object> handleAppException(AppException ex, WebRequest request) {
        ex.getErrors().addAll(ex.getErrorCodes().stream()
                .map(errorCode -> AppExceptionError.builder()
                        .errorCode(errorCode)
                        .errorMessage(messageSource.getMessage(errorCode, null, LocaleContextHolder.getLocale()))
                        .build())
                .collect(Collectors.toList()));
        ex.setErrorCodes(null);
        return handleExceptionInternal(ex, AppResponseDto.fromError(ex), new HttpHeaders(),
                HttpStatus.resolve(ex.getHttpStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<AppExceptionError> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(AppExceptionError.builder()
                    .errorCode(error.getCode())
                    .errorMessage(error.getDefaultMessage())
                    .build());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(AppExceptionError.builder()
                    .errorCode(error.getCode())
                    .errorMessage(error.getDefaultMessage())
                    .build());
        }

        return new ResponseEntity<>(AppResponseDto.fromError(HttpStatus.BAD_REQUEST.value(),
                AppException.builder()
                        .errors(errors)
                        .build()),
                headers,
                status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        final ResponseEntity<Object> responseEntity = super.handleExceptionInternal(ex, body, headers, status,
                request);

        if (responseEntity.getBody() != null) {
            return responseEntity;
        }
        return new ResponseEntity<>(AppResponseDto.fromError(status.value(), ex),
                headers,
                status);
    }

//    @ExceptionHandler({AppException.class})
//    public ResponseEntity<Object> handleAppException(AppException ex, WebRequest request) {
//        ex.getErrors().addAll(ex.getErrorCodes().stream()
//                .map(errorCode -> AppExceptionError.builder()
//                        .errorCode(errorCode)
//                        .errorMessage(messageSource.getMessage(errorCode, null, LocaleContextHolder.getLocale()))
//                        .build())
//                .collect(Collectors.toList()));
//        ex.setErrorCodes(null);
//        return handleExceptionInternal(ex, AppResponseDto.fromError(ex), new HttpHeaders(),
//                HttpStatus.resolve(ex.getHttpStatus()), request);
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers,
//                                                                  HttpStatus status,
//                                                                  WebRequest request) {
//        List<AppExceptionError> errors = new ArrayList<>();
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            errors.add(AppExceptionError.builder()
//                    .errorCode(error.getCode())
//                    .errorMessage(error.getDefaultMessage())
//                    .build());
//        }
//        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
//            errors.add(AppExceptionError.builder()
//                    .errorCode(error.getCode())
//                    .errorMessage(error.getDefaultMessage())
//                    .build());
//        }
//
//        return new ResponseEntity<>(AppResponseDto.fromError(HttpStatus.BAD_REQUEST.value(),
//                AppException.builder()
//                        .errors(errors)
//                        .build()),
//                headers,
//                status);
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
//                                                             HttpStatus status, WebRequest request) {
//        final ResponseEntity<Object> responseEntity = super.handleExceptionInternal(ex, body, headers, status,
//                request);
//
//        if (responseEntity.getBody() != null) {
//            return responseEntity;
//        }
//        return new ResponseEntity<>(AppResponseDto.fromError(status.value(), ex),
//                headers,
//                status);
//    }
}

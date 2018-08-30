package com.mytest.nack.error;

import com.mytest.nack.error.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> invalidParamsException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCodes.INVALID_PARAMS,
                getValidationErrorMessage(e));
        log.warn(e.getClass().getName(), e);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> invalidParamsException(ConstraintViolationException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCodes.INVALID_PARAMS,
                getValidationErrorMessage(e));
        log.warn(e.getClass().getName(), e);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ErrorResponse> customException(BaseException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getError(),
                e.getErrorDescription());
        log.info(e.getClass().getName(), e);
        ResponseStatus responseStatus = AnnotationUtils.getAnnotation(e.getClass(), ResponseStatus.class);
        Assert.notNull(responseStatus, "response status must not be null");
        return new ResponseEntity<>(errorResponse, responseStatus.value());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCodes.UNPARSABLED_JSON,
                e.getMessage());
        log.warn(e.getClass().getName(), e);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> unexpectedException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCodes.INTERNAL_SERVER_ERROR,
                "an unexpected error has occurred");
        log.error(e.getClass().getName(), e);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getValidationErrorMessage(MethodArgumentNotValidException e) {
        StringBuilder builder = new StringBuilder();
        BindingResult result = e.getBindingResult();

        for (FieldError fieldError : result.getFieldErrors()) {
            builder.append(fieldError.getDefaultMessage());
            builder.append(" & ");
        }
        for (ObjectError globalError : result.getGlobalErrors()) {
            builder.append(globalError.getDefaultMessage());
            builder.append(" & ");
        }
        removeLastAndSymbol(builder);

        return builder.toString();
    }

    private String getValidationErrorMessage(ConstraintViolationException e) {
        StringBuilder builder = new StringBuilder();
        Set<ConstraintViolation<?>> result = e.getConstraintViolations();

        result.forEach(x -> {
            builder.append(x.getMessage());
            builder.append(" & ");
        });
        removeLastAndSymbol(builder);

        return builder.toString();
    }

    private void removeLastAndSymbol(StringBuilder builder) {
        if (builder.length() >= 3) {
            builder.delete(builder.length() - 3, builder.length());
        }
    }
}

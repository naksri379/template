package com.mytest.nack.error.exception;

public class BaseException extends Exception {
    private final String error;
    private final String errorDescription;

    public BaseException(String error, String errorDescription) {
        this(error, errorDescription, errorDescription);
    }

    public BaseException(String error, String errorDescription, String message) {
        super(message);
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public BaseException(String error, String errorDescription, Exception e) {
        this(error, errorDescription, errorDescription, e);
    }

    public BaseException(String error, String errorDescription, String message, Exception e) {
        super(message, e);
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}

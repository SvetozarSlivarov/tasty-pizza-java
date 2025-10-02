package com.example.exception;

public class BadRequestException extends RuntimeException {
    private final String code;

    public BadRequestException(String code, String message) {
        super(message);
        this.code = code;
    }
    public BadRequestException(String message) {
        super(message);
        this.code = "bad_request";
    }

    public String getCode() {
        return code;
    }
}
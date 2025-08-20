package com.example.client;

public class ApiException extends Exception {
    private final int status;
    private final String code;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
        this.code = null;
    }
    public ApiException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
    public int getStatus() { return status; }
    public String getCode() { return code; }
}
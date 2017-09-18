package com.caseyellow.server.central.exceptions;

/**
 * Created by dango on 8/7/17.
 */
public class ErrorResponse {

    public static final int INTERNAL_ERROR_CODE = 420;

    private int statusCode;
    private String errorMessage;

    public ErrorResponse(String errorMessage) {
        this(0, errorMessage);
    }

    public ErrorResponse(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

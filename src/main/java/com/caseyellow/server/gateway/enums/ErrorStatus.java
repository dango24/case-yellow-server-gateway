package com.caseyellow.server.gateway.enums;

/**
 * Created by dango on 8/7/17.
 */
public enum ErrorStatus {

    APP_NOT_FOUND(4), ANALYZE_FAILED(7);

    private int statusCode;

    ErrorStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

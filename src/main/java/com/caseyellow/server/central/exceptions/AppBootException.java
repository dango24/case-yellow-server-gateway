package com.caseyellow.server.central.exceptions;

public class AppBootException extends InternalException {

    private final static int ERROR_CODE = 1;

    public AppBootException(String message) {
        super(ERROR_CODE, message);
    }
}

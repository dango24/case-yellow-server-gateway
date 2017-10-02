package com.caseyellow.server.central.exceptions;

public class SaveTestException extends InternalException {

    private final static int ERROR_CODE = 1;

    public SaveTestException(String message) {
        super(ERROR_CODE, message);
    }
}

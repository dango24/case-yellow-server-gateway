package com.caseyellow.server.central.exceptions;

public class IORuntimeException extends InternalException {

    private static final int ERROR_CODE = 1;

    public IORuntimeException(String message) {
        super(ERROR_CODE, message);
    }

    public IORuntimeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}

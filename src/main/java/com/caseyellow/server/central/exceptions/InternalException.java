package com.caseyellow.server.central.exceptions;


/**
 * Created by dango on 6/26/17.
 */

public abstract class InternalException extends RuntimeException {

    private int errorCode;

    public InternalException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public InternalException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

package com.caseyellow.server.central.exceptions;

public class ConverterException extends InternalException {

    private final static int ERROR_CODE = 2;

    public ConverterException(String message) {
        super(ERROR_CODE, message);
    }
}

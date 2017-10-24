package com.caseyellow.server.central.exceptions;

public class AnalyzerException extends RuntimeException {

    public AnalyzerException(String message) {
        super(message);
    }

    public AnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }
}

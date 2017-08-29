package com.caseyellow.server.gateway.exceptions;

import com.caseyellow.server.gateway.enums.ErrorStatus;

/**
 * Created by dango on 6/26/17.
 */

public abstract class InternalException extends RuntimeException {

    private ErrorStatus errorStatus;

    public InternalException(ErrorStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
    }

    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }
}

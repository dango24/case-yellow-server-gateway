package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.exceptions.InternalException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final int INTERNAL_ERROR_CODE = 420;
    private static final int ILLEGAL_ARGUMENT_ERROR_CODE = 601;

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponse> handleInternalException(InternalException ex)  {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception ex)  {
        return buildErrorResponse(ILLEGAL_ARGUMENT_ERROR_CODE, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex)  {
        return buildErrorResponse(-1, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(int errorCode, String message) {
        ErrorResponse errorResponse = new ErrorResponse(message);

        if (errorCode > 0) {
            errorResponse.setStatusCode(errorCode);
        }

        return ResponseEntity.status(INTERNAL_ERROR_CODE)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(errorResponse);
    }

    public static class ErrorResponse {

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

}

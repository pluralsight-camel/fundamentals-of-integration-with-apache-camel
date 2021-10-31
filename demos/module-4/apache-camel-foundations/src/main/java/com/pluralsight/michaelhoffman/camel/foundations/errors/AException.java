package com.pluralsight.michaelhoffman.camel.foundations.errors;

/**
 * Represents an exception for bad data
 */
public class AException extends Exception {

    public AException(String message) {
        super(message);
    }

    public AException(String message, Throwable cause) {
        super(message, cause);
    }

    public AException(Throwable cause) {
        super(cause);
    }

    protected AException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

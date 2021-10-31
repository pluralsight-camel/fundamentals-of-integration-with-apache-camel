package com.pluralsight.michaelhoffman.camel.foundations.errors;

/**
 * Represents an exception for bad data
 */
public class BadDataException extends Exception {

    public BadDataException(String message) {
        super(message);
    }

    public BadDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadDataException(Throwable cause) {
        super(cause);
    }

    protected BadDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

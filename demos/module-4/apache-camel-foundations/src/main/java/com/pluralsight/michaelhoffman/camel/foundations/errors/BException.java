package com.pluralsight.michaelhoffman.camel.foundations.errors;

/**
 * Represents an exception for bad data
 */
public class BException extends Exception {

    public BException(String message) {
        super(message);
    }

    public BException(String message, Throwable cause) {
        super(message, cause);
    }

    public BException(Throwable cause) {
        super(cause);
    }

    protected BException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

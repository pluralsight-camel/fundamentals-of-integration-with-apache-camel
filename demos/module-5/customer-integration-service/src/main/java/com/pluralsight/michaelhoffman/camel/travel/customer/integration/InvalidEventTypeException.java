package com.pluralsight.michaelhoffman.camel.travel.customer.integration;

public class InvalidEventTypeException extends Exception {

    public InvalidEventTypeException() {
        super();
    }

    public InvalidEventTypeException(String message) {
        super(message);
    }

    public InvalidEventTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEventTypeException(Throwable cause) {
        super(cause);
    }

    protected InvalidEventTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.pluralsight.michaelhoffman.camel.customer.integration.common;

/**
 * Exception for invalid customer data sent in the file
 */
public class InvalidCustomerAddressException extends Exception {
    public InvalidCustomerAddressException(String message) {
        super(message);
    }

    public InvalidCustomerAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCustomerAddressException(Throwable cause) {
        super(cause);
    }

    protected InvalidCustomerAddressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

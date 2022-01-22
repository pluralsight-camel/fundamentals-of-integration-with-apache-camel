package com.pluralsight.michaelhoffman.camel.fraud.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a customer transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String transactionId;
    private String transactionDateTime;
    private Long orderNumber;
    private String accountName;
    private String contactName;
    private String contactMainPhone;
    private String contactCellPhone;
    private String contactEmailAddress;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingAddressCity;
    private String shippingAddressState;
    private String shippingAddressPostalCode;
    private String shippingAddressCountry;
    private String paymentType;
    private String paymentAccountNumber;
}

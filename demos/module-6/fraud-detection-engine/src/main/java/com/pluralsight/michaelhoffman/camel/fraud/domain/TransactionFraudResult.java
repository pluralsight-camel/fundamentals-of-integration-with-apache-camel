package com.pluralsight.michaelhoffman.camel.fraud.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an order event for create
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFraudResult {
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
    private Integer fraudScore;
}

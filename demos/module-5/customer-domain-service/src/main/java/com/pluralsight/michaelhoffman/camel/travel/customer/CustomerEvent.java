package com.pluralsight.michaelhoffman.camel.travel.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a customer event for create, update and delete
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEvent {
    private int customerId;
    private String eventType;
}

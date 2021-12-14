package com.pluralsight.michaelhoffman.camel.travel.sales.integration;

import lombok.*;

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

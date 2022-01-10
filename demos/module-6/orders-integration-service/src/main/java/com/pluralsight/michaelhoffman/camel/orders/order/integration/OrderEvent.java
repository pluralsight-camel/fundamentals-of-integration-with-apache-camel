package com.pluralsight.michaelhoffman.camel.orders.order.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an order event for create
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private int orderNumber;
    private int itemNumber;
    private int customerNumber;
    private String eventType;
}

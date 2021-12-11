package com.pluralsight.michaelhoffman.camel.travel.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a customer event for create, update and delete
 */
@Getter
@Setter
@AllArgsConstructor
public class CustomerEvent {
    private int customerId;
    private String eventType;
}

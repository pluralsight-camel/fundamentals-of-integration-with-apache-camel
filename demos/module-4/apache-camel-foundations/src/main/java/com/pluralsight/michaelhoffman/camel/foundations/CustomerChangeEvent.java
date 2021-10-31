package com.pluralsight.michaelhoffman.camel.foundations;

import java.time.Instant;

public class CustomerChangeEvent {
    private String correlationId;
    private int customerId;
    private String eventType;
    private Instant eventTime;

    public CustomerChangeEvent(String correlationId, int customerId, String eventType, Instant eventTime) {
        this.correlationId = correlationId;
        this.customerId = customerId;
        this.eventType = eventType;
        this.eventTime = eventTime;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }
}

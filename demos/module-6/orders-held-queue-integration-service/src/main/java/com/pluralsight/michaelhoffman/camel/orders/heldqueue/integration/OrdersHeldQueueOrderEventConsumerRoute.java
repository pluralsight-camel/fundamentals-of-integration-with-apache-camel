package com.pluralsight.michaelhoffman.camel.orders.heldqueue.integration;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Route that consumes order events for the held order queue
 */
@Component
public class OrdersHeldQueueOrderEventConsumerRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(OrdersHeldQueueOrderEventConsumerRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(
            deadLetterChannel(
                "log:com.pluralsight.michaelhoffman.camel.orders.heldqueue.integration?level=ERROR"));

        from("kafka:{{app.kafka.topic}}?brokers={{app.kafka.brokers}}")
            .to("rest:post:orders-held-queue/order?host={{app.orders-held-queue-service.host}}");
    }
}

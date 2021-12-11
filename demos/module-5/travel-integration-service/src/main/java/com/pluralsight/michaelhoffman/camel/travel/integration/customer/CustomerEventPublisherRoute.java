package com.pluralsight.michaelhoffman.camel.travel.integration.customer;

import com.fasterxml.jackson.core.JsonParseException;
import com.pluralsight.michaelhoffman.camel.travel.common.CustomerEvent;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Route that starts with a REST endpoint for publishing the event
 * notification and publishes to the topic
 */
public class CustomerEventPublisherRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(CustomerEventPublisherRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(defaultErrorHandler().log(log));

        onException(InvalidEventTypeException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().constant("Invalid event type sent");

        onException(JsonParseException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().constant("Invalid event type sent");

        /**
         * Publish customer integration events
         */
        rest("customer-integration")
            .post("event")
                .type(CustomerEvent.class)
            .route()
            .choice()
                .when().simple("${body.eventType} =~ 'create'")
                    .setProperty("routingKey", constant("customer.create"))
                    .to("direct:sendEventToRabbitMQ")
                .when().simple("${body.eventType} =~ 'update'")
                    .setProperty("routingKey", constant("customer.update"))
                    .to("direct:sendEventToRabbitMQ")
                .when().simple("${body.eventType} =~ 'delete'")
                    .setProperty("routingKey", constant("customer.delete"))
                    .to("direct:sendEventToRabbitMQ")
                .otherwise()
                    .log(LoggingLevel.ERROR, "Received invalid event type for message: ${body}")
                    .throwException(new InvalidEventTypeException("Event type is invalid"));

        from("direct:sendEventToRabbitMQ")
            .setHeader(RabbitMQConstants.ROUTING_KEY, exchangeProperty("routingKey"))
            .to("rabbitmq:customer" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&autoDelete=false" +
                "&bridgeErrorHandler=true" +
                "&declare=false" +
                "&exchangeType=topic"
            );
    }
}

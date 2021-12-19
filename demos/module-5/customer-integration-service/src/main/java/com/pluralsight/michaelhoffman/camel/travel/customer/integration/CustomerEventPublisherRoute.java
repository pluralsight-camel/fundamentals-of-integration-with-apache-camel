package com.pluralsight.michaelhoffman.camel.travel.customer.integration;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Route that starts with a REST endpoint for publishing the event
 * notification and publishes to the topic
 */
@Component
public class CustomerEventPublisherRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(CustomerEventPublisherRoute.class);

    @Value("${app.integration.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .component("servlet")
            .host(host)
            .port(port)
            .bindingMode(RestBindingMode.json);

        errorHandler(defaultErrorHandler().log(log));

        onException(InvalidEventTypeException.class)
            .handled(true)
            .log(LoggingLevel.ERROR, "An invalid event type was sent")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().constant("Invalid event type sent");

        onException(JsonParseException.class)
            .handled(true)
            .log(LoggingLevel.ERROR, "An exception occurred parsing the request body")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().constant("Json pare exception was thrown");

        /**
         * Publish customer integration events
         */
        rest("/customer-integration")
            .post("/event")
                .type(CustomerEvent.class)
                .consumes("application/json")
            .route()
            .removeHeader(Exchange.HTTP_METHOD)
            .removeHeader(Exchange.HTTP_PATH)
            .removeHeader(Exchange.HTTP_URI)
            .removeHeader(Exchange.HTTP_URL)
            .removeHeader("CamelServletContextPath")
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
                    .throwException(new InvalidEventTypeException("Event type is invalid"));

        from("direct:sendEventToRabbitMQ")
            .setHeader(RabbitMQConstants.ROUTING_KEY, exchangeProperty("routingKey"))
            .marshal()
                .json()
            .to("rabbitmq:customer" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&autoDelete=false" +
                "&bridgeErrorHandler=true" +
                "&declare=false" +
                "&exchangePattern=InOnly" +
                "&exchangeType=topic"
            );
    }
}

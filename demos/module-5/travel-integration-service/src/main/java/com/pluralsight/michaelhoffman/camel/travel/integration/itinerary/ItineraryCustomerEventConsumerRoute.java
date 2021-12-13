package com.pluralsight.michaelhoffman.camel.travel.integration.itinerary;

import com.pluralsight.michaelhoffman.camel.travel.common.CustomerEvent;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Route that consumes customer events for the Itinerary domain
 */
@Component
public class ItineraryCustomerEventConsumerRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(ItineraryCustomerEventConsumerRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(
            deadLetterChannel(
                "log:com.pluralsight.michaelhoffman.camel.travel.integration.itinerary?level=ERROR"));

        from("rabbitmq:customer" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=topic" +
            "&passive=true" +
            "&queue=itinerary_customer"
        )
            .unmarshal()
                .json(CustomerEvent.class)
            .log(LoggingLevel.DEBUG, "Received customer event: ${body}")
            .choice()
                .when(header(RabbitMQConstants.ROUTING_KEY).isEqualToIgnoreCase("customer.delete"))
                    .log(LoggingLevel.DEBUG, "Processing customer deletion event")
                    .to("direct:postToItineraryEndpoint")
                .otherwise()
                    .log(LoggingLevel.DEBUG, "Received customer event type to ignore")
                    .stop();

        from("direct:postToItineraryEndpoint")
            .marshal()
                .json()
            .log(LoggingLevel.DEBUG, "Sending event to itinerary endpoint: ${body}")
            .to("rest:post:itinerary/customer?host={{app.itinerary-service.host}}");
    }
}

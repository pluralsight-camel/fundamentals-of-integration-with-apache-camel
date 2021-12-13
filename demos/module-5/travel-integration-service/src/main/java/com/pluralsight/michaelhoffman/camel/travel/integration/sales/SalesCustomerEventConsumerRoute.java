package com.pluralsight.michaelhoffman.camel.travel.integration.sales;

import com.pluralsight.michaelhoffman.camel.travel.common.CustomerEvent;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Route that consumes customer events for the Sales domain
 */
@Component
public class SalesCustomerEventConsumerRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(SalesCustomerEventConsumerRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(
            deadLetterChannel(
                "log:com.pluralsight.michaelhoffman.camel.travel.integration.sales?level=ERROR"));

        from("rabbitmq:customer" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=topic" +
            "&passive=true" +
            "&queue=sales_customer"
        )
            .unmarshal()
                .json(CustomerEvent.class)
            .log(LoggingLevel.DEBUG, "Received customer event: ${body}")
            .choice()
                .when(header(RabbitMQConstants.ROUTING_KEY).isEqualToIgnoreCase("customer.create"))
                    .log(LoggingLevel.DEBUG, "Processing customer create event")
                    .to("direct:postToSalesEndpoint")
                .when(header(RabbitMQConstants.ROUTING_KEY).isEqualToIgnoreCase("customer.delete"))
                    .log(LoggingLevel.DEBUG, "Processing customer delete event")
                    .to("direct:postToSalesEndpoint")
                .otherwise()
                    .log(LoggingLevel.DEBUG, "Received customer event type to ignore")
                    .stop();

        from("direct:postToSalesEndpoint")
            .marshal()
                .json()
            .log(LoggingLevel.DEBUG, "Sending event to sales endpoint: ${body}")
            .to("rest:post:sales/customer?host={{app.sales-service.host}}");
    }
}

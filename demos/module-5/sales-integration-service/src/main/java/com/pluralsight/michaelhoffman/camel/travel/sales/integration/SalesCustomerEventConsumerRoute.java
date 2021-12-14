package com.pluralsight.michaelhoffman.camel.travel.sales.integration;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                "log:com.pluralsight.michaelhoffman.camel.travel.sales.integration?level=ERROR"));

        from("rabbitmq:customer" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=topic" +
            "&passive=true" +
            "&queue=sales_customer"
        )
            .choice()
                .when(header(RabbitMQConstants.ROUTING_KEY).isEqualToIgnoreCase("customer.create"))
                    .to("direct:postToSalesEndpoint")
                .when(header(RabbitMQConstants.ROUTING_KEY).isEqualToIgnoreCase("customer.delete"))
                    .to("direct:postToSalesEndpoint")
                .otherwise()
                    .stop()
            .endChoice();

        from("direct:postToSalesEndpoint")
            .to("rest:post:sales/customer?host={{app.sales-service.host}}");
    }
}

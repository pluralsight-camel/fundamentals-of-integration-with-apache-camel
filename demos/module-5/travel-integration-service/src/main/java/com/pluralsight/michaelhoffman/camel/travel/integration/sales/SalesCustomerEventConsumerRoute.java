package com.pluralsight.michaelhoffman.camel.travel.integration.sales;

import com.pluralsight.michaelhoffman.camel.travel.common.CustomerEvent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Route that consumes customer events for the Sales domain
 */
public class SalesCustomerEventConsumerRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(SalesCustomerEventConsumerRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(defaultErrorHandler().log(log));

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
            .filter(header(RabbitMQConstants.ROUTING_KEY).isEqualToIgnoreCase("customer.update"))
                .end()
            .marshal()
                .json()
            .to("rest:post:sales/customer?host={{app.sales-service.host}}");
    }
}

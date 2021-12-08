package com.pluralsight.michaelhoffman.camel.travel.integration;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleRabbitMQRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(
            SimpleRabbitMQRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(defaultErrorHandler().log(log));

        // Publisher
        from("direct:simpleStart")
            .to("rabbitmq:travel.simple" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&bridgeErrorHandler=true" +
                "&exchangeType=topic" +
                "&autoDelete=false");

        // Subscriber
        from("rabbitmq:travel.simple" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&bridgeErrorHandler=true" +
            "&exchangeType=topic" +
            "&autoDelete=false")
            .to("rest:post:simple?host={{app.simple-service.host}}");
    }
}

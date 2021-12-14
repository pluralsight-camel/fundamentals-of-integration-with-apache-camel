package com.pluralsight.michaelhoffman.camel.simple.integration;

import org.apache.camel.LoggingLevel;
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

        /**
         * Publisher to durable exchange: simple
         * connectionFactory = Custom connection factory
         * autoDelete = Set as false to prevent the exchange from being deleted on teardown
         * bridgeErrorHandler = Bridge the default error handler
         * declare = Set as false to prevent Camel from declaring the exchange on the server
         * exchangeType = Set as topic to reflect the exchange type on the server
         */
        from("direct:simpleStart")
            .to("rabbitmq:simple" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&autoDelete=false" +
                "&bridgeErrorHandler=true" +
                "&declare=false" +
                "&exchangeType=topic"
            );

        /**
         * Subscriber to queue A with routing key of simple.a
         * connectionFactory = Custom connection factory
         * autoDelete = Set as false to prevent the exchange from being deleted on teardown
         * bridgeErrorHandler = Bridge the default error handler
         * declare = Set as false to prevent Camel from declaring the exchange on the server
         * exchangeType = Set as topic to reflect the exchange type on the server
         * passive = Set as true to assure the queue is available
         * queue = Queue to receive messages from, in this case simple_a bound to routing key simple.a
         */
        from("rabbitmq:simple" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=topic" +
            "&passive=true" +
            "&queue=simple_a"
        )
            .log(LoggingLevel.ERROR, "Queue A: ${body}")
            .to("rest:post:simple?host={{app.simple-service.host}}");

        /**
         * Subscriber to queue B with routing key of simple.b
         * connectionFactory = Custom connection factory
         * autoDelete = Set as false to prevent the exchange from being deleted on teardown
         * bridgeErrorHandler = Bridge the default error handler
         * declare = Set as false to prevent Camel from declaring the exchange on the server
         * exchangeType = Set as topic to reflect the exchange type on the server
         * passive = Set as true to assure the queue is available
         * queue = Queue to receive messages from, in this case simple_b bound to routing key simple.b
         */
        from("rabbitmq:simple" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=topic" +
            "&passive=true" +
            "&queue=simple_b"
        )
            .log(LoggingLevel.ERROR, "Queue B: ${body}")
            .to("rest:post:simple?host={{app.simple-service.host}}");

        /**
         * Subscriber to Alternate Exchange that catches any message that doesn't match a routing key
         * connectionFactory = Custom connection factory
         * autoDelete = Set as false to prevent the exchange from being deleted on teardown
         * bridgeErrorHandler = Bridge the default error handler
         * declare = Set as false to prevent Camel from declaring the exchange on the server
         * exchangeType = Set as fanout to reflect the exchange type on the server
         * passive = Set as true to assure the queue is available
         * queue = Queue to receive messages from, in this case simple_nomatch is unbound
         */
        from("rabbitmq:simple_nomatch" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=fanout" +
            "&passive=true" +
            "&queue=simple_nomatch"
        )
            .log(LoggingLevel.ERROR, "Queue No Match: ${body}")
            .to("rest:post:simple?host={{app.simple-service.host}}");

    }
}

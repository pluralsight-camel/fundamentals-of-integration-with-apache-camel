package com.pluralsight.michaelhoffman.camel.simple.integration;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleDeadLetterQueueRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(
            SimpleDeadLetterQueueRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel(
            "rabbitmq:simple.deadletter" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&autoDelete=false" +
                "&bridgeErrorHandler=true" +
                "&declare=false" +
                "&exchangeType=direct"
        ));

        from("direct:simpleDLQStart")
            .to("rabbitmq:simple.direct" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&autoDelete=false" +
                "&bridgeErrorHandler=true" +
                "&declare=false" +
                "&exchangeType=direct"
            );

        from("rabbitmq:simple.direct" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=direct" +
            "&passive=true" +
            "&queue=simple.direct"
        )
            .log(LoggingLevel.ERROR, "Read message: ${body}")
            .throwException(Exception.class, "Service failed!");

        from("rabbitmq:simple.deadletter" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=direct" +
            "&passive=true" +
            "&queue=simple.deadletter"
        )
            .log(LoggingLevel.ERROR, "DLQ: ${body}")
            .to("rest:post:simple?host={{app.simple-service.host}}");

    }
}

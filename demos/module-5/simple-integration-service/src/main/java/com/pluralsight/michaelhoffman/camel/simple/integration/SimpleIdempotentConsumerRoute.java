package com.pluralsight.michaelhoffman.camel.simple.integration;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleIdempotentConsumerRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(
            SimpleIdempotentConsumerRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(defaultErrorHandler().log(log));

        from("direct:simpleIdempotentConsumerStart")
            .to("rabbitmq:simple.idempotent" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&autoDelete=false" +
                "&bridgeErrorHandler=true" +
                "&declare=false" +
                "&exchangeType=direct"
            );

        from("rabbitmq:simple.idempotent" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=direct" +
            "&passive=true" +
            "&queue=simple.idempotent"
        )
            .idempotentConsumer(
                header("CorrelationID"),
                MemoryIdempotentRepository.memoryIdempotentRepository())
            .log(LoggingLevel.ERROR, "Read message: ${body}")
            .to("rest:post:simple?host={{app.simple-service.host}}");
    }
}

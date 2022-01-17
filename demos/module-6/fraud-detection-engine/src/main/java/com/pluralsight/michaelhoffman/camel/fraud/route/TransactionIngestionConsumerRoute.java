package com.pluralsight.michaelhoffman.camel.fraud.route;

import com.pluralsight.michaelhoffman.camel.fraud.processor.FraudDetectionProcessor;
import com.pluralsight.michaelhoffman.camel.fraud.processor.TransactionLineToTransactionEventMapper;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Route that consumes order events for the held order queue
 */
@Component
public class TransactionIngestionConsumerRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(TransactionIngestionConsumerRoute.class);

    private CsvDataFormat csvDataFormatTransaction;
    private JacksonDataFormat jsonFormatTransactionEvent;
    private FraudDetectionProcessor fraudDetectionProcessor;

    public TransactionIngestionConsumerRoute(
        @Qualifier("csvDataFormatTransaction")
            CsvDataFormat csvDataFormatTransaction,
        @Qualifier("jsonFormatTransactionEvent")
            JacksonDataFormat jsonFormatTransactionEvent,
        FraudDetectionProcessor fraudDetectionProcessor) {
        this.csvDataFormatTransaction = csvDataFormatTransaction;
        this.jsonFormatTransactionEvent = jsonFormatTransactionEvent;
        this.fraudDetectionProcessor = fraudDetectionProcessor;
    }

    @Override
    public void configure() throws Exception {
        errorHandler(defaultErrorHandler().log(log));

        from("kafka:{{app.kafka.topic}}" +
            "?brokers={{app.kafka.brokers}}" +
            "&bridgeErrorHandler=true" +
            "&clientId={{app.kafka.consumer.clientId}}" +
            "&consumersCount={{app.kafka.consumersCount}}" +
            "&groupId={{app.kafka.consumerGroupId}}")
            .routeId("transaction-ingestion-consumer-route")
            .unmarshal(jsonFormatTransactionEvent)
            .bean(FraudDetectionProcessor.class, "process")
            .log(LoggingLevel.DEBUG, "Received: ${body}");

    }
}

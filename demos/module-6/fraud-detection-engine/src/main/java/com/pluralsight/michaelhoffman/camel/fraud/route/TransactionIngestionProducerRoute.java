package com.pluralsight.michaelhoffman.camel.fraud.route;

import com.pluralsight.michaelhoffman.camel.fraud.processor.TransactionLineToTransactionEventMapper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Route that consumes order events for the held order queue
 */
@Component
public class TransactionIngestionProducerRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(TransactionIngestionProducerRoute.class);

    private CsvDataFormat csvDataFormatTransaction;

    public TransactionIngestionProducerRoute(
        @Qualifier("csvDataFormatTransaction")
            CsvDataFormat csvDataFormatTransaction) {
        this.csvDataFormatTransaction = csvDataFormatTransaction;
    }

    @Override
    public void configure() throws Exception {
        errorHandler(defaultErrorHandler().log(log));

        from("file:{{app.transactionProducerRoute.directory}}" +
            "?include={{app.transactionProducerRoute.includeFile}}" +
            "&move={{app.transactionProducerRoute.moveDirectory}}")
            .routeId("transaction-ingestion-producer-route")
            .unmarshal(csvDataFormatTransaction)
            .split(body())
            .bean(TransactionLineToTransactionEventMapper.class, "process")
            .setProperty("accountName", simple("${body.accountName}"))
            .marshal().json()
            .toD("kafka:{{app.kafka.topic}}" +
                "?brokers={{app.kafka.brokers}}" +
                "&clientId=fraud-engine-transaction-ingestion-route-producer" +
                "&key=${exchangeProperty.accountName}" +
                "&partitioner=com.pluralsight.michaelhoffman.camel.fraud.partitioner.LargeCustomerPartitioner");
    }
}

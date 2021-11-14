package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute;

import com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.processor.AddressUpdateLineToCustomerMapper;
import com.pluralsight.michaelhoffman.camel.customer.integration.common.InvalidCustomerAddressException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.management.PublishEventNotifier;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Camel Route
 *
 * Given I'm using Spring, Camel will automatically configure and execute
 * this route if its configured as a Spring bean. The route polls for files
 * in a shared directory and then processes the contents by calling a REST
 * endpoint.
 */
@Component
public class AddressUpdatesToCustomerServiceRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(
            AddressUpdatesToCustomerServiceRoute.class);

    private CsvDataFormat csvDataFormatAddressUpdate;

    public AddressUpdatesToCustomerServiceRoute(
        @Qualifier("csvDataFormatAddressUpdate")
            CsvDataFormat csvDataFormatAddressUpdate) {
        this.csvDataFormatAddressUpdate =
            csvDataFormatAddressUpdate;
    }

    @Override
    public void configure() throws Exception {
        PublishEventNotifier notifier = new PublishEventNotifier();
        notifier.setCamelContext(getContext());
        notifier.setEndpointUri("direct:event");
        notifier.setIgnoreCamelContextEvents(true);
        getContext().getManagementStrategy().addEventNotifier(notifier);

        errorHandler(defaultErrorHandler().log(log));

        // Thrown exception from the File component. To stop constant
        // errors from polling, one strategy is to stop the route and
        // send a notification. If the problem is fixed, say by the
        // directory being created, then the route can be started again
        // by restarting the JVM or by calling Camel management
        // extensions.
        onException(GenericFileOperationFailedException.class)
            .handled(true)
            .log(LoggingLevel.ERROR,
                "File component failed due to error: ${exception.message}")
            .doTry()
                .process(exchange ->
                    exchange.getContext().getRouteController()
                        .stopRoute("address-updates-to-customer-service-route"))
            .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Could not stop route")
            .end();

        // This is just an example of calling a specific exception that can be
        // thrown by mapping data
        onException(InvalidCustomerAddressException.class)
            .handled(true)
            .log(LoggingLevel.ERROR,
                "File had invalid row: ${exception.message}")
            .end();

        onException(HttpOperationFailedException.class, SocketTimeoutException.class)
            .handled(true)
            .log(LoggingLevel.ERROR,
                "Failed to patch: ${exception.message}")
            .maximumRedeliveries(2)
            .redeliveryDelay(5000)
            .logExhausted(true)
            .logExhaustedMessageHistory(true)
            .logRetryAttempted(true)
            .end();

        // From definition - immediate polling of the shared directory for any
        // files that match the include pattern. Once a file is processed, it's
        // archived to the directory specified by the move option.
        from("file:{{app.addressToCustomerRoute.directory}}" +
                "?include={{app.addressToCustomerRoute.includeFile}}" +
                "&move={{app.addressToCustomerRoute.moveDirectory}}" +
                "&autoCreate=false" +
                "&directoryMustExist=true" +
                "&bridgeErrorHandler=true")
            .onCompletion()
                .to("slack:?webhookUrl=" +
                    "https://hooks.slack.com/services/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")
                .end()
            // ID of the route
            .routeId("address-updates-to-customer-service-route")
            // Unmarshals from the GenericFile type input by the from definition
            // into a list of rows
            .unmarshal(csvDataFormatAddressUpdate)
            // Splitter will split the list of rows from the file into individual
            // messages for execution. The body() is a way to reference the exchange
            // body's input.
            .split(body())
            // Executes each element asynchronously using a default thread pool executor
            .parallelProcessing()
            // Bean definition to execute the mapper's validate method, passing the
            // body as a list of strings representing the row that was previously split and
            // throwing an InvalidCustomerAddressException if invalid
            .bean(AddressUpdateLineToCustomerMapper.class, "validate")
            // Bean definition to execute the mapper's process method, passing the body
            // as a list of strings representing the row that was previously split
            .bean(AddressUpdateLineToCustomerMapper.class, "process")
            // Exchange property sourced by using Simple Expression to get the customer
            // ID off the exchange input message's body
            .setProperty("customerId", simple("${body.id}"))
            // Marshal to JSON using the Jackson library
            .marshal().json()
            // Route to a dynamic to definition with the path including the
            // exchange property previously set.
            .toD(
                "rest:patch:customer/${exchangeProperty.customerId}" +
                    "?host={{app.customer-service.host}}");

        from("direct:event")
            .log(LoggingLevel.ERROR, "EVENT: ${body}");
    }
}

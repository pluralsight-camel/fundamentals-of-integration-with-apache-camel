package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.notifier;

import org.apache.camel.component.file.GenericFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Bean for various notifications. Notifier currently sends messages
 * to a log; however, this approach could also be used for sending notifications
 * to other channels, such as an email address, Slack Channel or
 * Microsoft Teams Channel.
 */
@Component
public class AddressUpdatesToCustomerServiceNotifier {

    private static final Logger log =
        LoggerFactory.getLogger(AddressUpdatesToCustomerServiceNotifier.class);

    public void notifyFileReceived(GenericFile inputFile,
        String exchangeId, String routeId) {
        log.info(new StringBuilder()
            .append("Route: [")
            .append(routeId)
            .append("] with Exchange ID: [")
            .append(exchangeId)
            .append("] processing a new file with the name [")
            .append(inputFile.getFileName())
            .append("] at: ")
            .append(LocalDateTime.now())
            .toString());
    }

    public void notifyProcessingCount(List inputFileRows,
        String exchangeId, String routeId) {
        log.info(new StringBuilder()
            .append("Route: [")
            .append(routeId)
            .append("] with Exchange ID: [")
            .append(exchangeId)
            .append("] processing [")
            .append(inputFileRows.size())
            .append("] records at: ")
            .append(LocalDateTime.now())
            .toString());
    }

    public void notifyProcessingCompleted(
        String exchangeId, String routeId) {
        log.info(new StringBuilder()
            .append("Route: [")
            .append(routeId)
            .append("] with Exchange ID: [")
            .append(exchangeId)
            .append("] completed processing at: ")
            .append(LocalDateTime.now())
            .toString());
    }

    public void notifyProcessingException(
        String exception,
        String exchangeId, String routeId
        ) {
        log.info(new StringBuilder()
            .append("Route: [")
            .append(routeId)
            .append("] with Exchange ID: [")
            .append(exchangeId)
            .append("] failed to complete processing due to: [")
            .append(exception)
            .append("] at: ")
            .append(LocalDateTime.now())
            .toString());
    }

}

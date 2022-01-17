package com.pluralsight.michaelhoffman.camel.fraud.processor;

import com.pluralsight.michaelhoffman.camel.fraud.event.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class FraudDetectionProcessor {

    private static final Logger log =
        LoggerFactory.getLogger(FraudDetectionProcessor.class);

    public void process(TransactionEvent transactionEvent) {
        log.error("Transaction ID: " + transactionEvent.getTransactionId() +
            "; Transaction Time: " + transactionEvent.getTransactionDateTime() +
            "; Order Number: " + transactionEvent.getOrderNumber() +
            "; Account Name" + transactionEvent.getAccountName() +
            "; Contact Name: " + transactionEvent.getContactName());
    }
}

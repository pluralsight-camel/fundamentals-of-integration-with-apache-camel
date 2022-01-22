package com.pluralsight.michaelhoffman.camel.fraud.processor;

import com.pluralsight.michaelhoffman.camel.fraud.domain.TransactionFraudResult;
import com.pluralsight.michaelhoffman.camel.fraud.event.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A simplified transaction fraud processor. In a real world example, there would be
 * many of these processors, looking at multiple fraud indicators to arrive a score.
 */
@Component
public class FraudDetectionProcessor {

    private static final Logger log =
        LoggerFactory.getLogger(FraudDetectionProcessor.class);

    public TransactionFraudResult process(TransactionEvent transactionEvent) {
        TransactionFraudResult fraudResult = new TransactionFraudResult();
        fraudResult.setTransactionId(transactionEvent.getTransactionId());
        fraudResult.setTransactionDateTime(transactionEvent.getTransactionDateTime());
        fraudResult.setOrderNumber(transactionEvent.getOrderNumber());
        fraudResult.setAccountName(transactionEvent.getAccountName());
        fraudResult.setContactName(transactionEvent.getContactName());
        fraudResult.setContactMainPhone(transactionEvent.getContactMainPhone());
        fraudResult.setContactCellPhone(transactionEvent.getContactCellPhone());
        fraudResult.setContactEmailAddress(transactionEvent.getContactEmailAddress());
        fraudResult.setShippingAddressLine1(transactionEvent.getShippingAddressLine1());
        fraudResult.setShippingAddressLine2(transactionEvent.getShippingAddressLine2());
        fraudResult.setShippingAddressCity(transactionEvent.getShippingAddressCity());
        fraudResult.setShippingAddressState(transactionEvent.getShippingAddressState());
        fraudResult.setShippingAddressPostalCode(transactionEvent.getShippingAddressPostalCode());
        fraudResult.setShippingAddressCountry(transactionEvent.getShippingAddressCountry());
        fraudResult.setPaymentType(transactionEvent.getPaymentType());
        fraudResult.setPaymentAccountNumber(transactionEvent.getPaymentAccountNumber());
        fraudResult.setFraudScore(Integer.valueOf(0));

        if ("VISA".equals(transactionEvent.getPaymentType()) &&
            "4888084029651820".equals(transactionEvent.getPaymentAccountNumber())) {
            fraudResult.setFraudScore(Integer.valueOf(100));
        }

        return fraudResult;
    }
}

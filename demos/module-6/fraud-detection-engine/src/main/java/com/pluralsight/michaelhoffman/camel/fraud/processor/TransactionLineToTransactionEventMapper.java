package com.pluralsight.michaelhoffman.camel.fraud.processor;

import com.pluralsight.michaelhoffman.camel.fraud.event.TransactionEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TransactionLineToTransactionEventMapper {

    public TransactionEvent process(List<String> transactionRow) throws Exception {
        TransactionEvent transactionEvent = new TransactionEvent();
        transactionEvent.setTransactionId(transactionRow.get(0));
        transactionEvent.setTransactionDateTime(transactionRow.get(1));
        transactionEvent.setOrderNumber(Long.valueOf(transactionRow.get(2)));
        transactionEvent.setAccountName(transactionRow.get(3));
        transactionEvent.setContactName(transactionRow.get(4));
        transactionEvent.setContactMainPhone(transactionRow.get(5));
        transactionEvent.setContactCellPhone(transactionRow.get(6));
        transactionEvent.setContactEmailAddress(transactionRow.get(7));
        transactionEvent.setShippingAddressLine1(transactionRow.get(8));
        transactionEvent.setShippingAddressLine2(transactionRow.get(9));
        transactionEvent.setShippingAddressCity(transactionRow.get(10));
        transactionEvent.setShippingAddressState(transactionRow.get(11));
        transactionEvent.setShippingAddressPostalCode(transactionRow.get(12));
        transactionEvent.setShippingAddressCountry(transactionRow.get(13));
        transactionEvent.setPaymentType(transactionRow.get(14));
        transactionEvent.setPaymentAccountNumber(transactionRow.get(15));
        return transactionEvent;
    }
}

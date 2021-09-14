package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.processor;

import com.pluralsight.michaelhoffman.camel.customer.integration.common.dto.Customer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper class from a row of customer data into a Customer data object. The
 * Camel route will match the list type from the exchange and use it as the
 * input to the process method. The return value from the process method
 * then becomes the new body of the exchange.
 */
@Component
public class AddressUpdateLineToCustomerMapper {

    public Customer process(List<String> addressLineRow) throws Exception {
        Customer customer = new Customer();
        customer.setId(Integer.parseInt(addressLineRow.get(0)));
        customer.setAddressLine1(addressLineRow.get(1));
        customer.setAddressLine2(addressLineRow.get(2));
        customer.setCity(addressLineRow.get(3));
        customer.setState(addressLineRow.get(4));
        customer.setPostalCode(addressLineRow.get(5));
        return customer;
    }

}

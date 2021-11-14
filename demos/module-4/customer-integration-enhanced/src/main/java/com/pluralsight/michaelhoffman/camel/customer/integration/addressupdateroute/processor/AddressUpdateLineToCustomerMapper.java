package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.processor;

import com.pluralsight.michaelhoffman.camel.customer.integration.common.InvalidCustomerAddressException;
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

    public void validate(List<String> addressLineRow) throws InvalidCustomerAddressException {
        // Make sure the row has the valid number of tokens
        if (addressLineRow == null || addressLineRow.size() != 6) {
            throw new InvalidCustomerAddressException(
                "Invalid row, must have 6 columns of data");
        }

        if (addressLineRow.get(0) == null || addressLineRow.get(0).isBlank()) {
            throw new InvalidCustomerAddressException(
                "Invalid ID, required field");
        }

        try {
            Integer.parseInt(addressLineRow.get(0));
        } catch (NumberFormatException e) {
            throw new InvalidCustomerAddressException(
                "Invalid ID, must be a number");
        }

        if (addressLineRow.get(1) == null || addressLineRow.get(1).isBlank()) {
            throw new InvalidCustomerAddressException(
                "Invalid Address Line 1, required field");
        }

        if (addressLineRow.get(3) == null || addressLineRow.get(3).isBlank()) {
            throw new InvalidCustomerAddressException(
                "Invalid City, required field");
        }

        if (addressLineRow.get(4) == null || addressLineRow.get(4).isBlank()) {
            throw new InvalidCustomerAddressException(
                "Invalid State, required field");
        }

        if (addressLineRow.get(5) == null || addressLineRow.get(5).isBlank()) {
            throw new InvalidCustomerAddressException(
                "Invalid Postal Code, required field");
        }
    }

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

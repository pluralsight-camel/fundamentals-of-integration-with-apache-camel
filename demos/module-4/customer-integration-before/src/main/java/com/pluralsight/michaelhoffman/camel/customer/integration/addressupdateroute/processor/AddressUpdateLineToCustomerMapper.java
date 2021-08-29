package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.processor;

import com.pluralsight.michaelhoffman.camel.customer.integration.common.dto.Customer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring Bean used for mapping from the row of a processed CSV
 * file to a Customer DTO for sending as a REST request.
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

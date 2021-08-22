package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.processor;

import com.pluralsight.michaelhoffman.camel.customer.integration.common.dto.Customer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressUpdateLineToCustomerMapperTest {

    private static final List<String> fixtureAddressRow =
        Arrays.asList("1", "1060 W. Addison St.",
            "Suite 100", "Chicago", "IL", "60605");

    @Test
    void process_testSuccessfulProcess() throws Exception {
        Customer customer =
            new AddressUpdateLineToCustomerMapper().process(fixtureAddressRow);
        assertEquals(1, customer.getId());
        assertEquals("1060 W. Addison St.", customer.getAddressLine1());
        assertEquals("Suite 100", customer.getAddressLine2());
        assertEquals("Chicago", customer.getCity());
        assertEquals("IL", customer.getState());
        assertEquals("60605", customer.getPostalCode());
    }
}

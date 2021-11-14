package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.processor;

import com.pluralsight.michaelhoffman.camel.customer.integration.common.InvalidCustomerAddressException;
import com.pluralsight.michaelhoffman.camel.customer.integration.common.dto.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the mapper class.
 */
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

    @Test
    void validate_testSuccessfulProcess() throws Exception {
        new AddressUpdateLineToCustomerMapper().validate(fixtureAddressRow);
    }

    @Test
    void validate_testFailedScenarios() throws Exception {
        Assertions.assertThrows(InvalidCustomerAddressException.class, () -> {
            List<String> invalidList = Arrays.asList(fixtureAddressRow.get(0));
            new AddressUpdateLineToCustomerMapper().validate(invalidList);
        });

        Assertions.assertThrows(InvalidCustomerAddressException.class, () -> {
            new AddressUpdateLineToCustomerMapper().validate(Arrays.asList(null, "A1", "A2", "C", "S", "P"));
        });

        Assertions.assertThrows(InvalidCustomerAddressException.class, () -> {
            new AddressUpdateLineToCustomerMapper().validate(Arrays.asList("INVALID", "A1", "A2", "C", "S", "P"));
        });

        Assertions.assertThrows(InvalidCustomerAddressException.class, () -> {
            new AddressUpdateLineToCustomerMapper().validate(Arrays.asList("1", null, "A2", "C", "S", "P"));
        });

        Assertions.assertThrows(InvalidCustomerAddressException.class, () -> {
            new AddressUpdateLineToCustomerMapper().validate(Arrays.asList("1", "A1", "A2", null, "S", "P"));
        });

        Assertions.assertThrows(InvalidCustomerAddressException.class, () -> {
            new AddressUpdateLineToCustomerMapper().validate(Arrays.asList("1", "A1", "A2", "C", null, "P"));
        });

        Assertions.assertThrows(InvalidCustomerAddressException.class, () -> {
            new AddressUpdateLineToCustomerMapper().validate(Arrays.asList("1", "A1", "A2", "C", "S", null));
        });
    }

}

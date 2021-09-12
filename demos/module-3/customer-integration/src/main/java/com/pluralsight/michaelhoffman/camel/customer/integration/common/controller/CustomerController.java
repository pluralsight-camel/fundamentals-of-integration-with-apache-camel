package com.pluralsight.michaelhoffman.camel.customer.integration.common.controller;

import com.pluralsight.michaelhoffman.camel.customer.integration.common.dto.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to support localized testing of the patch endpoint in the route.
 */
@RestController
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @PatchMapping(path = "/customer/{id}", consumes = "application/json")
    public ResponseEntity<Customer> updateCustomer(
        @PathVariable int id, @RequestBody Customer customer) {
        log.debug("Received customer request patch: " + customer);
        return ResponseEntity.ok(customer);
    }

}

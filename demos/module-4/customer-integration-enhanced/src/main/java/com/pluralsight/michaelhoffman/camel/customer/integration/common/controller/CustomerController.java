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

        if (id == 999) {
            return ResponseEntity.internalServerError().build();
        } else if (id == 9999) {
            return ResponseEntity.notFound().build();
        } else if (id == 99999) {
            try { Thread.sleep(20000); } catch (Exception e) { }
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.ok(customer);
        }
    }
}

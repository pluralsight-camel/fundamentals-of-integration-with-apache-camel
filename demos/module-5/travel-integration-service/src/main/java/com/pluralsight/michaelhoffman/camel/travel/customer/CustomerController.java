package com.pluralsight.michaelhoffman.camel.travel.customer;

import com.pluralsight.michaelhoffman.camel.travel.common.CustomerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * Represents the endpoints for the Customer Domain Service
 */
@RestController
@RequestMapping("customer")
public class CustomerController {

    private static final Logger log =
        LoggerFactory.getLogger(CustomerController.class);

    private final RestTemplate restTemplate;
    private String customerIntegrationServiceHost;

    @Autowired
    public CustomerController(RestTemplate restTemplate,
        @Value("${app.customer-integration-service.host}")
            String customerIntegrationServiceHost) {
        this.restTemplate = restTemplate;
        this.customerIntegrationServiceHost =
            customerIntegrationServiceHost;
    }

    @PostMapping
    public void createCustomer() {
        log.debug("Request to create customer");
        sendEvent(1, "create");
    }

    @PutMapping
    public void updateCustomer() {
        log.debug("Request to update customer");
        sendEvent(1, "update");
    }

    @DeleteMapping
    public void deleteCustomer() {
        log.debug("Request to delete customer");
        sendEvent(1, "delete");
    }

    /**
     * Calls the Customer Integration Service endpoint to post the
     * event message.
     *
     * @param customerId
     * @param eventType
     */
    private void sendEvent(int customerId, String eventType) {
        log.debug("Sending event with type: " + eventType);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CustomerEvent customerEvent =
            new CustomerEvent(customerId, eventType);
        HttpEntity<CustomerEvent> entity = new HttpEntity<>(customerEvent, headers);
        restTemplate.postForEntity(
            customerIntegrationServiceHost + "/event", customerEvent, Void.class);
        log.debug("Event sent successfully");
    }

}

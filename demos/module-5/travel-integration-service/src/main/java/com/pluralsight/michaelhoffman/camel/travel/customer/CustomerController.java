package com.pluralsight.michaelhoffman.camel.travel.customer;

import com.pluralsight.michaelhoffman.camel.travel.common.CustomerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("customer")
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.customer-integration-service.host}")
    private String customerIntegrationServiceHost;

    @PostMapping
    public void createCustomer() {
        sendEvent(1, "create");
    }

    @PutMapping
    public void updateCustomer() {
        sendEvent(1, "update");
    }

    @DeleteMapping
    public void deleteCustomer() {
        sendEvent(1, "delete");
    }

    private void sendEvent(int customerId, String eventType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CustomerEvent customerEvent =
            new CustomerEvent(customerId, eventType);
        HttpEntity<CustomerEvent> entity = new HttpEntity<>(customerEvent, headers);
        restTemplate.postForEntity(
            customerIntegrationServiceHost, customerEvent, Void.class);
    }

}

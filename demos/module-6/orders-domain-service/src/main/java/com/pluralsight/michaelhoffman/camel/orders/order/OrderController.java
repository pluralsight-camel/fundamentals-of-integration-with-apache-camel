package com.pluralsight.michaelhoffman.camel.orders.order;

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
 * Represents the endpoints for the Orders Domain Service
 */
@RestController
@RequestMapping("orders")
public class OrderController {
    private static final Logger log =
        LoggerFactory.getLogger(OrderController.class);

    private final RestTemplate restTemplate;
    private String ordersIntegrationServiceUrl;

    @Autowired
    public OrderController(RestTemplate restTemplate,
        @Value("${app.orders-integration-service.host}")
            String ordersIntegrationServiceUrl) {
        this.restTemplate = restTemplate;
        this.ordersIntegrationServiceUrl =
            ordersIntegrationServiceUrl;
    }

    @PostMapping
    public void createOrder(@RequestBody OrderEvent orderEvent) {
        log.debug("Request to create order");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderEvent> entity = new HttpEntity<>(orderEvent, headers);
        restTemplate.postForEntity(
            ordersIntegrationServiceUrl, orderEvent, Void.class);
        log.debug("Event sent successfully");
    }
}

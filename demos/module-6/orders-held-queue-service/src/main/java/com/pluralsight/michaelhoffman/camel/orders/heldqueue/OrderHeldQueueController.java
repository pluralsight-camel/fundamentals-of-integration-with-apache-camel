package com.pluralsight.michaelhoffman.camel.orders.heldqueue;

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
 * Represents the endpoints for the Orders Held Queue Service
 */
@RestController
@RequestMapping("orders-held-queue")
public class OrderHeldQueueController {
    private static final Logger log =
        LoggerFactory.getLogger(OrderHeldQueueController.class);

    @PostMapping("/order")
    public void processOrderEvent(
        @RequestBody OrderEvent orderEvent
    ) {
        log.debug("Received order event: " + orderEvent);
    }

}

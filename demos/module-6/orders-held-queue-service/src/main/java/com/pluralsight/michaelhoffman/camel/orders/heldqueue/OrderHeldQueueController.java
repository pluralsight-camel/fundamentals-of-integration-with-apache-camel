package com.pluralsight.michaelhoffman.camel.orders.heldqueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

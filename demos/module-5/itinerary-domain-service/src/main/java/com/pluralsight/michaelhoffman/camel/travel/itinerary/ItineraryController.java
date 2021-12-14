package com.pluralsight.michaelhoffman.camel.travel.itinerary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Represents the endpoints for the Itinerary Domain Service
 */
@RestController
@RequestMapping("itinerary")
public class ItineraryController {
    private static final Logger log =
        LoggerFactory.getLogger(ItineraryController.class);

    @PostMapping("/customer")
    public void processCustomerEvent(
        @RequestBody CustomerEvent customerEvent
    ) {
        log.debug("Received customer event: " + customerEvent);
    }

}

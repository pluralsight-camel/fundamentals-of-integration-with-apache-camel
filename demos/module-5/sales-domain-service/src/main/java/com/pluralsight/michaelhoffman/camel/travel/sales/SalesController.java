package com.pluralsight.michaelhoffman.camel.travel.sales;

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
 * Represents the endpoints for the Sales Domain Service
 */
@RestController
@RequestMapping("sales")
public class SalesController {
    private static final Logger log =
        LoggerFactory.getLogger(SalesController.class);

    @PostMapping("/customer")
    public void processCustomerEvent(
        @RequestBody CustomerEvent customerEvent
    ) {
        log.debug("Received customer event: " + customerEvent);
    }

}

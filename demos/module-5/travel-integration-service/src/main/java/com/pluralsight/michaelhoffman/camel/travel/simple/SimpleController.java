package com.pluralsight.michaelhoffman.camel.travel.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("simple")
public class SimpleController {

    private static final Logger log =
        LoggerFactory.getLogger(SimpleController.class);

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createSimple(
        @RequestBody String message) {
        log.debug("Received message: " + message);

        return ResponseEntity.ok(message);
    }
}

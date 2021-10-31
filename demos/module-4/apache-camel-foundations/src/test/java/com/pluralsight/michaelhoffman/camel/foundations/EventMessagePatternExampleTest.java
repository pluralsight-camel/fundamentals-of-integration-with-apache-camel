package com.pluralsight.michaelhoffman.camel.foundations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class EventMessagePatternExampleTest extends CamelTestSupport {

    @EndpointInject("mock:test")
    private MockEndpoint mockEndpoint;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .log("**********************************************")
                    .log("Exchange and Message Information for Event Message\n" +
                        "Message Body: ${body}\n" +
                        "Inbound Message Body: ${in.body}\n" +
                        "Headers: ${headers}\n" +
                        "Exchange ID: ${exchangeId}\n" +
                        "Thread Name: ${threadName}\n" +
                        "Camel ID (Camel Context Name): ${camelId}\n" +
                        "Exchange: ${exchange}\n" +
                        "Route ID: ${routeId}\n" +
                        "Message History: ${messageHistory}"
                    )
                    .log("**********************************************")
                    .to("mock:test");
            }
        };
    }

    @Test
    public void test_eventMessagePatternExample() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mockEndpoint.setExpectedMessageCount(1);
        template.sendBody("direct:start",
            mapper.writeValueAsString(
                new CustomerChangeEvent("12345", 1, "create", Instant.now())));
        mockEndpoint.assertIsSatisfied();
    }

}

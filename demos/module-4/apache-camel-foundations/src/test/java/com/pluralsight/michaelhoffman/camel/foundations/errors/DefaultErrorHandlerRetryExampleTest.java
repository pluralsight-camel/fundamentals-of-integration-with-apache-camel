package com.pluralsight.michaelhoffman.camel.foundations.errors;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class DefaultErrorHandlerRetryExampleTest extends CamelTestSupport {

    private static final Logger log = LoggerFactory.getLogger(DefaultErrorHandlerRetryExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                errorHandler(
                    defaultErrorHandler()
                        .maximumRedeliveries(30)
                        .redeliveryDelay(500)
                        .retryAttemptedLogLevel(LoggingLevel.ERROR));

                from("direct:start")
                    .process(exchange -> {
                        int total = (int)(Math.random() * 10);
                        if (total >= 5) {
                            throw new BadDataException("Error above 5: " + total);
                        }
                    })
                    .to("mock:test");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        mockEndpoint.expectedMessageCount(5);
        template.sendBody("direct:start", "Test");
        template.sendBody("direct:start", "Test");
        template.sendBody("direct:start", "Test");
        template.sendBody("direct:start", "Test");
        template.sendBody("direct:start", "Test");
        mockEndpoint.assertIsSatisfied();
    }
}

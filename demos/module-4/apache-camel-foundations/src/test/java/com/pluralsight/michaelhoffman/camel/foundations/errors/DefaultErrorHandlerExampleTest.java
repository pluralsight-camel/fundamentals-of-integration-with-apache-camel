package com.pluralsight.michaelhoffman.camel.foundations.errors;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultErrorHandlerExampleTest extends CamelTestSupport {

    private static final Logger log = LoggerFactory.getLogger(DefaultErrorHandlerExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .process(exchange -> {
                        if (!exchange.getIn().getBody().equals("GOOD")) {
                            throw new BadDataException("Oops an error!");
                        }
                    })
                    .to("mock:test");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        mockEndpoint.expectedMessageCount(3);
        template.sendBody("direct:start", "GOOD");
        template.sendBody("direct:start", "GOOD");
        try {
            template.sendBody("direct:start", "OOPS!");
        } catch (CamelExecutionException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("**********************************\n")
                .append("An error occurred: ").append(e.getMessage()).append("\n")
                .append("Exchange exception type: ")
                .append(e.getExchange().getException().getClass().getName())
                    .append("\n")
                .append("Exchange property exception caught: ")
                .append(e.getExchange().getProperty(Exchange.EXCEPTION_CAUGHT))
                    .append("\n")
                .append("Exchange property failure endpoint: ")
                .append(e.getExchange().getProperty(Exchange.FAILURE_ENDPOINT))
                    .append("\n")
                .append("Exchange property failure handled: ")
                .append(e.getExchange().getProperty(Exchange.FAILURE_HANDLED))
                    .append("\n")
                .append("**********************************")
            ;
            log.error(sb.toString());
        }
        template.sendBody("direct:start", "GOOD");
        mockEndpoint.assertIsSatisfied();
    }
}

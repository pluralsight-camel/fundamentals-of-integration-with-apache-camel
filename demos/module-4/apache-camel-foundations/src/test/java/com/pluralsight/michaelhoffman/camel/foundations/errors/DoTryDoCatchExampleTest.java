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

public class DoTryDoCatchExampleTest extends CamelTestSupport {

    private static final Logger log = LoggerFactory.getLogger(DoTryDoCatchExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("direct:start")
                .doTry()
                    .process(exchange -> {throw new AException("A");})
                .doCatch(AException.class)
                    .process(exchange -> {log.error("A was thrown");})
                .to("mock:test");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        mockEndpoint.expectedMessageCount(1);
        template.sendBody("direct:start", "A");
        mockEndpoint.assertIsSatisfied();
    }
}

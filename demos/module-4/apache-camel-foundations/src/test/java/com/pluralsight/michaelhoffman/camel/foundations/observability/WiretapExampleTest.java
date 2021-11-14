package com.pluralsight.michaelhoffman.camel.foundations.observability;

import com.pluralsight.michaelhoffman.camel.foundations.errors.AException;
import com.pluralsight.michaelhoffman.camel.foundations.errors.BException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiretapExampleTest extends CamelTestSupport {

    private static final Logger log =
        LoggerFactory.getLogger(WiretapExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockTestEndpoint;

    @EndpointInject("mock:trace")
    private MockEndpoint mockTraceEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("direct:start")
                .wireTap("direct:trace")
                .process(exchange -> {
                    log.debug("Processing: " + exchange.getIn().getBody());
                })
                .to("mock:test");

            from("direct:trace")
                .process(exchange -> {
                    log.debug("Wire tap: " + exchange.getIn().getBody());
                })
                .to("mock:trace");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        mockTestEndpoint.expectedMessageCount(2);
        mockTraceEndpoint.expectedMessageCount(2);
        template.sendBody("direct:start", "A");
        template.sendBody("direct:start", "B");
        mockTestEndpoint.assertIsSatisfied();
        mockTraceEndpoint.assertIsSatisfied();
    }


}

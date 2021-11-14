package com.pluralsight.michaelhoffman.camel.foundations.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SEDARoutingExampleTest extends CamelTestSupport {

    private static final Logger log =
        LoggerFactory.getLogger(SEDARoutingExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockTestEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("direct:start")
                .to("seda:logMessage");

            from("seda:logMessage?concurrentConsumers=20")
                .log(LoggingLevel.ERROR, "Message: ${body}")
                // Artificial delay so that you can see the messages
                // asynchronously processed in the log
                .delay(5000)
                .to("mock:test");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        mockTestEndpoint.expectedMessageCount(100);
        for (int i = 0; i < 100; i++) {
            template.sendBody("direct:start", "Message: " + i);
        }
        mockTestEndpoint.assertIsSatisfied();
    }


}

package com.pluralsight.michaelhoffman.camel.foundations.throttle;

import com.pluralsight.michaelhoffman.camel.foundations.errors.BadDataException;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class ThrottleExampleTest extends CamelTestSupport {

    private static final Logger log = LoggerFactory.getLogger(ThrottleExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("direct:start")
                // Maximum request count
                .throttle(25)
                    // Length of time maximum is valid
                    .timePeriodMillis(5000)
                    .to("log:?level=ERROR&showBody=true", "mock:test");
            }
        };
    }

    @Test
    public void test_throttleExample() throws Exception {
        mockEndpoint.expectedMessageCount(100);
        IntStream.range(0, 100).forEach(x->template.sendBody("direct:start", x));
        mockEndpoint.assertIsSatisfied();
    }
}

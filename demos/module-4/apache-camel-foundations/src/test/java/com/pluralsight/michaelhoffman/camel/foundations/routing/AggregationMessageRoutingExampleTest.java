package com.pluralsight.michaelhoffman.camel.foundations.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.juli.logging.Log;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AggregationMessageRoutingExampleTest extends CamelTestSupport {

    private static final Logger log =
        LoggerFactory.getLogger(AggregationMessageRoutingExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockTestEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("direct:start")
                .aggregate(header("eventType"), (oldEx, newEx) -> {
                        if (oldEx == null) {
                            List<Integer> elements =
                                new ArrayList<>(newEx.getIn().getBody(Integer.class));
                            newEx.getIn().setBody(elements);
                            return newEx;
                        }
                        List<Integer> elements = oldEx.getIn().getBody(List.class);
                        elements.add(newEx.getIn().getBody(Integer.class));
                        oldEx.getIn().setBody(elements);
                        return oldEx;
                })
                .completionSize(4)
                .log(LoggingLevel.ERROR, "Aggregated body: ${body}")
                .to("mock:test");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        mockTestEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", 1, "eventType", "customerAudit");
        template.sendBodyAndHeader("direct:start", 2, "eventType", "customerAudit");
        template.sendBodyAndHeader("direct:start", 3, "eventType", "customerAudit");
        template.sendBodyAndHeader("direct:start", 4, "eventType", "customerAudit");
        mockTestEndpoint.assertIsSatisfied();
    }


}

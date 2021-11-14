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

public class SlackNotificationWebhookExampleTest extends CamelTestSupport {

    private static final Logger log =
        LoggerFactory.getLogger(SlackNotificationWebhookExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            onException(AException.class)
                .log(LoggingLevel.ERROR, "A exception")
                .handled(true)
                .to("slack:?webhookUrl=" +
                    "https://hooks.slack.com/services/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");
            onException(BException.class)
                .log(LoggingLevel.ERROR, "B exception")
                .handled(true)
                .to("slack:?webhookUrl=" +
                    "https://hooks.slack.com/services/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");

            from("direct:start")
                .process(exchange -> {
                    if (exchange.getIn().getBody().equals("A")) {
                        throw new AException("A");
                    } else {
                        throw new BException("B");
                    }
                })
                .to("mock:test");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        mockEndpoint.expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "A");
        } catch (CamelExecutionException e) {
            log.error("An error occurred: " + e.getMessage() + ", and the exchange was " +
                "captured: " + e.getExchange().getException().getClass().getName());
        }
        try {
            template.sendBody("direct:start", "B");
        } catch (CamelExecutionException e) {
            log.error("An error occurred: " + e.getMessage() + ", and the exchange was " +
                "captured: " + e.getExchange().getException().getClass().getName());
        }
        mockEndpoint.assertIsSatisfied();
    }


}

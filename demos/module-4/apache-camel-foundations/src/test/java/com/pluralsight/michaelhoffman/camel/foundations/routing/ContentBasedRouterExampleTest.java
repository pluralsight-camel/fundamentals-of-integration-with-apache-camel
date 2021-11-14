package com.pluralsight.michaelhoffman.camel.foundations.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentBasedRouterExampleTest extends CamelTestSupport {

    private static final Logger log =
        LoggerFactory.getLogger(ContentBasedRouterExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockTestEndpoint;

    @EndpointInject("mock:testCreate")
    private MockEndpoint mockTestCreateEndpoint;

    @EndpointInject("mock:testUpdate")
    private MockEndpoint mockTestUpdateEndpoint;

    @EndpointInject("mock:testDelete")
    private MockEndpoint mockTestDeleteEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("direct:start")
                .choice()
                    .when(simple("${header.eventType} == 'createCustomer'"))
                        .to("direct:create")
                    .when(simple("${header.eventType} == 'updateCustomer'"))
                        .to("direct:update")
                    .when(simple("${header.eventType} == 'deleteCustomer'"))
                        .to("direct:delete")
                    .otherwise()
                        .to("mock:test");

            from("direct:create")
                .log(LoggingLevel.ERROR, "Create: ${body}")
                .to("mock:testCreate");

            from("direct:update")
                .log(LoggingLevel.ERROR, "Update: ${body}")
                .to("mock:testUpdate");

            from("direct:delete")
                .log(LoggingLevel.ERROR, "Delete: ${body}")
                .to("mock:testDelete");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        mockTestEndpoint.expectedMessageCount(0);
        mockTestCreateEndpoint.expectedMessageCount(3);
        mockTestUpdateEndpoint.expectedMessageCount(3);
        mockTestDeleteEndpoint.expectedMessageCount(3);
        template.sendBodyAndHeader("direct:start", 1, "eventType", "createCustomer");
        template.sendBodyAndHeader("direct:start", 2, "eventType", "updateCustomer");
        template.sendBodyAndHeader("direct:start", 3, "eventType", "createCustomer");
        template.sendBodyAndHeader("direct:start", 4, "eventType", "deleteCustomer");
        template.sendBodyAndHeader("direct:start", 5, "eventType", "createCustomer");
        template.sendBodyAndHeader("direct:start", 6, "eventType", "updateCustomer");
        template.sendBodyAndHeader("direct:start", 7, "eventType", "deleteCustomer");
        template.sendBodyAndHeader("direct:start", 8, "eventType", "updateCustomer");
        template.sendBodyAndHeader("direct:start", 9, "eventType", "deleteCustomer");
        mockTestEndpoint.assertIsSatisfied();
        mockTestCreateEndpoint.assertIsSatisfied();
        mockTestUpdateEndpoint.assertIsSatisfied();
        mockTestDeleteEndpoint.assertIsSatisfied();
    }


}

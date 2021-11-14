package com.pluralsight.michaelhoffman.camel.foundations.routing;

import com.pluralsight.michaelhoffman.camel.foundations.errors.BadDataException;
import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoutingSlipExampleTest extends CamelTestSupport {

    private static final Logger log =
        LoggerFactory.getLogger(RoutingSlipExampleTest.class);

    @EndpointInject("mock:test")
    private MockEndpoint mockTestEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("direct:start")
                .process(exchange -> {
                    Customer customer = exchange.getIn().getBody(Customer.class);
                    List<String> enrichmentSlips = new ArrayList<>();
                    if (customer.getBillingAddress().getAddressLine1() == null) {
                        enrichmentSlips.add("direct://enrichBillingAddress");
                    }
                    if (customer.getShippingAddress().getAddressLine1() == null) {
                        enrichmentSlips.add("direct://enrichShippingAddress");
                    }
                    if (customer.getPrimaryContact().getName() == null) {
                        enrichmentSlips.add("direct://enrichPrimaryContact");
                    }
                    exchange.getIn().setHeader("enrichmentRoutingSlip",
                        enrichmentSlips.stream().collect(Collectors.joining(",")));
                })
                .routingSlip(header("enrichmentRoutingSlip"))
                .to("mock:test");

            from("direct://enrichBillingAddress")
                .process(exchange -> {
                    Customer customer = exchange.getIn().getBody(Customer.class);
                    customer.setBillingAddress(new Address(1, "billing address line",
                        "billing city", "billing state", "billing postal"));
                    exchange.getIn().setBody(customer);
                })
                .log(LoggingLevel.ERROR, "Added billing address: ${body}");

            from("direct://enrichShippingAddress")
                .process(exchange -> {
                    Customer customer = exchange.getIn().getBody(Customer.class);
                    customer.setShippingAddress(new Address(1, "shipping address line",
                        "shipping city", "shipping state", "shipping postal"));
                    exchange.getIn().setBody(customer);
                })
                .log(LoggingLevel.ERROR, "Added shipping address: ${body}");

            from("direct://enrichPrimaryContact")
                .process(exchange -> {
                    Customer customer = exchange.getIn().getBody(Customer.class);
                    customer.setPrimaryContact(new Contact(1, "contact"));
                    exchange.getIn().setBody(customer);
                })
                .log(LoggingLevel.ERROR, "Added contact: ${body}");
            }
        };
    }

    @Test
    public void test_defaultErrorHandlerExample() throws Exception {
        Customer customerResult1 = new Customer(1,
            new Address(1, "billing address line",
            "billing city", "billing state", "billing postal"),
            new Address(1, "shipping address line",
                "shipping city", "shipping state", "shipping postal"),
            new Contact(1, "contact"));
        Customer customerResult2 = new Customer(2,
            new Address(2, "b street",
            "b city", "b state", "b postal"),
            new Address(2, "s street", "s city", "s state", "s postal"),
            new Contact(2, "test account"));

        mockTestEndpoint.expectedMessageCount(2);

        template.sendBody("direct:start",
            new Customer(1, new Address(1), new Address(1), new Contact(1)));
        template.sendBody("direct:start",
            new Customer(2, new Address(2, "b street",
                "b city", "b state", "b postal"),
                new Address(2, "s street", "s city", "s state", "s postal"),
                new Contact(2, "test account")));

        Customer exCust1 = mockTestEndpoint.getExchanges().get(0).getIn().getBody(Customer.class);
        Customer exCust2 = mockTestEndpoint.getExchanges().get(1).getIn().getBody(Customer.class);

        Assertions.assertEquals(exCust1.getBillingAddress().getAddressLine1(),
            customerResult1.getBillingAddress().getAddressLine1());
        Assertions.assertEquals(exCust1.getShippingAddress().getAddressLine1(),
            customerResult1.getShippingAddress().getAddressLine1());
        Assertions.assertEquals(exCust1.getPrimaryContact().getName(),
            customerResult1.getPrimaryContact().getName());

        Assertions.assertEquals(exCust2.getBillingAddress().getAddressLine1(),
            customerResult2.getBillingAddress().getAddressLine1());
        Assertions.assertEquals(exCust2.getShippingAddress().getAddressLine1(),
            customerResult2.getShippingAddress().getAddressLine1());
        Assertions.assertEquals(exCust2.getPrimaryContact().getName(),
            customerResult2.getPrimaryContact().getName());

        mockTestEndpoint.assertIsSatisfied();
    }


}

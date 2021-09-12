package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute;

import com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.processor.AddressUpdateLineToCustomerMapper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AddressUpdatesToCustomerServiceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file")
            .routeId("address-updates-to-customer-service-route")
            .to("rest:patch:customer?host=localhost");
    }
}

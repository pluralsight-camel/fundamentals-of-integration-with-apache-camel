package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute;

import com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.processor.AddressUpdateLineToCustomerMapper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AddressUpdatesToCustomerServiceRoute extends RouteBuilder {

    private CsvDataFormat csvDataFormatAddressUpdate;

    public AddressUpdatesToCustomerServiceRoute(
        @Qualifier("csvDataFormatAddressUpdate") CsvDataFormat csvDataFormatAddressUpdate
    ) {
        this.csvDataFormatAddressUpdate = csvDataFormatAddressUpdate;
    }

    @Override
    public void configure() throws Exception {
        from("file:{{app.addressToCustomerRoute.directory}}" +
            "?include={{app.addressToCustomerRoute.includeFile}}" +
            "&move={{app.addressToCustomerRoute.moveDirectory}}")
            .routeId("address-updates-to-customer-service-route")
            .unmarshal(csvDataFormatAddressUpdate)
            .split(body())
            .bean(AddressUpdateLineToCustomerMapper.class, "process")
            .setProperty("customerId", simple("${body.id}"))
            .marshal()
            .json()
            .toD("rest:patch:customer/${exchangeProperty.customerId}?host={{app.customer-service.host}}");
    }
}

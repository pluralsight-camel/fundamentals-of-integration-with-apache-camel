package com.pluralsight.michaelhoffman.camel.customer.integration.config;

import org.apache.camel.management.PublishEventNotifier;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationConfig {

    @Bean("csvDataFormatAddressUpdate")
    public CsvDataFormat csvDataFormatAddressUpdate() {
        CsvDataFormat csvDataFormatAddressUpdate = new CsvDataFormat();
        csvDataFormatAddressUpdate.setDelimiter(",");
        csvDataFormatAddressUpdate.setSkipHeaderRecord("true");
        return csvDataFormatAddressUpdate;
    }
}

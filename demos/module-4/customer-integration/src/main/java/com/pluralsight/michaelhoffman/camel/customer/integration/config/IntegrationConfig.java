package com.pluralsight.michaelhoffman.camel.customer.integration.config;

import org.apache.camel.model.dataformat.CsvDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Integration config for supporting CSV data formatting coming
 * from file processing in the route.
 */
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

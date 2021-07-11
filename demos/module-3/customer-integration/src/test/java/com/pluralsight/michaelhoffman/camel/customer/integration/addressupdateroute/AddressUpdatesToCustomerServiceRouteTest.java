package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute;

import com.pluralsight.michaelhoffman.camel.customer.integration.config.IntegrationConfig;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@CamelSpringBootTest
@SpringBootApplication
@ContextConfiguration(classes = IntegrationConfig.class)
@TestPropertySource(locations = "classpath:/config/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("file:.*|rest:.*")
@UseAdviceWith
public class AddressUpdatesToCustomerServiceRouteTest {
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock://rest:patch:customer/1")
    private MockEndpoint restEndpoint;

    @Value("classpath:data/customer-address-update-valid.csv")
    private Resource customerAddressUpdateFileValidResource;

    @Test
    public void route_testValid() throws Exception {
        AdviceWith.adviceWith(camelContext, "address-updates-to-customer-service-route",
            rb -> rb.replaceFromWith("direct:file:start"));
        camelContext.start();

        restEndpoint.expectedMessageCount(1);

        GenericFile file = new GenericFile();
        file.setFile(customerAddressUpdateFileValidResource.getFile());

        producerTemplate.sendBody("direct:file:start", file);

        restEndpoint.assertIsSatisfied();
    }

}

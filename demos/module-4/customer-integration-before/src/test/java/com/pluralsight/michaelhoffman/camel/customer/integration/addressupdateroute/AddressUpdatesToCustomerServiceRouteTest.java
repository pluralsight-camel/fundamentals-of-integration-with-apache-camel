package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute;

import com.pluralsight.michaelhoffman.camel.customer.integration.config.IntegrationConfig;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ToDynamicDefinition;
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
@TestPropertySource(locations = "classpath:/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AddressUpdatesToCustomerServiceRouteTest {

    @Test
    public void route_testValid() throws Exception {
    }

}

package com.pluralsight.michaelhoffman.camel.travel.integration;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@CamelSpringBootTest
@SpringBootApplication
@ContextConfiguration(classes = IntegrationConfig.class)
@TestPropertySource(locations = "classpath:/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("rest*")
@UseAdviceWith
public class SimpleRabbitMQRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate template;

    @Test
    public void test_simpleRabbitMQExample() throws Exception {
        MockEndpoint restEndpoint =
            camelContext.getEndpoint(
                "mock://rest:post:simple", MockEndpoint.class);

        camelContext.start();

        restEndpoint.expectedMessageCount(1);
        template.sendBody("direct:simpleStart", "TEST");
        restEndpoint.assertIsSatisfied();
    }
}

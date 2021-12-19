package com.pluralsight.michaelhoffman.camel.simple.integration;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
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
public class CompetingConsumersRabbitMQRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate template;

    @Test
    public void test_CompetingConsumerExample() throws Exception {
        MockEndpoint restEndpoint =
            camelContext.getEndpoint(
                "mock://rest:post:simple", MockEndpoint.class);

        camelContext.start();

        restEndpoint.expectedMessageCount(100);
        for (int i = 0; i < 100; i++) {
            template.sendBody("direct:produceEvent", Integer.toString(i));
        }
        restEndpoint.assertIsSatisfied();
    }
}

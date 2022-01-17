package com.pluralsight.michaelhoffman.camel.fraud.route;

import com.pluralsight.michaelhoffman.camel.fraud.config.IntegrationConfig;
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
@TestPropertySource(locations = "classpath:/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("file:.*")
@UseAdviceWith
public class TransactionIngestionRouteTest {
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:result")
    private MockEndpoint mockResultEndpoint;

    @Value("classpath:data/customer-transaction-large-file.csv")
    private Resource customerTransactionLargeFileResource;

    @Test
    public void route_testValid() throws Exception {
        GenericFile file = new GenericFile();
        file.setFile(customerTransactionLargeFileResource.getFile());

        AdviceWith.adviceWith(camelContext,
            "transaction-file-upload-route",
            rb -> rb.replaceFromWith("direct:file:start"));
        // Once advice with is used, the camel context has to be started manually
        camelContext.start();
        mockResultEndpoint.expectedMessageCount(200);
        long start = System.currentTimeMillis();
        producerTemplate.sendBody("direct:file:start", file);
        long end = System.currentTimeMillis();
        long total = end - start;
        System.err.println("Total time: " + total + "ms");
        Thread.sleep(20000);
        mockResultEndpoint.assertIsSatisfied();
    }

}

package com.pluralsight.michaelhoffman.camel.simple.integration;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class IntegrationConfig {

    @Value("${app.rabbitmq.username}")
    private String rabbitMqUsername;

    @Value("${app.rabbitmq.password}")
    private String rabbitMqPassword;

    @Value("${app.rabbitmq.host}")
    private String rabbitMqHost;

    @Value("${app.rabbitmq.port}")
    private int rabbitMqPort;

    @Value("${app.rabbitmq.virtualHost}")
    private String rabbitMqVirtualHost;

    @Bean
    public ConnectionFactory rabbitConnectionFactory()
        throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        ConnectionFactory rabbitConnectionFactory = new ConnectionFactory();
        rabbitConnectionFactory.setUsername(rabbitMqUsername);
        rabbitConnectionFactory.setPassword(rabbitMqPassword);
        rabbitConnectionFactory.setHost(rabbitMqHost);
        rabbitConnectionFactory.setPort(rabbitMqPort);
        rabbitConnectionFactory.setVirtualHost(rabbitMqVirtualHost);
        return rabbitConnectionFactory;
    }

}

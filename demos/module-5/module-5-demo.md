# Fundamentals of Integration with Apache Camel - Module 5 Demonstration

Module 5 focuses on event notification with Apache Camel and RabbitMQ.  

## RabbitMQ Username and Password

The username and password for the RabbitMQ configuration are both: travelintegrationservice

## Project Setup

1. Java
    1. This project uses the latest version of JDK 11. You can download it from here: https://openjdk.java.net/projects/jdk/11/
2. Submodules
    1. The sub-module simple-integration-service provides a simple example of a Camel route using RabbitMQ and several pattern implementations
    2. The sub-module rabbitmq-docker provides the docker file for running RabbitMQ to support all examples. 
    3. The remaining sub-modules support the travel integration scenarios. 
3. Maven
    1. At the root of the project is the maven executable "mvnw". All code is compiled and executed from it. You can also use the latest version of Maven if you prefer. In either case, the maven command is assumed to be on your path when running the demonstrations.
4. IDE
    1. All demonstrations are performed in the most recent version of IntelliJ IDEA Ultimate version. When importing the project, remember to correctly configure the maven runtime and the correct version of the JDK.
5. Docker
   1. To run the RabbitMQ broker server, you will need to have the latest version of Docker installed. 

## Project Dependencies

The following was a dependency added to the project for supporting RabbitMQ routing. 

```
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-rabbitmq-starter</artifactId>
</dependency>
```

## Running the Project

I've included the Camel Maven plugin to support directly executing the application. Simply run the following command from the root of the module:

```
mvnw exec:java
```

## Running RabbitMQ

The following project file contains the docker file for running RabbitMQ: module-5/rabbitmq-docker/src/main/docker/rabbitmq.yml

RabbitMQ will be initialized with exchanges, queues and bindings based on the definitions file here: module-5/rabbitmq-docker/src/main/docker/config/definitions.json

From the docker directly, execute the command:

```
docker compose -f rabbitmq.yml up
```

Then to shut down the RabbitMQ container, execute the command:

```
docker compose -f rabbitmq.yml down
```

The docker file was scripted to not persist data from the broker after shut down. This assures a clean environment each time the container is started.  

## Project Links

* [Camel RabbitMQ Component](https://camel.apache.org/components/3.13.x/rabbitmq-component.html)
* [RabbitMQ](https://www.rabbitmq.com/)
* [RabbitMQ Java API](https://www.rabbitmq.com/api-guide.html)

## Demo 1 - Simple Route Example

The first route demonstration is found in the sub-module simple-integration-service. You will need to have RabbitMQ running to execute the test.

* For the route to communicate with RabbitMQ, a configuration class was needed. The class is: 

```
com.pluralsight.michaelhoffman.camel.simple.integration.IntegrationConfig
```

* The configuration class annotates five application property values as members. These are used to initialize a rabbit connection factory as shown in the code below:

```
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
```

* The class name of the route is: 

```
com.pluralsight.michaelhoffman.camel.simple.integration.SimpleRabbitMQRoute
```

* The following code will publish the message from a direct endpoint to RabbitMQ:

```
        from("direct:simpleStart")
            .to("rabbitmq:simple" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&autoDelete=false" +
                "&bridgeErrorHandler=true" +
                "&declare=false" +
                "&exchangeType=topic"
            );
```

* There are two subscribers that consume messages from queues. The queues are simple_a and simple_b. They are bound using routing keys.

```
        from("rabbitmq:simple" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=topic" +
            "&passive=true" +
            "&queue=simple_a"
        )
            .log(LoggingLevel.ERROR, "Queue A: ${body}")
            .to("rest:post:simple?host={{app.simple-service.host}}");

        from("rabbitmq:simple" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=topic" +
            "&passive=true" +
            "&queue=simple_b"
        )
```

* An alternate exchange is also defined for messages that don't match the routing key. The alternate exchange is bound to a queue named simple_nomatch.

```
        from("rabbitmq:simple_nomatch" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=fanout" +
            "&passive=true" +
            "&queue=simple_nomatch"
        )
            .log(LoggingLevel.ERROR, "Queue No Match: ${body}")
            .to("rest:post:simple?host={{app.simple-service.host}}");
```

* You can then run the test case here to execute the route: 

```
com.pluralsight.michaelhoffman.camel.simple.integration.SimpleRabbitMQRouteTest.test_simpleRabbitMQExample
```

* The test will send three messages, each with a routing key. The routing key should map to the queue binding. 

```
        restEndpoint.expectedMessageCount(3);
        template.sendBodyAndHeader("direct:simpleStart", "A", RabbitMQConstants.ROUTING_KEY, "simple.a");
        template.sendBodyAndHeader("direct:simpleStart", "B", RabbitMQConstants.ROUTING_KEY, "simple.b");
        template.sendBodyAndHeader("direct:simpleStart", "C", RabbitMQConstants.ROUTING_KEY, "simple.c");
        restEndpoint.assertIsSatisfied();
```

## Demo 2 - Event Notification Travel Scenario

* This demonstration can be run with all sub-modules started. The following are the sub-modules and their port mappings:
  * customer-domain-service = 8081
  * customer-integration-service = 8082
  * itinerary-domain-service = 8083
  * itinerary-integration-service = 8084
  * sales-domain-service = 8085
  * sales-integration-service = 8086

* The customer controller endpoints are the starting point for the demonstration. You can run the following curl commands to test each endpoint:

```
curl -X POST http://localhost:8081/customer

curl -X POST http://localhost:8081/customer

curl -X DELETE http://localhost:8081/customer
```

* The customer controller will call the REST DSL endpoint on the customer integration service.  

```
com.pluralsight.michaelhoffman.camel.travel.customer.CustomerController

/**
 * Calls the Customer Integration Service endpoint to post the
 * event message.
 *
 * @param customerId
 * @param eventType
 */
private void sendEvent(int customerId, String eventType) {
    log.debug("Sending event with type: " + eventType);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    CustomerEvent customerEvent =
        new CustomerEvent(customerId, eventType);
    HttpEntity<CustomerEvent> entity = new HttpEntity<>(customerEvent, headers);
    restTemplate.postForEntity(
        customerIntegrationServiceUrl, customerEvent, Void.class);
    log.debug("Event sent successfully");
}

```

* The customer-integration-service project has REST DSL defined to receive the event. The route will publish the message based on the event type. 

```
com.pluralsight.michaelhoffman.camel.travel.customer.integration.CustomerEventPublisherRoute

        restConfiguration()
            .component("servlet")
            .host(host)
            .port(port)
            .bindingMode(RestBindingMode.json);

        errorHandler(defaultErrorHandler().log(log));

        onException(InvalidEventTypeException.class)
            .handled(true)
            .log(LoggingLevel.ERROR, "An invalid event type was sent")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().constant("Invalid event type sent");

        onException(JsonParseException.class)
            .handled(true)
            .log(LoggingLevel.ERROR, "An exception occurred parsing the request body")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().constant("Json pare exception was thrown");

        /**
         * Publish customer integration events
         */
        rest("/customer-integration")
            .post("/event")
                .type(CustomerEvent.class)
                .consumes("application/json")
            .route()
            .removeHeader(Exchange.HTTP_METHOD)
            .removeHeader(Exchange.HTTP_PATH)
            .removeHeader(Exchange.HTTP_URI)
            .removeHeader(Exchange.HTTP_URL)
            .removeHeader("CamelServletContextPath")
            .choice()
                .when().simple("${body.eventType} =~ 'create'")
                    .setProperty("routingKey", constant("customer.create"))
                    .to("direct:sendEventToRabbitMQ")
                .when().simple("${body.eventType} =~ 'update'")
                    .setProperty("routingKey", constant("customer.update"))
                    .to("direct:sendEventToRabbitMQ")
                .when().simple("${body.eventType} =~ 'delete'")
                    .setProperty("routingKey", constant("customer.delete"))
                    .to("direct:sendEventToRabbitMQ")
                .otherwise()
                    .throwException(new InvalidEventTypeException("Event type is invalid"));

        from("direct:sendEventToRabbitMQ")
            .setHeader(RabbitMQConstants.ROUTING_KEY, exchangeProperty("routingKey"))
            .marshal()
                .json()
            .to("rabbitmq:customer" +
                "?connectionFactory=#rabbitConnectionFactory" +
                "&autoDelete=false" +
                "&bridgeErrorHandler=true" +
                "&declare=false" +
                "&exchangePattern=InOnly" +
                "&exchangeType=topic"
            );
```

* The sales-integration-service and itinerary-integration-service will consume the published messages. The Sales service will process customer creates and deletes. The itinerary service will process customer deletes only. Each service will then call the respective sales or itinerary domain service. 

```
com.pluralsight.michaelhoffman.camel.travel.sales.integration.SalesCustomerEventConsumerRoute
com.pluralsight.michaelhoffman.camel.travel.itinerary.integration.ItineraryCustomerEventConsumerRoute
```

## Demo 3 - Competing Consumers

* The example route for competing consumers shows how you can use the rabbitmq component option to create three consumers of a queue: 

```
com.pluralsight.michaelhoffman.camel.simple.integration.CompetingConsumersRabbitMQRoute
com.pluralsight.michaelhoffman.camel.simple.integration.CompetingConsumersRabbitMQRouteTest
```

## Demo 4 - Dead Letter Channel

* The example route for a dead letter queue shows how you can use an error handler to route undeliverable messages to a dead letter queue:

```
com.pluralsight.michaelhoffman.camel.simple.integration.SimpleDeadLetterQueueRoute
com.pluralsight.michaelhoffman.camel.simple.integration.SimpleDeadLetterQueueRouteTest
```

## Demo 5 - Idempotent Consumer

* The example route for an idempotent consumer that dedupes incoming messages: 

```
com.pluralsight.michaelhoffman.camel.simple.integration.SimpleIdempotentConsumerRoute
com.pluralsight.michaelhoffman.camel.simple.integration.SimpleIdempotentConsumerRouteTest
```

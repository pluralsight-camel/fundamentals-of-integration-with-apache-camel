# Fundamentals of Integration with Apache Camel - Module 4 Demonstration

Module 4 focuses on foundational concepts without demonstrations; however, I have created tests and examples to support the spoken material. 

## Project Setup

1. Java
    1. This project uses the latest version of JDK 11. You can download it from here: https://openjdk.java.net/projects/jdk/11/
2. Submodules
    1. The module apache-camel-foundations is the only submodule and contains all finalized code
3. Maven
    1. At the root of the project is the maven executable "mvnw". All code is compiled and executed from it. You can also use the latest version of Maven if you prefer. In either case, the maven command is assumed to be on your path when running the demonstrations.
4. IDE
    1. All demonstrations are performed in the most recent version of IntelliJ IDEA Ultimate version. When importing the project, remember to correctly configure the maven runtime and the correct version of the JDK.

## Project Dependencies

In the pom.xml file of the module, I've included several dependencies that are required for the code:

* Spring Boot and Camel BOM, note the most recent version of Spring Boot is used for this project (2.5.2):
    ```xml
    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot BOM -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- Camel BOM -->
            <dependency>
                <groupId>org.apache.camel.springboot</groupId>
                <artifactId>camel-spring-boot-bom</artifactId>
                <version>${camel.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    ```
* Spring Boot and Spring Framework dependencies:
    ```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    ```

* Camel libraries with support for running on Spring Boot, includes support for CSV, HTTP and Jackson libraries:
    ```xml
        <dependency>
            <groupId>org.apache.camel.springboot</groupId>
            <artifactId>camel-spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.camel.springboot</groupId>
            <artifactId>camel-http-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.camel.springboot</groupId>
            <artifactId>camel-jackson-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-management</artifactId>
        </dependency>

        <dependency>
            <groupId>org.jolokia</groupId>
            <artifactId>jolokia-core</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-test-spring-junit5</artifactId>
            <scope>test</scope>
        </dependency>
    ```

## Running the Project

I've included the Camel Maven plugin to support directly executing the application. Simply run the following command from the root of the module:

```
mvnw exec:java
```

## Project Links


## Example 1 - Event Message Pattern

The test class com.pluralsight.michaelhoffman.camel.foundations.EventMessagePatternExampleTest shows a simple example of sending an event message through a route. It includes logging out details about the message. 

## Example 2 - Default Error Handler 

The test class com.pluralsight.michaelhoffman.camel.foundations.errors.DefaultErrorHandlerExampleTest shows the default behavior of Camel error handling. Key things to note about this test route:

* There are four messages sent into the route, but the assertion only has 3 messages expected. Why? Because the third message failed and the pipeline ended for the exchange.
* Camel caught the exception and logged it. The exchange was captured as part of the exception and no redelivery was performed on the message as part of the default behavior. 

When the test gets executed, you'll notice several important log messages: 

* The error log from Camel showing the DefaultErrorHandler type
* The failed delivery with 1 delivery attempt
* The actual exception thrown
* The message history
* The stack trace.

```
11:16:55.548 [main] ERROR org.apache.camel.processor.errorhandler.DefaultErrorHandler - 
Failed delivery for (MessageId: 2D1DD601DD4D13A-0000000000000002 on ExchangeId: 2D1DD601DD4D13A-0000000000000002). 
Exhausted after delivery attempt: 1 caught: com.pluralsight.michaelhoffman.camel.foundations.errors.BadDataException: Oops an error!

Message History (complete message history is disabled)
---------------------------------------------------------------------------------------------------------------------------------------
RouteId              ProcessorId          Processor                                                                        Elapsed (ms)
[route1            ] [route1            ] [from[direct://start]                                                          ] [         1]
...
[route1            ] [process1          ] [Processor@0x65fe9e33                                                          ] [         0]

Stacktrace
---------------------------------------------------------------------------------------------------------------------------------------

com.pluralsight.michaelhoffman.camel.foundations.errors.BadDataException: Oops an error!
at com.pluralsight.michaelhoffman.camel.foundations.errors.DefaultErrorHandlerExampleTest$1.lambda$configure$0(DefaultErrorHandlerExampleTest.java:27)
at org.apache.camel.support.processor.DelegateSyncProcessor.process(DelegateSyncProcessor.java:66)
at org.apache.camel.processor.errorhandler.RedeliveryErrorHandler$SimpleTask.run(RedeliveryErrorHandler.java:463)
at org.apache.camel.impl.engine.DefaultReactiveExecutor$Worker.schedule(DefaultReactiveExecutor.java:179)
at org.apache.camel.impl.engine.DefaultReactiveExecutor.scheduleMain(DefaultReactiveExecutor.java:64)
at org.apache.camel.processor.Pipeline.process(Pipeline.java:184)
```

## Example 3 - Default Error Handler with Retry

The example test com.pluralsight.michaelhoffman.camel.foundations.errors.DefaultErrorHandlerRetryExampleTest will handle redelivering the exchange for a route when an exception occurs. Here I'm explicitly defining an error handler with redelivery policies:

```java
errorHandler(
    defaultErrorHandler()
        .maximumRedeliveries(30)
        .redeliveryDelay(500)
        .retryAttemptedLogLevel(LoggingLevel.ERROR));

```

This will retry the exchange up to 30 times with a delay of 500ms. Here is the result of the failure being logged:

```
15:09:08.949 [main] ERROR org.apache.camel.processor.errorhandler.DefaultErrorHandler - Failed delivery for (MessageId: B85B26DA606B150-0000000000000001 on ExchangeId: B85B26DA606B150-0000000000000001). On delivery attempt: 1 caught: com.pluralsight.michaelhoffman.camel.foundations.errors.BadDataException: Error above 5: 6
15:09:08.949 [main] DEBUG org.apache.camel.processor.errorhandler.RedeliveryErrorHandler - Redelivery delay calculated as 500
15:09:08.949 [main] DEBUG org.apache.camel.processor.errorhandler.RedeliveryPolicy - Sleeping for: 500 millis until attempting redelivery
```

## Example 4 - On Exception Example

The example test com.pluralsight.michaelhoffman.camel.foundations.errors.OnExceptionExampleTest is a different approach for error handling as it applies exception handling policies based on the exception type. This is a useful approach when you want different policies based on the type of error that may occur. For example, you may only want to redeliver messages if a REST service times out, but not when there is invalid data produced to the route. Here is the implementation of the route:

```java
@Override
protected RouteBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
        onException(AException.class).log(LoggingLevel.ERROR, "A exception");
        onException(BException.class).log(LoggingLevel.ERROR, "B exception");

        from("direct:start")
            .process(exchange -> {
                if (exchange.getIn().getBody().equals("A")) {
                    throw new AException("A");
                } else {
                    throw new BException("B");
                }
            })
            .to("mock:test");
        }
    };
}
```

In fact, you can override the default error handler if you wanted. By also defining an error handler at the beginning of the route builder, default configuration will be used but the two exception types I've defined will override the way those exceptions are logged. 

## Example 5 - Do Try, Do Catch and Do Finally Definition Example

The example com.pluralsight.michaelhoffman.camel.foundations.errors.DoTryDoCatchExampleTest is an example of an approach that I covered in the error handling section. In this example, I'm simply wrapping the .process definition with a doTry and doCatch definition. This simulates Java's try-catch-finally blocks:

```java
@Override
protected RouteBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
            from("direct:start")
               .doTry()
                    .process(exchange -> {throw new AException("A");})
                    .to("mock:test")
                .doCatch(AException.class)
                    .process(exchange -> {log.error("A was thrown");})
                .endDoTry();
        }
    };
}
```

This gives you the opportunity to directly handle a variety of exceptions at any point in the route processing. It also means you are now responsible for handling what should happen to the error. Should the message continue to process? Should the message be logged? Should a notification be sent? Should there ba a retry? 

## Example 6 - Customer Integration Redelivery Policy

The enhanced customer integration route was updated to include a redelivery policy. The route is found here:com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.AddressUpdatesToCustomerServiceRoute.configure. The policy says that for the two defined exceptions, there will be two retries with a delay of 5s between each retry. In addition, logging was configured when the retry has been exhausted. 

```
onException(HttpOperationFailedException.class, SocketTimeoutException.class)
    .handled(true)
    .log(LoggingLevel.ERROR,
        "Failed to patch: ${exception.message}")
    .maximumRedeliveries(2)
    .redeliveryDelay(5000)
    .logExhausted(true)
    .logExhaustedMessageHistory(true)
    .logRetryAttempted(true)
    .end();
```

## Example 7 - Throttle Pattern

The test com.pluralsight.michaelhoffman.camel.foundations.throttle.ThrottleExampleTest gives you an example of throttling the number of messages in a route. The number of messages will be limited to 25 in a 5s time period. 

```
    from("direct:start")
        // Maximum request count
        .throttle(25)
            // Length of time maximum is valid
            .timePeriodMillis(5000)
        .to("log:?level=ERROR&showBody=true", "mock:test");
```

## Example 8 - Slack Notifications

Here is an example of using the Slack component for sending notifications on exception:com.pluralsight.michaelhoffman.camel.foundations.observability.SlackNotificationWebhookExampleTest. Note that you would need to configure your own webhook.

```
onException(AException.class)
    .log(LoggingLevel.ERROR, "A exception")
    .handled(true)
    .to("slack:?webhookUrl=" +
        "https://hooks.slack.com/services/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");
```

## Example 9 - Wire Tap

The following test shows an example of using the Wire Tap enterprise integration pattern to fork a message into a new route:com.pluralsight.michaelhoffman.camel.foundations.observability.WiretapExampleTest. This creates a shallow copy of the exchange. 

```
from("direct:start")
    .wireTap("direct:trace")
    .process(exchange -> {
        log.debug("Processing: " + exchange.getIn().getBody());
    })
    .to("mock:test");

from("direct:trace")
    .process(exchange -> {
        log.debug("Wire tap: " + exchange.getIn().getBody());
    })
    .to("mock:trace");
```

## Example 10 - Jolokia

I've added Jolokia as a dependency on the enhanced customer integration project. Jolokia exposes Camel's management extension beans over HTTP. When you run the container, you should be able to make HTTP calls to surface various data points. For example, the following URL will produce the response below:

http://localhost:8080/actuator/jolokia/read/org.apache.camel:context=*,type=routes,name=*

```
{
  "request": {
    "mbean": "org.apache.camel:context=*,name=*,type=routes",
    "type": "read"
  },
  "value": {
    "org.apache.camel:context=camel-1,name=\"address-updates-to-customer-service-route\",type=routes": {
      "StatisticsEnabled": true,
      "CamelManagementName": "camel-1",
      "EndpointUri": "file://c:/integration-file/in?autoCreate=false&bridgeErrorHandler=true&directoryMustExist=true&include=customer-address-update-.*.csv&move=c%3A%2Fintegration-file%2Farchive",
      "LastProcessingTime": 10595,
      "ExchangesCompleted": 1,
      "ExchangesFailed": 0,
      "Description": null,
      "FirstExchangeCompletedExchangeId": "755A9EBD8BCAF63-0000000000000000",
      "StartTimestamp": "2021-11-13T12:01:05-06:00",
      "FirstExchangeCompletedTimestamp": "2021-11-13T12:16:12-06:00",
      "LastExchangeFailureTimestamp": null,
      "MaxProcessingTime": 10595,
      "LastExchangeCompletedTimestamp": "2021-11-13T12:16:12-06:00",
      "Load15": "",
      "RouteProperties": {
        "template": "false",
        "parent": "3befd03a",
        "rest": "false",
        "description": null,
        "id": "address-updates-to-customer-service-route",
        "customId": "true"
      },
      "DeltaProcessingTime": 10595,
      "OldestInflightDuration": null,
      "ExternalRedeliveries": 0,
      "UptimeMillis": 921946,
      "ExchangesTotal": 1,
      "ResetTimestamp": "2021-11-13T12:01:05-06:00",
      "MeanProcessingTime": 10595,
      "ExchangesInflight": 1,
      "HasRouteController": false,
      "LastExchangeFailureExchangeId": null,
      "LogMask": false,
      "FirstExchangeFailureExchangeId": null,
      "Uptime": "15m21s",
      "CamelId": "camel-1",
      "TotalProcessingTime": 10595,
      "FirstExchangeFailureTimestamp": null,
      "RouteId": "address-updates-to-customer-service-route",
      "RoutePolicyList": "",
      "FailuresHandled": 0,
      "RouteGroup": null,
      "Load05": "",
      "LastError": null,
      "MessageHistory": false,
      "OldestInflightExchangeId": null,
      "State": "Started",
      "MinProcessingTime": 10595,
      "Redeliveries": 0,
      "LastExchangeCompletedExchangeId": "755A9EBD8BCAF63-0000000000000000",
      "Tracing": true,
      "Load01": ""
    }
  },
  "timestamp": 1636827387,
  "status": 200
}
```
## Example 11 - Using the Publish Event Notifier

I've added an example of using the PublishEventNotifier as part of the route:com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.AddressUpdatesToCustomerServiceRoute. This notifier will publish almost all events to an endpoint that I've created. 

```
PublishEventNotifier notifier = new PublishEventNotifier();
notifier.setCamelContext(getContext());
notifier.setEndpointUri("direct:event");
notifier.setIgnoreCamelContextEvents(true);
getContext().getManagementStrategy().addEventNotifier(notifier);

from("direct:event")
    .log(LoggingLevel.ERROR, "EVENT: ${body}");

```

It's important to filter out events that you don't need; otherwise, you'll overload the destination of your events due to the volume. 

## Example 12 - Splitter with Async

I've updated the route here to include parallel processing in the splitter defintion: com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute.AddressUpdatesToCustomerServiceRoute

This will result in Camel using a default thread pool executor to process the split elements asynchronously. 

```
.split(body())
  .parallelProcessing()
```

## Example 12 - Using the Aggregator Pattern

This is an example route for the Aggregator pattern:com.pluralsight.michaelhoffman.camel.foundations.routing.AggregationMessageRoutingExampleTest. This pattern defines three main concepts:

1. What rule(s) should be used to identify the messages to aggregate together
2. What strategy should be used to aggregate the messages
3. When should aggregation be considered complete

For the example, I'm aggregating messages based on the event type in the header. If I had different change event types, like create/update/delete, they can be aggregated separately. I aggregate by collecting the messages into a list. Then every time the aggregation hits a size of 4, the collection is complete and continues through the route as a single exchange. 

```
            from("direct:start")
                .aggregate(header("eventType"), (oldEx, newEx) -> {
                        if (oldEx == null) {
                            List<Integer> elements =
                                new ArrayList<>(newEx.getIn().getBody(Integer.class));
                            newEx.getIn().setBody(elements);
                            return newEx;
                        }
                        List<Integer> elements = oldEx.getIn().getBody(List.class);
                        elements.add(newEx.getIn().getBody(Integer.class));
                        oldEx.getIn().setBody(elements);
                        return oldEx;
                })
                .completionSize(4)
                .log(LoggingLevel.ERROR, "Aggregated body: ${body}")
                .to("mock:test");
```

## Example 13 - Content Based Routing

In the example: com.pluralsight.michaelhoffman.camel.foundations.routing.ContentBasedRouterExampleTest, I'm showing the usage of content-based routing using Camel's choice/when/otherwise defintion. This is similar to Java's if/else if/else construct. This allows for an expression to be evaluated in order to determine where the message gets routed. 

```
from("direct:start")
    .choice()
        .when(simple("${header.eventType} == 'createCustomer'"))
            .to("direct:create")
        .when(simple("${header.eventType} == 'updateCustomer'"))
            .to("direct:update")
        .when(simple("${header.eventType} == 'deleteCustomer'"))
            .to("direct:delete")
        .otherwise()
            .to("mock:test");

from("direct:create")
    .log(LoggingLevel.ERROR, "Create: ${body}")
    .to("mock:testCreate");

from("direct:update")
    .log(LoggingLevel.ERROR, "Update: ${body}")
    .to("mock:testUpdate");

from("direct:delete")
    .log(LoggingLevel.ERROR, "Delete: ${body}")
    .to("mock:testDelete");

```

## Example 14 - Routing Slip

The following test case shows an example of a routing slip:com.pluralsight.michaelhoffman.camel.foundations.routing.RoutingSlipExampleTest. This pattern is useful when message routing rules cannot be statically defined. In the example, a customer object might be a partial or full object. If any part is partially defined, I can have the slip include an endpoint to enrich that portion of the customer. If no enrichment is required, then no additional routing would occur. 

```
from("direct:start")
    .process(exchange -> {
        Customer customer = exchange.getIn().getBody(Customer.class);
        List<String> enrichmentSlips = new ArrayList<>();
        if (customer.getBillingAddress().getAddressLine1() == null) {
            enrichmentSlips.add("direct://enrichBillingAddress");
        }
        if (customer.getShippingAddress().getAddressLine1() == null) {
            enrichmentSlips.add("direct://enrichShippingAddress");
        }
        if (customer.getPrimaryContact().getName() == null) {
            enrichmentSlips.add("direct://enrichPrimaryContact");
        }
        exchange.getIn().setHeader("enrichmentRoutingSlip",
            enrichmentSlips.stream().collect(Collectors.joining(",")));
    })
    .routingSlip(header("enrichmentRoutingSlip"))
    .to("mock:test");

from("direct://enrichBillingAddress")
    .process(exchange -> {
        Customer customer = exchange.getIn().getBody(Customer.class);
        customer.setBillingAddress(new Address(1, "billing address line",
            "billing city", "billing state", "billing postal"));
        exchange.getIn().setBody(customer);
    })
    .log(LoggingLevel.ERROR, "Added billing address: ${body}");

from("direct://enrichShippingAddress")
    .process(exchange -> {
        Customer customer = exchange.getIn().getBody(Customer.class);
        customer.setShippingAddress(new Address(1, "shipping address line",
            "shipping city", "shipping state", "shipping postal"));
        exchange.getIn().setBody(customer);
    })
    .log(LoggingLevel.ERROR, "Added shipping address: ${body}");

from("direct://enrichPrimaryContact")
    .process(exchange -> {
        Customer customer = exchange.getIn().getBody(Customer.class);
        customer.setPrimaryContact(new Contact(1, "contact"));
        exchange.getIn().setBody(customer);
    })
    .log(LoggingLevel.ERROR, "Added contact: ${body}");

```

## Example 15 - Staged Event Driven Architecture (SEDA)

This example shows how SEDA can be used to introduce parallel processing in a route:com.pluralsight.michaelhoffman.camel.foundations.routing.SEDARoutingExampleTest. SEDA acts as an in memory queue and can be configured to support more than one concurrent consumers. 

```
from("direct:start")
    .to("seda:logMessage");

from("seda:logMessage?concurrentConsumers=20")
    .log(LoggingLevel.ERROR, "Message: ${body}")
    // Artificial delay so that you can see the messages
    // asynchronously processed in the log
    .delay(5000)
    .to("mock:test");
```

## Example 16 - Threads

This example shows how a thread pool can be configured for SEDA:com.pluralsight.michaelhoffman.camel.foundations.routing.ThreadsRoutingExampleTest. Configurations include the initial pool size, max pool size and thread name.

```
from("direct:start")
    .to("seda:logMessage");

from("seda:logMessage")
    .threads(5, 20, "test")
    .delay(5000)
    .log(LoggingLevel.ERROR, "Message: ${body}")
    .to("mock:test");
```

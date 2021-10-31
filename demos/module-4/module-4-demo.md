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

The example com.pluralsight.michaelhoffman.camel.foundations.errors.DoTryDoCatchExampleTest is an example of an approach that I covered in the notifications section of the course, but is also relevant to the error handling section. In this example, I'm simply wrapping the .process definition with a doTry and doCatch definition. This simulates Java's try-catch-finally blocks:

```java
@Override
protected RouteBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
        from("direct:start")
            .doTry()
                .process(exchange -> {throw new AException("A");})
            .doCatch(AException.class)
                .process(exchange -> {log.error("A was thrown");})
            .to("mock:test");
        }
    };
}
```

This gives you the opportunity to directly handle a variety of exceptions at any point in the route processing. It also means you are now responsible for handling what should happen to the error. Should the message continue to process? Should the message be logged? Should a notification be sent? Should there ba a retry? 

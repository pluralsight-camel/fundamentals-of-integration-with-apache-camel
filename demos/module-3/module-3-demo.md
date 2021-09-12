# Fundamentals of Integration with Apache Camel - Module 3 Demonstration

This is the demonstration for module 3 of the Fundamentals of Integration with Apache Camel Pluralsight course. In this module, I demonstrate an ETL route that consumes a file of shipping address updates and routes the data to a REST endpoint for a PATCH of the address information.

## Project Setup

1. Java
    1. This project uses the latest version of JDK 11. You can download it from here: https://openjdk.java.net/projects/jdk/11/
2. Submodules
    1. The module customer-integration-before is meant to be the starting point for the demonstration. You should be able to use this project for following along.
    2. The module customer-integration is the completed project at the end of the demonstration. Feel free to use this if you do not plan to code along with the video.
3. Maven
    1. At the root of the project is the maven executable "mvnw". All code is compiled and executed from it. You can also use the latest version of Maven if you prefer. In either case, the maven command is assumed to be on your path when running the demonstrations.
4. IDE
    1. All demonstrations are performed in the most recent version of IntelliJ IDEA Ultimate version. When importing the project, remember to correctly configure the maven runtime and the correct version of the JDK.

## Project Dependencies

In the pom.xml file of each module, I've included several dependencies that are required for the demonstration:

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
            <artifactId>camel-csv-starter</artifactId>
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

* CSV data formatter: https://camel.apache.org/components/latest/dataformats/csv-dataformat.html
* File component: https://camel.apache.org/components/latest/file-component.html
* Jackson JSON data formatter: https://camel.apache.org/components/latest/dataformats/json-jackson-dataformat.html
* REST component: https://camel.apache.org/components/latest/rest-component.html
* Splitter pattern: https://camel.apache.org/components/latest/eips/split-eip.html
* To D dynamic endpoint: https://camel.apache.org/components/latest/eips/toD-eip.html

## Demo 1 Script - Route and Unit Test Creation

1. I've opened my development environment along with two application classes. The first class is named Address Updates to Customer Service Route. This is where I will add the route logic. Since I'm using Spring for this demonstration, one of it's benefits is that I can annotate a Route Builder class as a Spring component. Camel will then automatically take care of loading it and executing it in the Camel context. Just as in the previous route demonstration, I'll start by defining the from and to definition in the configure method.
```
from("")
  .to("");
```

2. If you remember in the last demonstration, Camel provided a default name for my route when it was initialized. It's always a good idea to define a specific name for your routes. As you'll see shortly, the name can then be used for identification in testing or for other purposes such as logging and notifications.
```
from("")
  .routeId("address-updates-to-customer-service-route")
  .to("");
```

3. Since I'm routing from a file of customer addresses and decided to use the file component, I'm going to add it for the endpoint in the from definition.
```
from("file")
  .routeId("address-updates-to-customer-service-route")
  .to("");
```

4. For now, I'm not going to define the file name, path or any other attributes of the component. I just want a placeholder that I can mock out for testing. Next I need to define the to definition of the route. I'm routing content that I've processed from the file to a REST endpoint for a customer account service.
```
from("file")
  .routeId("address-updates-to-customer-service-route")
  .to("rest:patch:customer?host=localhost");
```

5. The URI I entered includes identifying REST as the component name, the method type as patch, the path as customer and the host as a configuration. You may be asking, if you are mocking this endpoint, why can't you just put the component name like in the from definition. Even though I'm mocking the endpoint, Camel will still check if endpoint URI is valid. What I entered was the bare minimum URI required. This is enough to run an initial test of my route. The next step is to implement the test case. I'll open the file Address Updates to Customer Service Route Test.

6. The first step is to annotate the test class with the annotations I described.
```
@MockEndpointsAndSkip("file:.*|rest:.*")
@UseAdviceWith
```

7. The first annotation tells Camel what components to mock, which in our case is the file and rest component. Then the second annotation tells Camel that I want to use "advice with" for enhancing the route. Because I'm using advice with, Camel won't automatically start the route for me. I'll need to auto wire the camel context and call its start method in the test.
```
@Autowired
private CamelContext camelContext;

camelContext.start();
```

8. I want to be able to produce a file to the route, so I'll also auto wire Camel's producer template.
```
@Autowired
private ProducerTemplate producerTemplate;
```

9. In the test method, I can call this producer template's send body method to send any content I want into the route. The benefit is that I control what information is passed to the route and tested. I'll be adding the method shortly when building out the test case. The last member I need on the test class is the mock endpoint. I'll add it now.
```
@EndpointInject("mock://rest:patch:customer")
private MockEndpoint restEndpoint;
```

10. Camel has a mock component that defines the endpoint URI I want to replace. I can then use this rest endpoint test member to make assertions on data it gets passed. Now I need to complete my test case. I want to define how the file gets produced to the route and then I want to define the expectations for the mock that consumes it. Similar to the mock, I'm going to use Camel's "advice with" to replace the "from" definition.
```
AdviceWith.adviceWith(camelContext,
  "address-updates-to-customer-service-route",
    rb -> rb.replaceFromWith("direct:file:start"));
```

11. The static method I've used intercepts the start of my route. It updates the route by replacing the from definition with a direct component. I've also included an arbitrary name for the URI of file colon start. Now I can use the producer template I defined to send a file into the route, but first I need an instance of a file. Let me add that now.
```
@Value("classpath:data/customer-address-update-valid.csv")
private Resource customerAddressUpdateFileValidResource;

GenericFile file = new GenericFile();
file.setFile(customerAddressUpdateFileValidResource.getFile());
```

12. I've provided you a CSV file of the addresses that are being updated. Did you notice that I'm using the type Generic File? This is actually a Camel class for wrapping file processing as part of routing. It's significant because you need to make sure you are producing the same content as the component would; otherwise, you may get different results or errors when you move to integration testing or full route execution. The next step is to add the producer template call.
```
producerTemplate.sendBody("direct:file:start", file);
```

13. The send body method will send the file to the route via the direct component I defined. This is a basic pattern you can follow for most production of content to a route. The last step of the test is to tell Camel what the mock rest endpoint expects. I'll add the expectation and assertion calls now.
```
restEndpoint.expectedMessageCount(1);

restEndpoint.assertIsSatisfied();
```

14. These two lines of code tell Camel that I expect one message to be sent to the rest endpoint and to assert all validations were satisfied. This should be all I need to get a passing test. Let me run the test. I'll open up a terminal window.

15. In the terminal, I've navigated to my project. I'll run the command to execute tests, which is mvnw test.
```
mvnw test
```

16. Here you can see that the test completed successfully and the file was sent through the route. Let's scroll up and review the logs.
```
Enabling auto mocking and skipping of endpoints matching pattern [file:.*|rest:.*] on CamelContext with name [camelContext].
```

17. This line shows Camel applied the "mock endpoints and skip" annotation. Next let's look at how Camel configured the route using advice with.
```
2021-08-15 10:20:22.497  INFO   --- [           main] o.apache.camel.builder.AdviceWithTasks   : AdviceWith replace input from [file] --> [direct:file:start]
2021-08-15 10:20:22.498  INFO   --- [           main] org.apache.camel.builder.AdviceWith      : AdviceWith route after: Route(address-updates-to-customer-service-route)[From[direct:file:start] -> [To[rest:patch:customer?host=localhost]]]
2021-08-15 10:20:22.501  INFO   --- [           main] org.apache.camel.builder.AdviceWith      : Adviced route before/after as XML:
<route xmlns="http://camel.apache.org/schema/spring" customId="true" id="address-updates-to-customer-service-route">
    <from uri="file"/>
    <to uri="rest:patch:customer?host=localhost"/>
</route>
<route xmlns="http://camel.apache.org/schema/spring" customId="true" id="address-updates-to-customer-service-route">
    <from uri="direct:file:start"/>
    <to uri="rest:patch:customer?host=localhost"/>
</route>
2021-08-15 10:20:22.654  INFO   --- [           main] .c.m.InterceptSendToMockEndpointStrategy : Adviced endpoint [rest://patch:customer?host=localhost] with mock endpoint [mock:rest:patch:customer]
```

18. The log shows how Camel replaced the from definition, including displaying the new route definition. Then, the log tells me that the mock of the REST endpoint was successful. If I scroll down, I should see the assertion.
```
Asserting: mock://rest:patch:customer is satisfied
```

19. And that assertion tells me that the exchange message successfully made it through the route. Once I'm finished coding the route, I'll return this test and I'll enhance it to check for actual content. Let's build out the processing steps next. First, I want to show the tracing property I mentioned earlier. I'll open the application test properties file.
```
camel.springboot.tracing=true
```

20. This property will provide me with trace-level details on my route, including the type and contents of the exchange. Its really helpful for supporting initial development as you'll hopefully see shortly. I'll open up the route builder again.

21. The first processing step is to unmarshal the file data that gets produced to the route. In order to accomplish this, I need to tell Camel the format of the file data. I've already defined a Spring bean for this. Let me open the class Integration Config.

22. Here I've defined an instance of the Camel CSV data format. I plan to support a comma for the delimiter and a header on the file, which will be skipped during formatting. Let's go back to the route builder and inject this.

23. I'll add the CSV data formatter as a member of the class and then include it as part of the constructor.
```
private CsvDataFormat csvDataFormatAddressUpdate;

public AddressUpdatesToCustomerServiceRoute(
    @Qualifier("csvDataFormatAddressUpdate") CsvDataFormat csvDataFormatAddressUpdate
) {
    this.csvDataFormatAddressUpdate = csvDataFormatAddressUpdate;
}
```

24. Now I can add the unmarshal definition in the route.
```
.unmarshal(csvDataFormatAddressUpdate)
```

25. Let's open the terminal again and run the test.
```
mvnw test
```

26. I want to call out a section of the logs.
```
2021-08-16 19:58:36.337  INFO   --- [           main] org.apache.camel.Tracing                 : *--> [address-upda] [from[direct:file:start]          ] Exchange[Id: D8959D56E48E44F-0000000000000000, BodyType: org.apache.camel.component.file.GenericFile, Body: [Body
 is file based: GenericFile[null]]]
2021-08-16 19:58:36.346  INFO   --- [           main] org.apache.camel.Tracing                 :      [address-upda] [unmarshal[org.apache.camel.model.] Exchange[Id: D8959D56E48E44F-0000000000000000, BodyType: org.apache.camel.component.file.GenericFile, Body: [Body
 is file based: GenericFile[null]]]
2021-08-16 19:58:36.370  INFO   --- [           main] org.apache.camel.Tracing                 :      [address-upda] [rest:patch:customer?host=localhos] Exchange[Id: D8959D56E48E44F-0000000000000000, BodyType: java.util.ArrayList, Body: 1,1060 W. Addison St.,,Chicag
o,IL,60613]
2021-08-16 19:58:36.374  INFO   --- [           main] org.apache.camel.Tracing                 : *<-- [address-upda] [from[direct://file:start]        ] Exchange[Id: D8959D56E48E44F-0000000000000000, BodyType: java.util.ArrayList, Body: 1,1060 W. Addison St.,,Chicag
o,IL,60613]
```

27. You can see here that the tracing information tells me the exchange ID, the body type and the data inside the body. Its a good way to initially verify that the unmarshal worked as the result was an array list of rows from the file. Next, I'll go back to the route builder and add the remaining steps.

28. If you remember from the design, I need to process each row in the list from unmarshaling. The reason is that my REST endpoint only supports a single customer record. I'll define a splitter. The splitter takes something called an expression. Camel supports a full API for expressions that allow you to perform evaluations against an exchange. For example, you can check if the exchange contains certain text or is of a certain type. In this case, I just need to use an expression to get the body of the exchange message. Let me enter that now.
```
.split(body())
```

29. Camel will now route each row from the file through the remainder of the route definition. Since our REST endpoint doesn't expect a comma-delimited string, I need to transform each row into a request object. I've already created a simple mapper for this called Address Update Line to Customer Mapper. I'll define the processing for that.  
```
.bean(AddressUpdateLineToCustomerMapper.class, "process")
```

30. What I've specified is a bean definition. As the name implies, I can route the exchange to any bean in my container. Camel supports its own bean container, and since I'm using Spring, I also can route to Spring beans. The second parameter passed to the bean definition is a specific method on the mapper class that will be executed by Camel. Let's open the mapper class.

31. If you look at the method signature for the process method, you'll notice the parameter is a list. Is this the parameter type you expected? Or were you expecting a Camel class such as Exchange? One of the benefits of routing to a bean is that Camel will take care of mapping the body of the exchange to the method input for you. This is also a way for you to integrate with other APIs or common libraries. For example, you could have a common library to send notifications to Slack and have your routes use bean definitions for calling the API. Let's return to the route and finish the processing logic.

32. To recap the processing, Camel will unmarshal the CSV data from a file using a CSV data formatter, split the lines to be processed individually and each line will be mapped to a customer object. I now need to marshal the object into JSON format so that it can be sent to the REST endpoint. I'll add the code now.
```
.marshal().json()
```

33. I've defined marshalling using a JSON formatter. Camel will use the Jackson library to perform the formatting as I included it as a project dependency. Let's run the test again to see the results.
```
2021-08-18 18:48:21.623  INFO   --- [           main] org.apache.camel.Tracing                 : *--> [address-upda] [from[direct:file:start]          ] Exchange[Id: C4F5910865290A0-0000000000000000, BodyType: org.apache.camel.component.file.GenericFile, Body: [Body is file based: GenericFile[null]]]
2021-08-18 18:48:21.626  INFO   --- [           main] org.apache.camel.Tracing                 :      [address-upda] [unmarshal[org.apache.camel.model.] Exchange[Id: C4F5910865290A0-0000000000000000, BodyType: org.apache.camel.component.file.GenericFile, Body: [Body is file based: GenericFile[null]]]
2021-08-18 18:48:21.633  INFO   --- [           main] org.apache.camel.Tracing                 :      [address-upda] [split[simple{${body}}]           ] Exchange[Id: C4F5910865290A0-0000000000000000, BodyType: java.util.ArrayList, Body: 1,1060 W. Addison St.,,Chicago,IL,60613]
2021-08-18 18:48:21.639  INFO   --- [           main] org.apache.camel.Tracing                 : *--> [address-upda] [from[direct:file:start]          ] Exchange[Id: C4F5910865290A0-0000000000000001, BodyType: java.util.ArrayList, Body: 1,1060 W. Addison St.,,Chicago,IL,60613]
2021-08-18 18:48:21.639  INFO   --- [           main] org.apache.camel.Tracing                 :      [address-upda] [bean[com.pluralsight.michaelhoffm] Exchange[Id: C4F5910865290A0-0000000000000001, BodyType: java.util.ArrayList, Body: 1,1060 W. Addison St.,,Chicago,IL,60613]
2021-08-18 18:48:21.659  INFO   --- [           main] org.apache.camel.Tracing                 :      [address-upda] [marshal[org.apache.camel.model.da] Exchange[Id: C4F5910865290A0-0000000000000001, BodyType: com.pluralsight.michaelhoffman.camel.customer.integration.common.dto.Customer, Body: Customer{id=1, addressLine1='1060 W. Addison St.', addressLine2='', city='Chicago', state='IL', postalCode='60613'}]
2021-08-18 18:48:21.705  INFO   --- [           main] org.apache.camel.Tracing                 :      [address-upda] [rest:patch:customer?host=localhos] Exchange[Id: C4F5910865290A0-0000000000000001, BodyType: byte[], Body: {"id":1,"addressLine1":"1060 W. Addison St.","addressLine2":"","city":"Chicago","state":"IL","postalCode":"60613"}]
2021-08-18 18:48:21.708  INFO   --- [           main] org.apache.camel.Tracing                 : *<-- [address-upda] [from[direct://file:start]        ] Exchange[Id: C4F5910865290A0-0000000000000000, BodyType: java.util.ArrayList, Body: 1,1060 W. Addison St.,,Chicago,IL,60613]
```

34. Here you can see the trace of each step in the route and the input to the step. The unmarshal step accepts a generic file and returns a list. This list becomes the input to the splitter, which will separate each line into its own sub-route. The mapper bean accepts the list of data from the line and returns a customer object. Finally, the REST call is made with a JSON string representing the customer to be patched. Let's open the route again.

35. The processing logic is now in place, but I'm still using mocks for the from and to definitions. Let's address this.


## Demo 2 Script - Integration Development and Testing

1. I've opened up the route builder class. I'll be replacing the from definition with a configured endpoint.
```
from("file:{{app.addressToCustomerRoute.directory}}" +
        "?include={{app.addressToCustomerRoute.includeFile}}" +
        "&move={{app.addressToCustomerRoute.moveDirectory}}")
```

2. The bind variables all point to properties found in the application properties file under the project resources folder. Again, the benefit of externalizing these values is that I can change them in each environment I deploy to. Another benefit is that your automated integration tests can be configured to use a different directory path and file name than your normal runtime. At this point, its a good idea to revisit the unit test and make sure it still runs successfully. I'll open a terminal window now.

3. Let's run the test case.
```
mvnw test
```

4. The test ran successfully as I expected.
```
2021-08-22 08:09:13.753  INFO   --- [           main] o.apache.camel.builder.AdviceWithTasks   : AdviceWith replace input from [file:{{app.addressToCustomerRoute.directory}}?include={{app.addressToCustomerRoute.includeFile}}&move={{app.addressToCustomerRoute.moveDire
ctory}}] --> [direct:file:start]
```

5. As you can see in the log here, Camel's advice with was still able to replace the matching from definition after changing the endpoint. Let's go back to the route builder class.

6. Now I have address the "to" definition of the route. The route destination is a REST controller that I've included in the project. Let me replace the "to" definition.
```
.toD("rest:patch:customer/${exchangeProperty.customerId}?host={{app.customer-service.host}}");
```

7. To re-iterate, I need to dynamically change the endpoint at run-time, which isn't supported with the common to definition. I'm going to use "to d" in order to support this. I've included a bind variable for the host parameter. The endpoint path is customer, slash, customer ID. You may be asking, how does this customer ID get set? What is an exchange property? As I've described earlier, the input to each step of the route is mapped to the output from the previous step. Given this, I need a way to store data that is available across multiple steps of the route. An exchange property is meta-data that is available across route processing. Let me add the setting of this property now.
```
.bean(AddressUpdateLineToCustomerMapper.class, "process")
.setProperty("customerId", simple("${body.id}"))
.marshal()
```

8. What this line of code does is set a property on the exchange with the key as customer ID. The value uses a simple expression to get the customer ID attribute off the message body. ID is an attribute on the message created by the bean definition in the previous line. In the dynamic to definition, I'm again using a simple expression to access the exchange property using the key name. Now that both endpoints are configured, let's again go back to a terminal and run the unit tests.

9. I'll execute a maven test command.
```
mvnw test
```

10. The test seems to have failed. If I scroll up, I'll see the error.
```
[ERROR] Failures:
[ERROR]   AddressUpdatesToCustomerServiceRouteTest.route_testValid:55 mock://rest:patch:customer Received message count. Expected: <1> but was: <0>
```

11. It seems I didn't get the number of messages expected. But is that the real root cause of the error? Let's scroll up some more in the logs.
```
2021-08-22 08:25:30.173  INFO   --- [           main] .c.m.InterceptSendToMockEndpointStrategy : Adviced endpoint [rest://patch:customer/1?host=http://localhost:8080] with mock endpoint [mock:rest:patch:customer/1]
2021-08-22 08:25:30.654  INFO   --- [           main] org.apache.camel.Tracing                 : *<-- [address-upda] [from[direct://file:start]        ] Exchange[Id: 0FD066CB0ED50EC-0000000000000000, BodyType: java.util.ArrayList, Body: 1,1060 W. Addison St.,,Chicago
,IL,60613]
2021-08-22 08:25:30.655  INFO   --- [           main] o.a.camel.component.mock.MockEndpoint    : Asserting: mock://rest:patch:customer is satisfied
2021-08-22 08:25:40.671  WARN   --- [           main] o.a.camel.component.mock.MockEndpoint    : The latch did not reach 0 within the specified time
```

12. Here you can see the real issue. The endpoint path changed to include an ID, and as a result the path wasn't matched to be mocked. I've also switched to use a dynamic to definition, which means I need to change how I code my unit test. Let's open the test class up again.

13. The problem with my test is the endpoint inject annotation doesn't match the dynamic route. I need to change the strategy for mocking the REST endpoint. I'll first remove the endpoint inject and mock endpoint.
```
@EndpointInject("mock://rest:patch:customer")
private MockEndpoint restEndpoint;
```

14. Rather than having it as a test member, I'll just create a local variable to retrieve the mock from the camel context.
```
GenericFile file = new GenericFile();
file.setFile(customerAddressUpdateFileValidResource.getFile());

MockEndpoint restEndpoint =
    camelContext.getEndpoint("mock://rest:patch:customer", MockEndpoint.class);
```

15. This allows me to retrieve the mock, but how do I tell Camel to match the dynamic endpoint? To do this, I'll use advice with, similar to how I replaced the from defintion.
```
AdviceWith.adviceWith(camelContext, "address-updates-to-customer-service-route",
    rb -> rb.replaceFromWith("direct:file:start"));

AdviceWith.adviceWith(camelContext, "address-updates-to-customer-service-route",
    rb -> rb.weaveByType(ToDynamicDefinition.class).replace().toD("mock://rest:patch:customer"));
```

16. This line of code will tell Camel to replace any dynamic to definition in the route with a mocked dynamic to definition. This should be all that's needed to fix the test. Let's open a terminal and try it.

17. I'm going run the test case again.
```
mvnw test
```

18. If I scroll up in the log, I can see that advice with worked and my mock assertion succeeded.
```
2021-08-22 10:34:44.742  INFO   --- [           main] org.apache.camel.Tracing                 :      [address-upda] [mock://rest:patch:customer       ] Exchange[Id: 9F12406401A97D6-0000000000000001, BodyType: byte[], Body: {"id":1,"addr
essLine1":"1060 W. Addison St.","addressLine2":"","city":"Chicago","state":"IL","postalCode":"60613"}]
2021-08-22 10:34:44.748  INFO   --- [           main] org.apache.camel.Tracing                 : *<-- [address-upda] [from[direct://file:start]        ] Exchange[Id: 9F12406401A97D6-0000000000000000, BodyType: java.util.ArrayList, Body:
1,1060 W. Addison St.,,Chicago,IL,60613]
2021-08-22 10:34:44.748  INFO   --- [           main] o.a.camel.component.mock.MockEndpoint    : Asserting: mock://rest:patch:customer is satisfied
```

19. My purpose for showing you the dynamic endpoint as a separate step was to ease you in to a different way of routing to a destination. Likely you would have implemented unit tests to support the dynamic endpoint earlier as part of initial development. Its also important to note that starting with mocks hid the fact that I was missing part of the path on my REST endpoint. Mocks are great for starting development, but this reinforces the importance of actual integration testing earlier in development to catch these gaps. The last step in this demo is to try actually executing the route in full. Let's open a few terminal windows.

20. In the first terminal, I'll run the project using mvnw space exec colon java.
```
mvnw exec:java
```

21. The log says the route is started and listening for files at the path c colon slash integration dash file slash in. You will need to make sure this path and the archive path in the properties file exist first before running the command. I'll open the second terminal window.

22. In this window, I'll copy a test file from my project's resources directory using the windows command copy.
```
copy customer-integration\src\test\resources\data\customer-address-update-full.csv c:\integration-file\in
```

23. Now I'll go back to the camel runtime terminal.

24. In the log I see all three patch requests as a result of processing the file:
```
2021-08-22 11:06:13.253 DEBUG 17612 --- [nio-8080-exec-1] c.p.m.c.c.i.c.c.CustomerController       : Received customer request patch: Customer{id=1, addressLine1='1060 W. Addison St.', addressLine2='', city='Chicago', state='IL', postalC
ode='60613'}
2021-08-22 11:06:13.288 DEBUG 17612 --- [nio-8080-exec-2] c.p.m.c.c.i.c.c.CustomerController       : Received customer request patch: Customer{id=2, addressLine1='120 E. 76th St.', addressLine2='Suite 200', city='Chicago', state='IL', po
stalCode='60613'}
2021-08-22 11:06:13.296 DEBUG 17612 --- [nio-8080-exec-4] c.p.m.c.c.i.c.c.CustomerController       : Received customer request patch: Customer{id=3, addressLine1='220 E. 77th St.', addressLine2='Apt 402', city='Chicago', state='IL', post
alCode='60614'}
```

25. It looks like I have a successfully running Camel route. That completes the demonstration.

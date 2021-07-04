# Fundamentals of Integration with Apache Camel - Module 2 Demonstration

This is the demonstration for module 2 of the Fundamentals of Integration with Apache Camel Pluralsight course. In this module, I demonstrate a simple route using the Camel console component.

## Project Setup

1. Java
    1. This project uses the latest version of JDK 11. You can download it from here: https://openjdk.java.net/projects/jdk/11/
2. Submodules
    1. The module route-to-console-before is meant to be the starting point for the demonstration. You should be able to use this project for following along. 
    2. The module route-to-console is the completed project at the end of the demonstration. Feel free to use this if you do not plan to code along with the video. 
3. Maven
    1. At the root of the project is the maven executable "mvnw". All code is compiled and executed from it. You can also use the latest version of Maven if you prefer. In either case, the maven command is assumed to be on your path when running the demonstrations. 
4. IDE
    1. All demonstrations are performed in the most recent version of IntelliJ IDEA Ultimate version. When importing the project, remember to correctly configure the maven runtime and the correct version of the JDK.  

## Project Dependencies

In the pom.xml file of each module, I've included several dependencies that are required for the demonstration:

* Core Camel library: 
    ```xml
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-core</artifactId>
            <version>${camel.version}</version>
        </dependency>
    ```
* Camel library to support running Camel on the command line: 
    ```xml
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-main</artifactId>
            <version>${camel.version}</version>
        </dependency>
    ```
* Camel library for the Stream component:
    ```xml
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-stream</artifactId>
            <version>${camel.version}</version>
        </dependency>
    ```

## Running the Project

I've included the Camel Maven plugin to support directly executing the application. Simply run the following command from the root of the module:

```
mvn camel:run
```

## Project Links

* Stream component: https://camel.apache.org/components/latest/stream-component.html
* Main: https://camel.apache.org/components/3.7.x/others/main.html

## Demo Script

1. I've opened my development environment as well as the application class, Route to Console Application, where I'll be adding the route.  
2. The first step is to initialize Camel's standalone run-time. To do this, I'll create an instance of Camel's Main class and call its run behavior. 
```
import org.apache.camel.main.Main;

Main main = new Main();

main.run(args);
```
3. It's not common for me to run Camel stand-alone, but if you need to run Camel from a command line, it is supported. The main class will start up Camel's container and initialize its context. 
4. Next, I need to add the route.  
```
    main.configure().addLambdaRouteBuilder(
            
    ); 
```
5. Route Builder provides Camel's Fluent DSL for defining the route. This code adds the route to the camel context. 
6. I'm going to start by adding in the from, process and to definitions that I explained earlier. 
```
    rb -> rb
        .from()
        .process()
        .to()        
```
7. The route defines where Camel should route data from, how to process it and where it should route data to. For this route, I want to accept input from the console, change the data and then return output to the console. How can I accomplish this? I need to use Java's system input stream and system output stream. Camel just so happens to have a corresponding component named Stream. Let me add it now. 
```
from("stream://in")

to("stream://out")
```
8. You may be wondering what the strings are that I just added. On the left side of the colon is the name of the component, in this case stream. If you were using Camel's file component, you would expect to see the word "file" on the left. This string is a URI. All route definitions use this same, standard URI pattern. Note that the forward slashes are optional. To the right of the forward slashes, I've defined a path. I've specified "in" for the "from" definition and "out" for the "to" definition. This just says route "from" system in and route "to" system out. Just having a prompt can be confusing, so how do I add text to the prompt? It can be accomplished by using query parameter options in the URI. 
```
from("stream:in?promptMessage=What should I repeat: ")
```
9. Every component supports configuration through URI query parameters. The parameter added tells the stream component to prompt a text message as part of system in. This addresses the "from" and "to" definitions, but now I want to process the input message. I'm going to add code to the process definition that enhances the text typed by the user. 
```
(exchange) ->
    exchange.getIn().setBody("You said: " + exchange.getIn().getBody(String.class))
```
10. I've introduced a new concept here called the exchange. An exchange transports data through the route. The exchange contains a message with a header and body. When a user types text in the console, that text is stored in the body of a message. The message is then sent to the processor as part of an exchange. 
11. That completes the route. Next I'm going to start the application and try it out.
```
Add a break here
```
12. I've opened a terminal in IntelliJ and navigated to the root of my project. I've already compiled the code. Because I'm using the Camel Maven plugin, I can run the command mvnw camel colon run. 
```
mvnw camel: run
```
14. The app started successfully and the prompt is displayed as I was expecting. Let's take a moment to look at the log for startup. I mentioned earlier, Camel starts up a CamelContext. At startup, Camel will tell me the route or routes that were initialized. I didn't provide a unique name, so Camel just called this route1. 
15. I'll enter some text and should see the message get repeated back to me. 
16. Once I hit enter, the message was streamed into my route, the processor added the text you said and then the route output the full message back to system out. 
17. As a last step, I'll hit control c and terminate the route. Here the log shows that Camel has successfully stopped the route.  
18. I chose this type of route because I thought it was better than the traditional hello world example. If you'd like to do some additional exploration, try extending this example to behave like a CLI would. For instance, have your processor check the type of command entered and respond appropriately.
19. Let's revisit some of the concepts I introduced in this route. 

# Fundamentals of Integration with Apache Camel - Module 5 Demonstration

Module 5 focuses on event notification with Apache Camel.  

## RabbitMQ Username and Password

The username and password for the RabbitMQ configuration are both: travelintegrationservice

## Project Setup

1. Java
    1. This project uses the latest version of JDK 11. You can download it from here: https://openjdk.java.net/projects/jdk/11/
2. Submodules
    1. The sub-modules simple-service and simple-integration-service provide a simple example of a Camel route using RabbitMQ
    2. The sub-module rabbitmq-docker provides the docker file for running RabbitMQ to support all examples. 
    3. The remaining sub-modules support the travel integration scenarios. 
3. Maven
    1. At the root of the project is the maven executable "mvnw". All code is compiled and executed from it. You can also use the latest version of Maven if you prefer. In either case, the maven command is assumed to be on your path when running the demonstrations.
4. IDE
    1. All demonstrations are performed in the most recent version of IntelliJ IDEA Ultimate version. When importing the project, remember to correctly configure the maven runtime and the correct version of the JDK.
5. Docker
   1. To run the project, you will need to have the latest version of Docker installed. 

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

The following project file contains the docker file for running RabbitMQ: module-5/travel-integration-service/src/main/docker/rabbitmq.yml

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


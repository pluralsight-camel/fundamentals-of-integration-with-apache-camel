# Fundamentals of Integration with Apache Camel - Module 6 Demonstration

Module 6 focuses on event streaming using Apache Kafka.

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
    1. To run the Kafka broker server and Kafka Connect, you will need to have the latest version of Docker installed.
6. Kafka
    1. The latest versions of Kafka 2.x were used for the demonstrations. 

## Project Dependencies

The following was a dependency added to the project for supporting Kakfa routing.

```
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-kafka-starter</artifactId>
</dependency>
```

## Running the Project

I've included the Camel Maven plugin to support directly executing the application. Simply run the following command from the root of the module:

```
mvnw exec:java
```

## Running Kafka and Kafka Connect

There are three Docker runtimes used, one in each demonstration:

1. Simple example using Orders and a single broker: demos/module-6/kafka-docker/src/main/docker/orders/kafka.yml
2. Complex example using Fraud and multiple brokers: demos/module-6/kafka-docker/src/main/docker/fraud/kafka-multi-broker.yml
3. Kafka Connect example using Orders, a single broker and the Camel HTTP Sink component: demos/module-6/kafka-docker/src/main/docker/orders-connect/kafka-connect.yml

To start each of these, execute the command with the correct docker compose file name:

```
docker compose -f kafka.yml up
```

Then to shut down the Kafka container(s), execute the command:

```
docker compose -f kafka.yml down -v
```

Note, the -v option is important as Kafka will store offsets in a volume that persists past teardown. Otherwise, the docker files were scripted to not persist data from the broker after shut down. This assures a clean environment each time the container is started.

For the Kafka Connect example, I've built an image that copies the Camel HTTP Sink component into a directory. I have run into an occasional issue during this process. If needed, you can easily remove the image by stopping all containers and running the command with the image ID:

```
docker rmi <IMAGE_ID>
```

## Project Links

* [Camel Kafka Component](https://camel.apache.org/components/3.14.x/kafka-component.html)
* [2021 Refactoring of Camel Kafka Component](https://camel.apache.org/blog/2021/09/camel-kafka-consumer-changes/)
* [Camel Kafka Connect](https://camel.apache.org/camel-kafka-connector/1.0.x/)
* [List of Camel Kafka Connector Libraries](https://camel.apache.org/camel-kafka-connector/1.0.x/reference/index.html)
* [Kafka Connect Docker Compose Example](https://github.com/confluentinc/demo-scene/blob/master/kafka-connect-zero-to-hero/docker-compose.yml)

## Demo 1 - Simple Example with Camel Kafka Component

The first demonstration implements a design for holding orders in a queue to be worked by customer service reps. The process flow is:

1. The order is created by an order service, which then fires an event to an order integration service, which publishes the event for all subscribers. 
2. An order hold queue integration service subscribes to the message and posts the event to an order hold service for processing.  

To run the demonstration, follow these steps:

1. Start the Kafka broker. You can do this with a docker compose command using the file: demos/module-6/kafka-docker/src/main/docker/orders/kafka.yml. The configuration for the broker uses port 9092. Also note the container "topics". This will run the command found in this file: demos/module-6/kafka-docker/src/main/docker/orders/kakfa/create-topics-order.sh. The command will create a topic with the name "orders" in the broker. This should be all you need to get Kafka running. 
2. Next, you'll need to run each of the four service projects:
   1. orders-domain-service - Runs on port 8081
   2. orders-integration-service - Runs on port 8082
   3. orders-held-queue-integration-service - Runs on port 8083
   4. orders-held-queue-service - Runs on port 8084. 
3. Once all the servers are running, you should be able to post a message to the orders domain service endpoint:
    ```
    curl -X POST http://localhost:8081/orders
   -H 'Content-Type: application/json'
   -d '{"orderNumber":3, "itemNumber":10, "customerNumber":1000, "eventType":"order.event"}'
    ```
4. If the request is successful, you should see log messages for each integration service producing/consuming the message and the message being posted to the order held queue service.

## Demo 2 - Complex Example Using Camel Kafka Component

This demonstration uses the example of producing a stream of transactions for fraud detection processing. The transactions are sourced from a file. 

To run the demonstration, follow these steps:

1. Start the Kafka broker, similar to the previous demonstration. Use the docker compose file: demos/module-6/kafka-docker/src/main/docker/fraud/kafka-multi-broker.yml. This will create a topic for customer transactions. 
2. Start the fraud detection engine. The engine will read files from the directory c:/integration-file/in. Two example files are provided in the path: demos/module-6/fraud-detection-engine/data. 
3. After you copy the file to the folder, the engine should pick it up using the Camel file component. Each line will be sent to the broker as a message. The fraud detection engine has two routes, one for production and one for consumption:
   1. com.pluralsight.michaelhoffman.camel.fraud.route.TransactionIngestionProducerRoute
   2. com.pluralsight.michaelhoffman.camel.fraud.route.TransactionIngestionConsumerRoute
4. If the route was successful, you should see all messages consumed in the logs. 

## Demo 3 - Camel Kafka Connector

This demonstration uses the same example from demo 1, but replaces the order hold queue integration service with Kafka Connect. Note that I've also used a mock server so that the entire example runs within Docker.

To run the demonstration, follow these steps:

1. Start the Kafka broker and connect server, similar to the previous demonstrations. Use the docker compose file: demos/module-6/kafka-docker/src/main/docker/orders-connect/kafka-connect.yml. 
2. Once the Kafka Connect server has started, you will need to post the configuration to the Connect server endpoint with the body content from the file: demos/module-6/kafka-docker/src/main/docker/orders-connect/OrdersHttpSinkConnector.json:
   ```
    curl -X POST http://localhost:8083/connectors
   -H 'Content-Type: application/json'
   -d '<REPLACE WITH CONTENTS FROM FILE demos/module-6/kafka-docker/src/main/docker/orders-connect/OrdersHttpSinkConnector.json>'
    ```
3. Start the order services:
   1. orders-domain-service - Runs on port 8081 
   2. orders-integration-service - Runs on port 8082
4. Now post the same message to the orders service as in the first demo:
    ```
    curl -X POST http://localhost:8081/orders
   -H 'Content-Type: application/json'
   -d '{"orderNumber":3, "itemNumber":10, "customerNumber":1000, "eventType":"order.event"}'
    ```
5. The mock server running on port 8084 should receive the message and return a successful response. 

## Opportunity for Learning

The fraud detection engine is an interesting use case for Camel and Kafka. I highly recommend extending it as a learning experience. Here's some ideas for enhancement:

1. Add a Kafka Connect source connectors to consume transactions from other sources, such as a database of customer transactions. 
2. Add a stream for producing attributes of a fraudulent transaction so that they can be captured as "negatives". Negatives are known fraud, like a customer name or a payment card. 
3. Consume from the stream of negatives to spider through all transactions to find fraudulent historical transactions that may have been missed during initial processing. 

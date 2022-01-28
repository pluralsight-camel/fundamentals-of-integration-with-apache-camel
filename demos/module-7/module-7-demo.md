# Fundamentals of Integration with Apache Camel - Module 7 Demonstration

Module 7 focuses on running Camel K on Kubernetes 

## Project Setup

1. Docker
   1. To run the examples, you will need to have the latest version of Docker installed.
2. Minikube
   1. There are two options for using minikube. If you are running Docker desktop as I did, you can enable it directly in the Docker application settings. To install minikube, I followed the installation here: https://minikube.sigs.k8s.io/docs/start/. Note that the course shows running in Windows 10; however, choose the installation that best fits your scenario.

The module demos were based on the following versions:

* Camel K Client = 1.8.0
* Camel K Runtime = 1.11.0
* Camel Framework = 3.14
* Minikube = v1.17.1
* Docker Desktop for Windows = 4.4.4  
* Docker Engine = v20.10.12

## Installing Camel K

To install Camel K, follow these steps:

1. Download the latest version of the Camel K client from the releases page: https://github.com/apache/camel-k/releases. Make sure to grab the client install for your specific environment.
2. Extract the client from the download and update your system environment variable to add the directory on your path. 
3. With Docker and Minikube running, execute the following command to install the container registry used by Camel K:
    ```
    minikube addons enable registry
    ```
4. Finally, run this command to install the Camel K operator:
    ```
    kamel install
    ```
5. You should see the message, "Camel K installed in the namespace default". You can then run the following command to verify the operator is available by checking the status is "running": 
    ```
    kubectl get pod
    ```

## Initialize a Route with Camel K

To create a new route for running in Camel K, you can use the following command: 

```
kamel init MyRoute.java
```

The route logic will be generated based on the file extension, so if you wanted to use Yaml, just change the extension to .yaml. 

## Initialize a Kamelet with Camel K

To create a kamelet, you can use the same command as creating a route for camel K using the following: 

kamel init my-name-source.kamelet.yaml

## Project Links

* [Camel K Documentation - Camel Website](https://camel.apache.org/camel-k/1.7.x/index.html)
* [Camel K Releases](https://github.com/apache/camel-k/releases)
* [Camel K repository](https://github.com/apache/camel-k)

## Demo 1 - Basic Overview of the Camel K CLI

1. I've opened up powershell, I have minikube running and I've installed Camel K. I'll run a command to make sure the operator is running, kube control get pod: 
    ```
    kubectl get pod
    ```
2. The output shows the camel k operator is in fact running, so I should be able to deploy my route. I've navigated to the directory of my route. The route I'm running is named HelloWorldRoute and you can find it in the root package of the project. Let's take a quick look at the contents of the route. 
    ```
    Get-Content .\HelloWorldRoute.java
    ```
3. The hello world route starts with a timer that fires every second. As part of processing, the message hello world is sent in the body. Let's execute the route. The command I'll use is, camel with a k, run, hello world route dot java, then two dashes with the option as dev. 
    ```
    kamel run HelloWorldRoute.java --dev
    ```
4. I'll stop the route with control c so that we can look at the log output.  
   1. In the first line, the operator logs that an integration was created. Think of an integration as the specification for the route application, including its source, dependencies and configuration. 
   2. At this line, the integration is being built by the operator. The integration goes through several states before starting to actually execute the route. 
   3. These lines show you see all the dependencies that the operator has identified and brought in for you. 
   4. Finally, at this line, the route begins to execute by printing out hello world every second. What you just saw was how quickly you can get a Camel route executing in a container with Camel K. 
5. Let's start the route again by running the kamel run command
    ```
    kamel run HelloWorldRoute.java --dev
    ```
6. You may be wondering, what does the dev option mean? Including it will still result in a full production build, but it will allow you to make live updates for iteratively building and testing your routes. Let's open the hello world route that we executed in an editor. 
7. Let's change the message from hello world to Goodbye world and save the file. Now let's look at the command window again. 
8. I'll stop the processing with control c and then scroll up in the log. 
   1. Here is where the Camel K operator re-loaded our route by building it again and then executing it. 
   2. Here is the line with our updated text in the body of the message. If I wanted to, I could continue making changes to the route and verifying them without having to fully deploy the integration. This is a nice feature for verifying your route and testing it. 
9. Running routes with Camel K is highly configurable. For example, build time configurations can be used to affect how integrations are built. More commonly, you'll want to use properties during the execution of your routes. Properties can be provided both on the command and through a file. I'm going to show you the route named Hello World Route Property.
    ```
    Get-Content .\HelloWorldRouteProperty.java
    ```
10. Let's look at the details for this route. 
     1. For the timer component, I have a property for how frequently the timer should file. This will be specified in a properties file. 
     2. The processor then includes properties. One will be defined on the command line and one in the properties file. Let's look at that file now.
    ```
    Get-Content .\helloworld.properties
    ```
11. The property file includes two of the runtime configurations. Now let's execute this route. The command I'll use is similar to the previous one, using kamel run. I'll specify two arguments with dash, dash, property, one with the file and one with the key value pair. The file needs to be prefixed with file colon. Let's run the command. 
    ```
     kamel run --property file:helloworld.properties --property person.message=Michael HelloWorldRouteProperty.java --dev
    ```
12. I'll stop the route again. Here you can see in the log that every five seconds, I'm getting the message I expected from the properties. In addition to build and runtime properties, Camel K will also support you for using Kubernetes config maps to define properties, such as for secrets.  
13. Now that I'm comfortable with the first hello world route in development mode, let's create the integration in full. I'll run the command kamel run without the dash, dash, dev option
    ```
    kamel run HelloWorldRoute.java
    ```
14. I got back a message saying that the integration was successfully created, so let's look at a few other commands. First, let's take a look at the log. I'll enter the command kamel log and the integration name hello, dash, world, dash, route:
    ```
    kamel log hello-world-route
    ```
15. This is going to tail the route. Now if I want to stop and delete the integration, I can run the command kamel delete and the integration name: 
    ```
    kamel delete hello-world-route
    ```
16. If I run the command kamel get, I should no longer see the integration: 
    ```
    kamel get 
    ```
17. Along with configuration properties, Camel K provides you with something like thirty five categories for customizing the behavior of the integration being built. These customizations are called traits. They are key value pairs added to the argument dash, dash, trait. Let's look at one to customize logging to be output in JSON format. I'll use the same kamel run command with my route and the trait logging dot json set to true. 
    ```
    kamel run --trait logging.json=true HelloWorldRoute.java
    ```
18. The integration is created and I can run the command kamel log hello, dash, world, dash, route to see the log is now in JSON format. 
    ```
    kamel log hello-world-route
    ```
19. Here you see the log messages are now in JSON format. Again, this is just one of the many traits available, but it demonstrates how you have control over the customization of an integration. Up until now, I've been showing you existing routes, but how do you get started with Camel K? I'll show you one more command called init. Let's run the command kamel init Hello World Test Route dot yaml. 
    ```
    kamel init HelloWorldTestRoute.yaml
    ```
20. A route should have been generated for you. I'll open it now. 
    ```
    Get-Content .\HelloWorldTestRoute.yaml 
    ```
21. Here you can see the route generated in yaml. I can use this to kickstart building a route. Hopefully this provided you with a complete example of using the Camel K C, L, I for deploying your routes to Kubernetes. Let's summarize what we just covered. 

## Demo 2 - Implementing Kamelets in a Route

1. Let's start by looking at an actual Kamelet specification. I'll open the file timer dash source dot kamelet dot yaml.
   ```
    Get-Content .\timer-pluralsight-source.kamelet.yaml
   ```
2. This is a specification that I generated and modified using the same kamel init command I showed you earlier. The init command generates either a kamelet or a route based on the structure of the file name. In this case, the structure included source, because it's a source flow type, and kamelet dot yaml because I want a kamelet specification generated. After I generated the file, I made a few small updates to it. Let's look at the key properties. 
   1. The line metadata, name is considered the Kamelet ID. The name will be used as part of the URI in my route to identify the kamelet I want to use.  
   2. In the spec, definition section, I've defined two properties, which again will be used in the route URI. 
   3. The flow section is where I define the steps of the source route. Essentially what I'm doing here is wrapping the timer component and adding a step to set the body. This is really the meat of the Kamelet. While this example is simplistic, you could imagine any of our previous routes and integration patterns being part of this Kamelet. Note that the properties I've defined in the spec, definition section are being used as bind variables in the parmameters and steps. 
3. While there are more sections to the specification, that should hopefully give you a high level understanding of what a Kamelet looks like. Now, how do you use this? I have to install the kamelet on a Kubernetes namespace in order for it to be used by other integration. The command I'll use is cube control, apply, dash f and the file name of my kamelet: 
   ```
    kubectl apply -f .\timer-pluralsight-source.kamelet.yaml
   ```
4. From the output, we can see this line notes that the kamelet was successfully added. 
5. Next, let's look at the route that will use the kamelet. I'll open the route timer, kamelet, route: 
   ```
   Get-Content .\TimerKameletRoute.java
   ```
6. The key part of this route is the reference to my kamelet. I'm using the component name as kamelet, the time, dash, pluralsight, dash, source ID that I specified in the kamelet spec and the two properties I configured. Let's try running this route with Camel K: 
   ```
   kamel run .\TimerKameletRoute.java --dev
   ```
7. I'll stop the execution. The output here shows that my route is successfully using my kamelet, firing the timer every five seconds and outputting the message Hello. With minimal effort, I was able to create a kamelet, implement it in a route and then deploy it out to kubernetes. Let's look at a few more details regarding Camel K and Kameletes.   

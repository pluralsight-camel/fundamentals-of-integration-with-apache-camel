package com.pluralsight.michaelhoffman.camel.routetoconsole;

import org.apache.camel.main.Main;

public class RouteToConsoleApplication {

    /**
     * Main method for running the application
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Provides support for running Camel routes from the command line
        Main main = new Main();

        // Adds one or more routes to the runtime
        main.configure().addLambdaRouteBuilder(
            rb -> rb.
                // From definition is using the Stream component for receiving input
                from("stream://in?promptMessage=What should I repeat: ")
                // Defines processing of the message
                .process(
                    (exchange) ->
                        exchange.getIn().setBody("You said: " + exchange.getIn().getBody(String.class))
                )
                // To definition is using the Stream component for streaming output
                .to("stream://out")
        );

        // Starts the Camel runtime and loops until terminated
        main.run(args);
    }
}

import org.apache.camel.builder.RouteBuilder;

public class HelloWorldRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer:helloWorldTimer?period=1000")
            .process(exchange -> exchange.getIn().setBody("Hello World!"))
            .log("${body}");

    }

}

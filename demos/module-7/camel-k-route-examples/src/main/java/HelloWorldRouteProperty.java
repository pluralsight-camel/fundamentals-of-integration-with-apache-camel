import org.apache.camel.builder.RouteBuilder;

public class HelloWorldRouteProperty extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer:helloWorldTimer?period={{timer.period}}")
            .log("{{hello.message}} {{person.message}}!");

    }

}

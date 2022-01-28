// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;

public class TimerKameletRoute extends RouteBuilder {
  @Override
  public void configure() throws Exception {

      // Write your routes here, for example:
      from("kamelet:timer-pluralsight-source?frequency=5000&message=Hello")
        .to("log:INFO");

  }
}

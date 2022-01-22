# Build an image with the Camel HTTP Sink Connector already available as plug-in
FROM confluentinc/cp-kafka-connect-base:latest

EXPOSE 8083

COPY camel-http-sink-kafka-connector-1.0.0 /usr/share/camel-kafka-connectors/camel-http-sink-kafka-connector

ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/camel-kafka-connectors"

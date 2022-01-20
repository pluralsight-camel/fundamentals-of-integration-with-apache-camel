# Build an image with the Camel File Connector already available as plug-in
FROM confluentinc/cp-kafka-connect:latest

ARG CAMEL_REPO_URL="https://repo.maven.apache.org/maven2/org/apache/camel/kafkaconnector"
ARG CAMEL_FILE_COMP="camel-file-kafka-connector"
ARG CAMEL_FILE_COMP_V="1.0.0"

RUN cd "/tmp" \
    && wget -qO- "${CAMEL_REPO_URL}/${CAMEL_FILE_COMP}/${CAMEL_FILE_COMP_V}/${CAMEL_FILE_COMP}-${CAMEL_FILE_COMP_V}-package.tar.gz" \
        | tar -C "/usr/share/java" -xz

ENV CONNECT_PLUGIN_PATH="/usr/share/java"

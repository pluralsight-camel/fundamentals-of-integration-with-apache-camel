#!/bin/bash

/bin/connect-standalone.sh /kafka-connect/standalone-worker.properties /kafka-connect/FraudFileSourceConnector.json /kafka-connect/FraudFileSinkConnector.json

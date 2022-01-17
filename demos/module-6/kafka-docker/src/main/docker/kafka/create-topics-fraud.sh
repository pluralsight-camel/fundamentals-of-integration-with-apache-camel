#!/bin/bash

/bin/kafka-topics --bootstrap-server broker:29092 --topic customer-transactions --partitions 2 --replication-factor 1 --create

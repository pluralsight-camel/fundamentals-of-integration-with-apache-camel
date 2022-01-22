#!/bin/bash

/bin/kafka-topics --bootstrap-server broker1:29092,broker2:29093,broker3:29094 --topic customer-transactions --partitions 3 --replication-factor 3 --create

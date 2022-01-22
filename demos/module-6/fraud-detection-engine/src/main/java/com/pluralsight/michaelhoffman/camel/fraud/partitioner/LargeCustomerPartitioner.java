package com.pluralsight.michaelhoffman.camel.fraud.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * This is a custom partitioner that is used for edge cases where
 * a single key may have a high percentage of records created. In this example,
 * the customer account "Carved Rock" does the majority of business and will be
 * segregated to the last partition.
 */
@Component
public class LargeCustomerPartitioner implements Partitioner {

    public void configure(Map<String, ?> configs) {}

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        if (((String) key).equals("Carved Rock")) {
            return partitions.size() - 1;
        }
        return Math.abs(Utils.murmur2(keyBytes)) % (partitions.size() - 1);
    }

    public void close() {}
}

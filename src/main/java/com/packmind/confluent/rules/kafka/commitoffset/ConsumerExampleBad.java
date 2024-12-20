package com.packmind.confluent.rules.kafka.commitoffset;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class ConsumerExampleBad {
    public static void main(final String[] args) {

        final Properties props = new Properties();
        props.put("bootstrap.servers", "<BOOTSTRAP SERVERS>");
        // set other props
        // props.put(...);
        // props.put(...);

        Map<TopicPartition, OffsetAndMetadata> offsetToCommit = new HashMap<>();
        int messageCount = 0;
        long lastCommitTime = System.currentTimeMillis();

        final String topic = "purchases";

        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props)) {
            consumer.subscribe(Arrays.asList(topic));

            while (true) { // Infinite loop to keep polling and processing records
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100)); // Adjust poll duration as needed
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    // Do something with the record
                    doProcess(record);

                    // Commit offset after successful processing of the record
                    consumer.commitSync(Collections.singletonMap(new TopicPartition(record.topic(),
                            record.partition()), new OffsetAndMetadata(record.offset() + 1)));
                }
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e);
        }
    }
}

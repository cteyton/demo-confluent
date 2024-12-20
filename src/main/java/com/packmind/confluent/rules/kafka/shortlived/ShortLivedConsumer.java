package com.packmind.confluent.rules.kafka.shortlived;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ShortLivedConsumer {
    public static void main(String[] args) {
        String topicName = "example-topic";
        String groupId = "example-group";

        for (int i = 0; i < 1000; i++) {
            // Create a new consumer instance for each iteration (bad practice)
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

            Consumer<String, String> consumer = new KafkaConsumer<>(props);

            // Subscribe to the topic
            consumer.subscribe(Collections.singletonList(topicName));

            // Poll for new data
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                System.out.printf("Received record (key: %s, value: %s)%n", record.key(), record.value());
            });

            // Close the consumer (bad practice)
            consumer.close();
        }
    }
}

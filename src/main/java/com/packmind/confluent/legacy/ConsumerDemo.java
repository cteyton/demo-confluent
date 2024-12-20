package com.packmind.confluent.legacy;

import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.producer.ProducerConfig;

public class ConsumerDemo {
    
    public void shortLiveConsumers() {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        for (int i = 0; i < 1000; i++) {
            Consumer<String, String> _consumer = new ConsumerDemo<>(props);

            // Subscribe to the topic
            _consumer.subscribe(Collections.singletonList("topicName"));

            // Poll for new data
            ConsumerRecords<String, String> records = _consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                System.out.printf("Received record (key: %s, value: %s)%n", record.key(),
                        record.value());
            });

            // Close the consumer (bad practice)
            _consumer.close();
        }

    }

}

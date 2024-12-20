package com.packmind.confluent.rules.kafka.shortlived;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ShortLivedProducer {

    public static void main(String[] args) {
        String topicName = "example-topic";
        for (int i = 0; i < 1000; i++) {
            // Create a new producer instance for each new message (bad practice)
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            Producer<String, String> producer = new KafkaProducer<>(props);

            // Create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, "key-" + i, "value-" + i);

            // Send data to Kafka
            producer.send(record);

            // Close the producer (bad practice)
            producer.close();
        }
    }
}

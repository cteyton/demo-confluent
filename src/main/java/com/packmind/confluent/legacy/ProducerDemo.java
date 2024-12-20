package com.packmind.confluent.legacy;

import java.util.Properties;

public class ProducerDemo {

    public void shortLiveProducers() {
        String topicName = "mytopic";

        for (int i = 0; i < 1000; i++) {
            // Create a new producer instance for each message (bad practice)
            Producer<String, String> _producer = new KafkaProducer<>(new Properties());

            // Create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, "key-" + i,
                    "value-" + i);

            // Send data to Kafka
            _producer.send(record);

            // Close the producer (bad practice)
            _producer.close();
        }
    }

}

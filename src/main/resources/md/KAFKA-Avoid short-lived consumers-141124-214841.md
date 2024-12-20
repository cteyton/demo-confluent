
 ```markdown
# Avoid short-lived consumers

| Summary | Creating numerous short-lived Kafka consumers can lead to resource overhead and increased latency, it’s better to reuse existing consumers. |
|---------|------------------------------------------------------------------------------------------------------------------------------------------|
| Category | Core Kafka                                                                                                                              |
| Type     | Code                                                                                                                                    |
| Tags     | Performance, Efficiency                                                                                                                 |

## Description

Creating too many short-lived Kafka consumers can introduce several problems that developers should be aware of:

1. **Resource Overhead:** Each Kafka consumer maintains its own network connections, threads, and buffers. Continuously spinning up and tearing down these consumers uses significant CPU, memory, and network resources, potentially leading to resource exhaustion and slowing down your system. The broker also keeps tabs on Kafka client metadata, so it can lead to frequent GC pauses on the nodes themselves, not just the client.

2. **Increased Latency:** Starting a Kafka consumer isn't instant—it involves establishing connections to brokers, fetching metadata, and handling authentication if required. If your consumers are short-lived, this setup cost gets incurred repeatedly, increasing the overall latency for receiving messages.

3. **Network Throttling:** Each consumer needs to connect to the Kafka cluster, and a high churn of consumer connections can congest the network, reducing throughput and increasing message latency for other clients using the same network.

4. **Connection Limits:** Kafka brokers have limits on the number of active connections they can handle. Rapidly creating many consumers can exhaust these limits, leading to connection failures and preventing other clients from connecting.

5. **Broker Load Imbalance:** Creating many transient consumers can cause uneven load distribution across Kafka brokers, making the cluster's message handling less efficient.

6. **Garbage Collection Pressure:** Rapid creation and disposal of consumers lead to frequent memory allocations and deallocations, putting extra pressure on garbage collection. This can slow down your application and degrade JVM performance. See point 1 about generating GC pauses on the broker too.

7. **Unnecessary Network Overhead:** Every new consumer creates new connections to brokers, refreshes metadata, and performs SSL/TLS handshakes if security is enabled. This overhead can quickly add up when consumers are short-lived.

8. **Potential Security Bottlenecks:** If you're using secure connections, continuously creating consumers means frequent authentications (e.g., Kerberos), which can strain your authentication infrastructure and increase the risk of failures.

To avoid these issues, it’s generally better to reuse existing consumers instead of creating and destroying new ones. This approach will minimize overhead, reduce latency, and make communication with the Kafka cluster more efficient.

## Positive Examples

## Negative Examples

```java
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerExample {
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
```

## Detection Guidelines (Packmind)

Detect `new KafkaConsumer` within `for` or `while` loops.

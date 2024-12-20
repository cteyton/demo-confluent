## Avoid short-lived producers

| Summary  | Creating numerous short-lived Kafka producers can lead to resource overhead and increased latency, it’s better to reuse existing producers. |
|----------|-----------------------------------------------------------------------------------------------------------------------------------------|
| Category | Core Kafka                                                                                                                              |
| Type     | Code                                                                                                                                    |
| Tags     | Performance, Efficiency                                                                                                                 |

### Description

Creating too many short-lived Kafka producers can introduce several problems that developers should be aware of:

1. **Resource Overhead**: Each Kafka producer maintains its own network connections, threads, and buffers. Continuously spinning up and tearing down these producers uses significant CPU, memory, and network resources, potentially leading to resource exhaustion and slowing down your system. The broker keeps also tab on Kafka client metadata, so it can lead to frequent GC pauses on the nodes themselves, not just the client.

2. **Increased Latency**: Starting a Kafka producer isn’t instant—it involves establishing connections to brokers, fetching metadata, and handling authentication if required. If your producers are short-lived, this setup cost gets incurred repeatedly, increasing the overall latency for sending messages.

3. **Network Throttling**: Each producer needs to connect to the Kafka cluster, and a high churn of producer connections can congest the network, reducing throughput and increasing message latency for other clients using the same network.

4. **Connection Limits**: Kafka brokers have limits on the number of active connections they can handle. Rapidly creating many producers can exhaust these limits, leading to connection failures and preventing other clients from connecting.

5. **Broker Load Imbalance**: Creating many transient producers can cause uneven load distribution across Kafka brokers, making the cluster’s message handling less efficient.

6. **Garbage Collection Pressure**: Rapid creation and disposal of producers lead to frequent memory allocations and deallocations, putting extra pressure on garbage collection. This can slow down your application and degrade JVM performance. See point 1 about generating GC pauses on the broker too.

7. **Unnecessary Network Overhead**: Every new producer creates new connections to brokers, refreshes metadata, and performs SSL/TLS handshakes if security is enabled. This overhead can quickly add up when producers are short-lived.

8. **Potential Security Bottlenecks**: If you’re using secure connections, continuously creating producers means frequent authentications (e.g., Kerberos), which can strain your authentication infrastructure and increase the risk of failures.

To avoid these issues, it’s generally better to reuse existing producers or implement a producer pool instead of creating and destroying new ones. This approach will minimize overhead, reduce latency, and make communication with the Kafka cluster more efficient.

### Positive Examples

### Negative Examples

```java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaProducerExample {

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
```

## Detection Guidelines (Packmind)

Detect `new KafkaProducer` within `for` or `while` loops.

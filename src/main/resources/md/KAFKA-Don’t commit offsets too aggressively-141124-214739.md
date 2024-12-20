# Don’t commit offsets too aggressively

| Summary  | If manually committing, avoid overly aggressive commits to reduce broker load. |
|----------|------------------------------------------------------------------------------|
| Category | Core Kafka                                                                   |
| Type     | code                                                                         |
| Tags     | Performance, Efficiency                                                      |

## Description

When committing offsets manually with Apache Kafka, it means you are handling offset management instead of relying on automatic offset commits. This approach gives you more control over when offsets are considered "successfully processed."

Frequent commits can result in higher network overhead, which may negatively impact overall performance. To avoid this, consider committing offsets in batches instead of after processing every individual message. This approach improves throughput and reduces network calls to the Kafka broker. For example, you can track the highest offset processed and commit it periodically or after a set number of messages.

When working with multiple partitions, be aware of the offset for each partition and commit offsets accordingly.

If you feel this is getting too complex, consider using auto-commit instead. Make sure to read the detail of implementation as they vary across different technologies, e.g., Java clients vs librdkafka-based clients.

## Positive Examples

```java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConsumerExample {

    // Configurable batch size and time interval (in milliseconds) for committing offsets
    private static final int COMMIT_BATCH_SIZE = 10;
    private static final long COMMIT_INTERVAL_MS = 5000; // Commit every 5 seconds as a fallback

    public static void main(final String[] args) {

        final Properties props = new Properties();
        props.put("bootstrap.servers", "<BOOTSTRAP SERVERS>");
        // set other props
        // props.put(...)
        // props.put(...);

        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
        int messageCount = 0;
        long lastCommitTime = System.currentTimeMillis();
        
        final String topic = "purchases";
        
        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props)) {
            consumer.subscribe(Arrays.asList(topic));
            while (true) { // Infinite loop to keep polling and processing records
                ConsumerRecords<String, String> consumeRecords = consumer.poll(Duration.ofMillis(100)); // Adjust poll duration as needed
                for (ConsumerRecord<String, String> record : consumeRecords) {
        
                    // Do something with the record
                    doProcess(record);
        
                    // Track the latest offset for each partition
                    TopicPartition partition = new TopicPartition(record.topic(), record.partition());
                    offsetsToCommit.put(partition, new OffsetAndMetadata(record.offset() + 1));
                    messageCount++;
        
                    // Check if either the batch size is reached or the time interval has passed
                    long currentTime = System.currentTimeMillis();
                    if (messageCount >= COMMIT_BATCH_SIZE || (currentTime - lastCommitTime) >= COMMIT_INTERVAL_MS) {
                        consumer.commitSync(offsetsToCommit);
                        System.out.println("Committed offsets: " + offsetsToCommit);
        
                        // Reset counters
                        offsetsToCommit.clear();
                        messageCount = 0;
                        lastCommitTime = currentTime; // Update the last commit time
                    }
                }
            }
        
            // Final commit for any remaining offsets (optional, based on your needs)
            if (!offsetsToCommit.isEmpty()) {
                consumer.commitSync(offsetsToCommit);
                System.out.println("Final commit of offsets after processing batch: " + offsetsToCommit);
                offsetsToCommit.clear();
            }
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e);
        }
    }
}
```

**Negative Examples**

When offsets are committed for every message in the `poll` loop.

```java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

public class ConsumerExample {

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
                for (ConsumeRecord<String, String> record : consumerRecords) {
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
```

## References

- [https://docs.confluent.io/platform/current/clients/consumer.html#offset-management](https://docs.confluent.io/platform/current/clients/consumer.html#offset-management)

## Guidelines (Packmind)

A violation should be raised in the following cases:
* A for loop other a type "ConsumeRecords" is found in the code.
* Inside this for loop, a call to the method "commitSync" is executed. 
* There is no call to "partition()" and "new OffsetAndMetadata", that would suggest a batch strategy
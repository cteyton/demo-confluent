package com.packmind.confluent.rules.kafka.commitoffset;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConsumerExampleGood {
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

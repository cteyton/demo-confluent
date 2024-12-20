# Avoid the dual-write problem

| Summary | Applications performing updates to both a database and a messaging system (e.g., Apache Kafka) can risk data inconsistencies in failure scenarios |
|---------|----------------------------------------------------------------------------------------------------------------------------------------------|
| Category | Core Kafka                                                                                                                                    |
| Type     | Code                                                                                                                                          |
| Tags     | Correctness                                                                                                                                   |

## Description

When building event-driven microservices, a common challenge arises when you need to change the state of a service (like writing to a database) and then emit an event reflecting that change, say, to Kafka. The dual-write problem happens when these two actions — writing to the database and emitting an event — are not transactional, meaning there’s a risk of inconsistency if one succeeds while the other fails. If, for instance, the database write completes but the event emission fails, you end up with a state that’s out of sync with your events. This can lead to nasty bugs and data mismatches.

Let’s say you’re handling a bank transaction. You update a user’s account balance in the database and then emit an event to process a bill payment. If your app crashes after the balance update but before the event emission, the user’s money is withdrawn, but the bill remains unpaid. Swapping the order (emitting first, then updating state) just reverses the problem: the event might go out, but the state change never happens. Clearly, this isn’t a trivial issue.

"Just wrap it all in a transaction," you might think. But there’s a catch: databases and messaging platforms typically don’t support distributed transactions across both systems. If your event is emitted but the database transaction fails or rolls back, you’re still left with an inconsistent state.

So, how do you tackle this? You need to decouple the operations and ensure they work reliably together. Here are some strategies that can help:

1. **Change Data Capture (CDC):** A CDC system monitors changes in your database and triggers events based on those changes. Once the database update happens, the CDC tool will detect it and emit the corresponding event. This way, you eliminate the risk of emitting an event unless the state has already been changed.

2. **Transactional Outbox Pattern:** With this pattern, both the state change and a write to an “outbox” table (a log of events to be emitted) happen in a single transaction. Once the transaction commits, a separate process picks up pending events from the outbox and emits them to the messaging system. This guarantees consistency because if the database update is rolled back, the outbox entry is too.

3. **Event Sourcing:** Here, you don’t directly store state — you store a series of events that describe state changes. The current state can be reconstructed by replaying these events. New events are added to the log, and any necessary downstream actions (like emitting to Kafka) happen based on the log itself. This inherently ensures that emitted events and state remain in sync.

4. **Listen to Yourself Pattern:** This flips the usual flow. Instead of updating state and then emitting an event, you emit the event first. A separate service listens to this event and updates the state accordingly. It needs solid deduplication mechanisms, but it provides another path to decoupling.

Ignoring the dual-write issue can lead to subtle, hard-to-debug issues because these inconsistencies don’t throw errors; they silently break things. To build robust systems, you need to plan for failure and use a pattern that ensures consistency and reliability. This approach not only makes your architecture more resilient but also saves you countless hours of chasing bugs in production.

# Positive Examples

# Negative Examples

```java
import java.sql.SQLException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class DualWriteProblemExample {

    public static void main(String[] args) {
        // Database URL, username, and password
        String jdbcUrl = "jdbc:mysql://localhost:3306/mydatabase";
        String dbUser = "username";
        String dbPassword = "password";

        // Kafka properties
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            // Start a database transaction
            connection.setAutoCommit(false);

            // 1. Write operation to the SQL database
            String sqlInsert = "INSERT INTO mytable (id, data) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
                statement.setInt(1, 1);
                statement.setString(2, "some data");
                statement.executeUpdate();
    
                // Simulating a possible error during the database write
                // throw new SQLException("Database write failed");
    
                // 2. Publish an event to Kafka
                ProducerRecord<String, String> record = new ProducerRecord<>("my-topic", "key", "event data");
                RecordMetadata metadata = producer.send(record).get();
    
                // Simulating a possible error during the Kafka publish
                // throw new ExecutionException(new Throwable("Kafka publish failed"));
    
                // If both operations succeed, commit the transaction
                connection.commit();
                System.out.println("Both operations succeeded.");
            } catch (SQLException | ExecutionException e) {
                // If any operation fails, roll back the transaction
                connection.rollback();
                System.err.println("An error occurred, transaction rolled back: " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
```

### References

- [What is the Dual Write Problem? | Designing Event-Driven Microservices (YouTube video)](https://www.youtube.com/watch?v=dQw4w9WgXcQ)
- [https://www.confluent.io/blog/dual-write-problem/ (blog post)](https://www.confluent.io/blog/dual-write-problem/)
- [https://thorben-janssen.com/dual-writes/ (blog post)](https://thorben-janssen.com/dual-writes/)
- [https://www.linkedin.com/pulse/event-driven-architecture-complex-aspects-dual-write-momen-negm/ (blog post)](https://www.linkedin.com/pulse/event-driven-architecture-complex-aspects-dual-write-momen-negm/)
```
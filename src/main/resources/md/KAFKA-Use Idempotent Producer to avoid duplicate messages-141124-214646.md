# Use Idempotent Producer to avoid duplicate messages

| Summary  | Enable idempotence (enable.idempotence=true) to ensure exactly-once delivery semantics. |
|----------|----------------------------------------------------------------------------------------|
| Category | Kafka Client                                                                           |
| Type     | Configuration                                                                          |
| Tags     | Correctness                                                                            |

## Description

To avoid duplicate messages, Confluent recommends enabling idempotent producers. This is achieved by setting `enable.idempotence=true` in the producer configuration.

An idempotent producer assigns a sequence number to each batch of messages. Kafka brokers use this sequence number to detect and discard duplicate messages. This mechanism ensures that even if a producer retries sending a message due to a network error or other transient issue, the message is written to the topic only once. The sequence numbers are persisted, so even if the leader broker fails, the new leader can still detect duplicates.

Since [KAFKA-13673: disable idempotence when config conflicts](https://link.to/kafka-13673), which landed in Kafka 3.2 and was backported to 3.0 and 3.1, if `max.in.flight.requests > 5`, idempotence is implicitly disabled in the Java client. It will become an error in 4.0.

- **If you don't care about duplicates, but care about ordering**, you should configure:

  ```plaintext
  retries=Integer.MAX_VALUE
  delivery.timeout.ms=Integer.MAX_VALUE
  max.inflight.requests.per.connection=5
  ```

- **If you don't care about duplicates, but care about ordering**, you should configure:

  ```plaintext
  retries=Integer.MAX_VALUE
  delivery.timeout.ms=Integer.MAX_VALUE
  max.inflight.requests.per.connection=1
  ```

- **If you care about duplicates and ordering**, you should configure:

  ```plaintext
  retries=Integer.MAX_VALUE
  delivery.timeout.ms=Integer.MAX_VALUE
  enable.idempotence=true
  ```

## Positive examples

```plaintext
retries=Integer.MAX_VALUE
delivery.timeout.ms=Integer.MAX_VALUE
enable.idempotence=true
```

## Negative examples

```plaintext
retries=Integer.MAX_VALUE
delivery.timeout.ms=Integer.MAX_VALUE
max.inflight.requests.per.connection=1
```

```plaintext
retries=Integer.MAX_VALUE
delivery.timeout.ms=Integer.MAX_VALUE
max.inflight.requests.per.connection=5
```

## References

- [Confluent Developer pattern](https://developer.confluent.io/patterns/event-processing/idempotent-writer/)
- [ProducerConfig in Kafka 3.9](ProducerConfig in Kafka 3.9)
- [Confluent Support Portal](https://support.confluent.io/hc/en-us/articles/360060854672-Confluent-Platform-Producers-and-Consumers#producer_configs)

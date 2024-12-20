# Configure automatic retries

| Summary  | Retries help gracefully manage both major broker failures and routine maintenance by handling common transient errors. |
|----------|----------------------------------------------------------------------------------------------------------------------|
| Category | Kafka Client                                                                                                          |
| Type     | Configuration                                                                                                         |
| Tags     | Resilience                                                                                                            |

## Description

When executing producer.send(), the hope is that records go through and are successfully stored in a topic. The reality is that, for some reason or another, the producer request might fail. In certain cases, the failure is transient and retriable (i.e., the failure could be recovered from given a sufficient amount of time and the client retry of the request) whilst others will be permanent (i.e., something needs to be fixed before the request can succeed).

For example, during cluster rolling, some of the following retriable exceptions may be encountered by clients:

- UNKNOWN_TOPIC_OR_PARTITION
- LEADER_NOT_AVAILABLE
- NOT_LEADER_FOR_PARTITION
- NOT_ENOUGH_REPLICAS
- NOT_ENOUGH_REPLICAS_AFTER_APPEND

If retries and retry time are not configured properly, all of these exceptions will be logged as errors, which can potentially disrupt your client and result in lost messages.

We recommend setting retries to `max_int` (which happens to be the default starting in Apache Kafka version 2.2), which gives your client the best chance of gracefully handling such exceptions. Consider tuning `retry.backoff.ms` too.

It's recommended to use `delivery.timeout.ms` to control retry behavior, instead of retries (source: [KafkaProducer Javadoc](https://javadoc-link)).

Configure `retries` and `retry.backoff.ms` to handle transient errors. Idempotent producers (using `enable.idempotence=true`) require `acks=all`, `max.in.flight.requests.per.connection <= 5`, and `retries > 0`.

## Positive examples

Kafka client configuration:

```plaintext
retries=2147483647
```

```plaintext
# retries isn't set (it defaults to 2147483647)
```

## Negative examples

```plaintext
retries=0
```

## References

- [https://www.confluent.io/blog/5-common-pitfalls-when-using-apache-kafka/](https://www.confluent.io/blog/5-common-pitfalls-when-using-apache-kafka/)
- [INTERNAL] Introduction to Producer & Consumer Clients - Q124 (Slide Deck)

## Detection Guidelines (Packmind)

Detect this pattern both in properties file and Java file



# Configure Adaptive Partitioning for slow brokers

| Summary   | Configure Adaptive Partitioning to accommodate slow brokers |
|-----------|-------------------------------------------------------------|
| Category  | Core Kafka                                                  |
| Type      | Configuration                                               |
| Tags      | Performance                                                 |

## Description

[KIP-794](https://cwiki.apache.org/confluence/display/KAFKA/KIP-794%3A+Adaptive+Partitioning) enhanced the default partitioner to better handle brokers with different processing rates. The KIP guaranteed that a uniform amount of bytes are produced to a partition, regardless of broker performance, and added new configurations to control how the `KafkaProducer` produces records to partitions based on broker performance. The relevant configurations are named `partitioner.adaptive.partitioning.enable` and `partitioner.availability.timeout.ms`.

### partitioner.adaptive.partitioning.enable

When set to `true`, the producer will try to adapt to broker performance and produce more messages to partitions hosted on faster brokers. If `false`, producer will try to distribute messages uniformly. Note: this setting has no effect if a custom partitioner is used.

| Type         | boolean |
|--------------|---------|
| Default:     | `true`  |
| Valid Values:| `true` or `false`  |
| Importance:  | low     |

### partitioner.availability.timeout.ms

If a broker cannot process produce requests from a partition for `partitioner.availability.timeout.ms` time, the partitioner treats that partition as not available. If the value is 0, this logic is disabled. Note: this setting has no effect if a custom partitioner is used or `partitioner.adaptive.partitioning.enable` is set to `false`.

| Type         | long |
|--------------|------|
| Default:     | `0`  |
| Valid Values:| [0,...] |
| Importance:  | low  |

The default partitioner does not effectively route around significant broker slowdowns with the default configuration. The `partitioner.adaptive.partitioning.enable` concept selects the next partition with a probability inversely proportional to the partition queue size. The partitioner continues to select slow partitions until a significant partition queue builds up, and this significant queue build up leads to high latency for record delivery.

The `partitioner.availability.timeout.ms` configuration can mitigate the impact of significant broker slowdowns. Determining an effective value requires manual testing and it is disabled by default so many clients likely do not use the functionality.

## Positive Examples

## Negative Examples
```plaintext
partitioner.adaptive.partitioning.enable=false
```

```plaintext
partitioner.availability.timeout.ms=0
// or not set at all, default being 0
```

## References

- [https://www.confluent.io/blog/kafka-producer-internals-preparing-event-data/](https://www.confluent.io/blog/kafka-producer-internals-preparing-event-data/)
- [https://docs.confluent.io/platform/current/installation/configuration/producer-configs.html](https://docs.confluent.io/platform/current/installation/configuration/producer-configs.html)
- [INTERNAL] Adaptive Partitioning Power of N Choices

## Detection Guidelines (Packmind)

```
partitioner.adaptive.partitioning.enable=false
partitioner.availability.timeout.ms=0
```

Or missing `partitioner.availability.timeout.ms` configuration if `partitioner.adaptive.partitioning.enable` is enabled.

```
partitioner.adaptive.partitioning.enable=false
```
